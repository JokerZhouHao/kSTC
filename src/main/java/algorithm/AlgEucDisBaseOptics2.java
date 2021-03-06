package algorithm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import entity.CellSign;
import entity.Circle;
import entity.Cluster;
import entity.Node;
import entity.NodeNeighbors;
import entity.NoiseRecoder;
import entity.PNodeCollection;
import entity.QueryParams;
import entity.SGPLInfo;
import entity.SortedClusters;
import entity.fastrange.Cellid2Nodes;
import entity.fastrange.NgbNodes;
import entity.optics.OrderSeeds;
import entity.optics.SteepArea;
import index.CellidPidWordsIndex;
import precomputation.dataset.file.FileLoader;
import precomputation.dataset.file.OrginalFileWriter;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.IOUtility;

/**
 * the distance of the base optics is caculated by Euclidean Distance
 * @author ZhouHao
 * @since 2018年12月4日
 */
public class AlgEucDisBaseOptics2 implements AlgInterface{
	
	protected Point[] allLocations = null;
	protected CellidPidWordsIndex cellidWIndex = null;
	protected NoiseRecoder noiseRecoder = new NoiseRecoder();
	protected SGPLInfo sgplInfo = null;
//	protected Circle sCircle = new Circle(0.0, new double[2]);
	protected Circle sCircle = null;
	protected SteepArea tSteepArea = new SteepArea();
	protected QueryParams qp = null;
	
//	public AlgEucDisBaseOptics() throws Exception{
//		allLocations = FileLoader.loadPoints(Global.pathIdNormCoord);
////		allLocations = FileLoader.loadPoints(Global.pathIdCoord);
////		cellidWIndex = new CellidPidWordsIndex(Global.pathCellidPidWordsIndex);
//		
//		cellidWIndex = new CellidPidWordsIndex(Global.pathCellidRtreeidOrPidWordsIndex);
//		
//		cellidWIndex.openIndexReader();
//	}
	
	
	public AlgEucDisBaseOptics2(QueryParams qp) throws Exception {
		this.sgplInfo = qp.sgplInfo;
		sCircle = new Circle(0.0, new double[2], sgplInfo);
		this.qp = qp;
		init();
	}
	
	private void init() throws Exception{
		if(Global.allLocations == null)	allLocations = FileLoader.loadPoints(Global.pathIdNormCoord);
		else allLocations = Global.allLocations;
		
		String path =  Global.getPathCellidRtreeidOrPidWordsIndex(qp.rtreeFanout, qp.h);
		cellidWIndex = new CellidPidWordsIndex(path);
		cellidWIndex.openIndexReader();
	}
	
	public void free() throws Exception{
		cellidWIndex.close();
	}
	
	@Override
	public SortedClusters excuteQuery(QueryParams qParams) throws Exception{
		return excuteQuery(qParams, null);
	}
	
	/**
	 * excute query
	 * @param qParams
	 * @return
	 * @throws Exception
	 */
	public SortedClusters excuteQuery(QueryParams qParams, String pathOrderedFile) throws Exception{
		if(qParams.sWords.isEmpty())	return null;
		
		qp.runTimeRec.timeTotal = System.nanoTime();
		
		// 采用lucene分词产生的wid_terms.txt([-125.0, 28.0], [15.0, 60]文件全部是小写，故输入的查询关键词得先转化为小写
		List<String> tWs = new ArrayList<>();
		for(String w : qParams.sWords)	tWs.add(w.toLowerCase());
		qParams.sWords = tWs;
		
		sCircle.radius = qParams.xi;
		
		qp.runTimeRec.setFrontTime();
		Cellid2Nodes cid2nds = cellidWIndex.searchWordsReCellid2Nodes(qParams, allLocations);
		if(null == cid2nds) {
			qp.runTimeRec.timeTotal = 0;
			qp.runTimeRec.timeTotalPrepareData = 0;
			return null;
		}
		qp.runTimeRec.numNid = cid2nds.numNode();
		qp.runTimeRec.numCellid = cid2nds.numCell();
		qp.runTimeRec.timeSearchTerms = qp.runTimeRec.getTimeSpan();
		
		qp.runTimeRec.timeOpticFunc = System.nanoTime();
		List<Node> sortedNodes = optics(cid2nds.cellid2Nodes, qParams, pathOrderedFile);
		qp.runTimeRec.timeOpticFunc = System.nanoTime() - qp.runTimeRec.timeOpticFunc;
		
		qp.runTimeRec.timeExcuteQueryFunc = System.nanoTime();
		SortedClusters sc = excuteQueryByWu(qParams, pathOrderedFile, cid2nds.pNodes, sortedNodes);
		qp.runTimeRec.timeExcuteQueryFunc = System.nanoTime() - qp.runTimeRec.timeExcuteQueryFunc;
		
		qp.runTimeRec.timeTotalPrepareData = qp.runTimeRec.timeSearchTerms + qp.runTimeRec.timeSortByDistance + 
											qp.runTimeRec.timeSortByScore;
		
		qp.runTimeRec.timeTotal = System.nanoTime() - qp.runTimeRec.timeTotal;
		
		if(null == sc) qp.runTimeRec.numCluster = 0;
		else qp.runTimeRec.numCluster = sc.getSize();
		
		if(null==sc)	qp.runTimeRec.topKScore = 0;
		else qp.runTimeRec.topKScore = sc.getLastScore();
		
		return sc;
	}
	
	/**
	 * excuteQuery
	 * @param qParams
	 * @param pathOrderedFile
	 * @param cellid2Nodes
	 * @param sortedNodes
	 * @return
	 * @throws Exception
	 */
	public SortedClusters excuteQuery(QueryParams qParams, String pathOrderedFile, Map<Integer, List<Node>> cellid2Nodes,
			List<Node> sortedNodes) throws Exception{
		/* building sort distance node collection and sort score node collection */
		List<Node> nodes = new ArrayList<>();
		for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
			nodes.addAll(en.getValue());
		}
		
		qp.runTimeRec.setFrontTime();
		PNodeCollection disPNodeCol = new PNodeCollection(nodes).sortByDistance();
		qp.runTimeRec.timeSortByDistance = qp.runTimeRec.getTimeSpan();
		
		qp.runTimeRec.setFrontTime();
		PNodeCollection scorePNodeCol = new PNodeCollection(nodes).sortByScore();
		qp.runTimeRec.timeSortByScore = qp.runTimeRec.getTimeSpan();

		SortedClusters sClusters = new SortedClusters(qParams);
		Cluster cluster = null;
		int curClusterId = 1;
		
		double bound = Double.MIN_VALUE;
		List<SteepArea> downAreas = new ArrayList<>();
		int index = 0;
		double mib = 0.0;
		int size = sortedNodes.size();
		SteepArea steepArea = null;
		int i = 0;
		SteepArea upArea = null;
		
		while(index + 1< size) {
			if(sortedNodes.get(index).reachabilityDistance * qp.steepOppositeDegree >= sortedNodes.get(index + 1).reachabilityDistance) {
				// may be is start point of down area
				steepArea = getDownArea(sortedNodes, qParams, index);
				if(steepArea.isNormalArea()) {
					mib = Math.max(mib, steepArea.lastMib);
					index = steepArea.lastEnd;
				} else {	// is down area
					filterDownAreas(sortedNodes, downAreas, mib, null);
					downAreas.add(steepArea.copy());
					index = steepArea.lastEnd;
					mib = steepArea.lastMib;
				}
			} else if (sortedNodes.get(index).reachabilityDistance > sortedNodes.get(index + 1).reachabilityDistance * qp.steepOppositeDegree) {
				// is normal point
				mib = Math.max(mib, sortedNodes.get(index).reachabilityDistance);
				index++;
			} else {
				// may be is start point of up area
				upArea = getUpArea(sortedNodes, qParams, index);
				if(upArea.isNormalArea()) {
					mib = Math.max(mib, upArea.lastMib);
					index = upArea.lastEnd;
				} else {	// is up area
					filterDownAreas(sortedNodes, downAreas, mib, upArea);
					
					cluster = null;
					for(i = downAreas.size() - 1; i >= 0; i--) {
						steepArea = downAreas.get(i);
						if(steepArea.isStop())	break;
						else if(steepArea.noUsed()) {
							cluster = getCluster(curClusterId, qParams, sortedNodes, steepArea, upArea);
							if(null != cluster) {
								downAreas.get(downAreas.size() - 1).status = SteepArea.STATUSSTOP;
								sClusters.add(cluster);
								curClusterId++;
								// compare to threshold
								disPNodeCol.refreshFirstIndex();
								scorePNodeCol.refreshFirstIndex();
								bound = disPNodeCol.getFirstNoUsedDis() * qp.alpha + scorePNodeCol.getFirstNoUsedScore() * (1 - qp.alpha);
								if(bound >= sClusters.getTopKScore())	return sClusters;
								break;
							}
						}
					}
					
					index = upArea.lastEnd;
					mib = upArea.lastMib;
				}
			}
		}
		if(sClusters.getSize() == 0)	return null;
		return sClusters;
	}
	
	
	public List<Node> optics(Map<Integer, List<Node>> cellid2Nodes, QueryParams qParams, String pathOrderedFile) throws Exception{
		List<Node> orderedNodes = new ArrayList<>();
		OrginalFileWriter ofw = null;
		if(null!=pathOrderedFile) {
			ofw = new OrginalFileWriter(pathOrderedFile);
		}
		
		for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
			for(Node nd : en.getValue()) {
				if(!nd.isProcessed) {
					expandClusterOrder(cellid2Nodes, nd, qParams, orderedNodes, ofw);
					qp.runTimeRec.numExpandClusterOrder++;
				}
			}
		}
		if(null!=pathOrderedFile) {
			ofw.close();
		}
		return orderedNodes;
	}
	
	public void expandClusterOrder(Map<Integer, List<Node>> cellid2Nodes, Node centerNode, QueryParams qParams, List<Node> orderedNodes, OrginalFileWriter ofw) throws Exception{
		qp.runTimeRec.setFrontTime();
		List<Node> neighbors = fastRange(cellid2Nodes, qParams, centerNode);
		
		qp.runTimeRec.numOpticFastRange++;
		qp.runTimeRec.timeOpticFastRange += qp.runTimeRec.getTimeSpan();
		
		centerNode.isProcessed = Boolean.TRUE;
		centerNode.reachabilityDistance = Node.UNDEFINED;
//		centerNode.setCoreDistance(qParams, neighbors);
		centerNode.setCoreDistanceBySorted(qParams, neighbors);
		
		if(null != ofw)	ofw.writeIdCoreAndDirectDis(centerNode.id, centerNode.coreDistance, centerNode.reachabilityDistance); 
		orderedNodes.add(centerNode);
		OrderSeeds orderSeeds = new OrderSeeds();
		if(centerNode.coreDistance != Node.UNDEFINED) {
			orderSeeds.update(neighbors, centerNode);
			while(!orderSeeds.isEmpty()) {
				centerNode = orderSeeds.pollFirst();
				
				qp.runTimeRec.setFrontTime();
				neighbors = fastRange(cellid2Nodes, qParams, centerNode);
				
				qp.runTimeRec.numOpticFastRange++;
				qp.runTimeRec.timeOpticFastRange += qp.runTimeRec.getTimeSpan();
				
				centerNode.isProcessed = Boolean.TRUE;
//				centerNode.setCoreDistance(qParams, neighbors);
				centerNode.setCoreDistanceBySorted(qParams, neighbors);
				
				
				if(null != ofw)	ofw.writeIdCoreAndDirectDis(centerNode.id, centerNode.coreDistance, centerNode.reachabilityDistance);
				orderedNodes.add(centerNode);
				if(centerNode.coreDistance != Node.UNDEFINED) {
					orderSeeds.update(neighbors, centerNode);
				}
			}
		}
	}
	
	public static List<Node> fastRange(Map<Integer, List<Node>> cellid2Nodes, Circle sCircle, QueryParams qParams, 
			Node centerNode, SGPLInfo sgplInfo) throws Exception{
		sCircle.center = centerNode.location.m_pCoords;
		List<CellSign> coveredCellids = sgplInfo.cover(sCircle);
		List<Node> cellNodes = null;
		NgbNodes ngb = new NgbNodes(Boolean.TRUE);
		double dis = 0.0;
		
		for(CellSign cs : coveredCellids) {
			if(null == (cellNodes = cellid2Nodes.get(cs.getId())))	continue;
			for(Node nd : cellNodes) {
				dis = centerNode.location.getMinimumDistance(nd.location);
				if(dis <= sCircle.radius) {
					nd.disToCenter = dis;
					ngb.add(dis, nd);
				}
			}
		}
		
		
		
//		System.out.println(centerNode.id + " : " + ngb.toList().size());
		
		
		
		return ngb.toList();
	}
	
	public List<Node> fastRange(Map<Integer, List<Node>> cellid2Nodes,QueryParams qParams, 
			Node centerNode) throws Exception{
		return fastRange(cellid2Nodes, sCircle, qParams, centerNode, sgplInfo); 
	}
	
	public SteepArea getDownArea(List<Node> sortedNodes, QueryParams qParams, int start) {
		int end = start;
		int tIndex = start + 1;
		int curPointNum = 0;
		while(tIndex + 1 < sortedNodes.size()) {
			if(sortedNodes.get(tIndex).reachabilityDistance * qp.steepOppositeDegree >= sortedNodes.get(tIndex + 1).reachabilityDistance) {
				end = tIndex;
				curPointNum = 0;
			} else if(sortedNodes.get(tIndex).reachabilityDistance >= sortedNodes.get(tIndex + 1).reachabilityDistance) {
				if(qParams.minpts <= (++curPointNum))	break;
			} else break;
			tIndex++;
		}
		if(start==end)	return tSteepArea.set(SteepArea.TYPENORMAL, 0, 0, 0, SteepArea.STATUSNOUSED, tIndex, sortedNodes.get(start).reachabilityDistance);
		else return tSteepArea.set(SteepArea.TYPEDOWNAREA, start, end, sortedNodes.get(start + 1).reachabilityDistance, SteepArea.STATUSNOUSED, tIndex, sortedNodes.get(end + 1).reachabilityDistance);
	}
	
	public SteepArea getUpArea(List<Node> sortedNodes, QueryParams qParams, int start) {
		int end = start;
		int tIndex = start + 1;
		int curPointNum = 0;
		while(tIndex + 1 < sortedNodes.size()) {
			if(sortedNodes.get(tIndex).reachabilityDistance <= sortedNodes.get(tIndex + 1).reachabilityDistance * qp.steepOppositeDegree) {
				end = tIndex;
				curPointNum = 0;
			} else if(sortedNodes.get(tIndex).reachabilityDistance <= sortedNodes.get(tIndex + 1).reachabilityDistance) {
				if(qParams.minpts <= (++curPointNum))	break;
			} else break;
			tIndex++;
		}
		if(start==end)	return tSteepArea.set(SteepArea.TYPENORMAL, 0, 0, 0, SteepArea.STATUSNOUSED, tIndex, sortedNodes.get(tIndex).reachabilityDistance);
		else return tSteepArea.set(SteepArea.TYPEUPAREA, start, end, sortedNodes.get(end - 1).reachabilityDistance, SteepArea.STATUSNOUSED, tIndex, sortedNodes.get(tIndex).reachabilityDistance);
	}
	
	public Cluster getCluster(int clusterId, QueryParams qParams, List<Node> sortedNodes, SteepArea downArea, SteepArea upArea) {
		if(downArea.mib > sortedNodes.get(upArea.end).reachabilityDistance * qp.steepOppositeDegree ||
			upArea.end - downArea.start + 1 < qParams.minpts) {
			downArea.mib = Math.max(downArea.mib, sortedNodes.get(upArea.end).reachabilityDistance);
			return null;
		} else {
			double tDou = sortedNodes.get(upArea.end + 1).reachabilityDistance;
			int i = 0;
			if(sortedNodes.get(downArea.start).reachabilityDistance * qp.steepOppositeDegree >= tDou) {
				for(i=downArea.start + 1; i <= downArea.end; i++) {
					if(sortedNodes.get(i).reachabilityDistance <= tDou)	break;
				}
				if(i > downArea.end || upArea.end - i + 1 < qParams.minpts) {
					downArea.mib = Math.max(downArea.mib, sortedNodes.get(upArea.end).reachabilityDistance);
					return null;
				}
				List<Node> nds = new ArrayList<>();
				for(; i <= upArea.end; i++) {
					sortedNodes.get(i).isUsed = Boolean.TRUE;
					sortedNodes.get(i).orderId = i;
					nds.add(sortedNodes.get(i));
				}
				return new Cluster(clusterId, nds, qp);
			}
			
			tDou = sortedNodes.get(downArea.start).reachabilityDistance;
			if(sortedNodes.get(upArea.end + 1).reachabilityDistance * qp.steepOppositeDegree >= tDou) {
				for(i=upArea.end; i>= upArea.start; i--) {
					if(sortedNodes.get(i).reachabilityDistance <= tDou)	break;
				}
				if(i < upArea.start || i - downArea.start + 1 < qParams.minpts) {
					downArea.mib = Math.max(downArea.mib, sortedNodes.get(upArea.end).reachabilityDistance);
					return null;
				}
				int end = i;
				List<Node> nds = new ArrayList<>();
				for(i = downArea.start; i <= end; i++) {
					sortedNodes.get(i).isUsed = Boolean.TRUE;
					sortedNodes.get(i).orderId = i;
					nds.add(sortedNodes.get(i));
				}
				return new Cluster(clusterId, nds, qp);
			}
			List<Node> nds = new ArrayList<>();
			if(sortedNodes.get(downArea.start).reachabilityDistance == Node.UNDEFINED)	i = downArea.start + 1;
			else i = downArea.start;
			for(; i <= upArea.end; i++) {
				sortedNodes.get(i).isUsed = Boolean.TRUE;
				sortedNodes.get(i).orderId = i;
				nds.add(sortedNodes.get(i));
			}
			return new Cluster(clusterId, nds, qp);
		}
		
	}
	
	public void filterDownAreas(List<Node> sortedNodes, List<SteepArea> downAreas, double curMib, SteepArea steepArea) {
		if(null != steepArea)	// is a up area
			curMib = Math.max(curMib, steepArea.mib);
		int i = downAreas.size() - 1;
		for(; i>=0; i--) {
			steepArea = downAreas.get(i);
			if(steepArea.isStop())	break;
			else if(steepArea.noUsed()) {
				if(sortedNodes.get(steepArea.start).reachabilityDistance * qp.steepOppositeDegree < curMib)	steepArea.status = SteepArea.STATUSHASUSED;
				else	steepArea.mib = Math.max(steepArea.mib, curMib);
			}
		}
	}
	
	/**********************	以下全部是采用吴老师提出的方法来计算cluster的相关函数	******************/
	public SortedClusters excuteQueryByWu(QueryParams qParams, String pathOrderedFile, List<Node> nodes,
			List<Node> sortedNodes) throws Exception{
	
		qp.runTimeRec.setFrontTime();
		PNodeCollection disPNodeCol = new PNodeCollection(nodes).sortByDistance();
		qp.runTimeRec.timeSortByDistance = qp.runTimeRec.getTimeSpan();
		
		qp.runTimeRec.setFrontTime();
		PNodeCollection scorePNodeCol = new PNodeCollection(nodes).sortByScore();
		qp.runTimeRec.timeSortByScore = qp.runTimeRec.getTimeSpan();
		
		SortedClusters sClusters = new SortedClusters(qParams);
		Cluster cluster = null;
		int curClusterId = 1;
		
		double bound = Double.MIN_VALUE;
		List<SteepArea> downAreas = new ArrayList<>();
		int index = 0;
		double mib = 0.0;
		int size = sortedNodes.size();
		SteepArea steepArea = null;
		int i = 0;
		SteepArea upArea = null;
		
		while(index + 1< size) {
			if(sortedNodes.get(index).reachabilityDistance * qp.steepOppositeDegree >= sortedNodes.get(index + 1).reachabilityDistance) {
				// may be is start point of down area
				steepArea = getDownArea(sortedNodes, qParams, index);
				if(steepArea.isNormalArea()) {
					mib = Math.max(mib, steepArea.lastMib);
					index = steepArea.lastEnd;
				} else {	// is down area
//					filterDownAreas(sortedNodes, downAreas, mib, null);
					downAreas.add(steepArea.copy());
					index = steepArea.lastEnd;
					mib = steepArea.lastMib;
				}
			} else if (sortedNodes.get(index).reachabilityDistance > sortedNodes.get(index + 1).reachabilityDistance * qp.steepOppositeDegree) {
				// is normal point
				mib = Math.max(mib, sortedNodes.get(index).reachabilityDistance);
				index++;
			} else {
				// may be is start point of up area
				upArea = getUpArea(sortedNodes, qParams, index);
				if(upArea.isNormalArea()) {
					mib = Math.max(mib, upArea.lastMib);
					index = upArea.lastEnd;
				} else {	// is up area
//					filterDownAreas(sortedNodes, downAreas, mib, upArea);
					cluster = null;
					for(i = downAreas.size() - 1; i >= 0; i--) {
						steepArea = downAreas.get(i);
						if(steepArea.isStop())	break;
						else if(steepArea.noUsed()) {
							cluster = getClusterByWu(curClusterId, qParams, sortedNodes, steepArea, upArea);
							if(null != cluster) {
								downAreas.get(downAreas.size() - 1).status = SteepArea.STATUSSTOP;
								sClusters.add(cluster);
								curClusterId++;
								// compare to threshold
								disPNodeCol.refreshFirstIndex();
								scorePNodeCol.refreshFirstIndex();
								bound = disPNodeCol.getFirstNoUsedDis() * qp.alpha + scorePNodeCol.getFirstNoUsedScore() * (1 - qp.alpha);
								if(bound >= sClusters.getTopKScore())	return sClusters;
								break;
							}
						}
					}
					
					index = upArea.lastEnd;
					mib = upArea.lastMib;
				}
			}
		}
		if(sClusters.getSize() == 0)	return null;
		return sClusters;
	}
	
	public Cluster getClusterByWu(int clusterId, QueryParams qParams, List<Node> sortedNodes, SteepArea downArea, SteepArea upArea) {
		if(upArea.end - downArea.start + 1 < qParams.minpts) {
			return null;
		} else {
			double tDou = Math.min(sortedNodes.get(downArea.start).reachabilityDistance, sortedNodes.get(upArea.end + 1).reachabilityDistance) 
							* qp.steepOppositeDegree;
			int i = 0;
			for(i=downArea.start; i <= upArea.end; i++) {
				if(sortedNodes.get(i).reachabilityDistance <= tDou)	break;
			}
			int start = i;
			for(++i; i<=upArea.end; i++) {
				if(sortedNodes.get(i).reachabilityDistance > tDou)	break;
			}
			int end = i - 1;
			if(end - start + 1 < qParams.minpts)	return null;
			List<Node> nds = new ArrayList<>();
			for(i=start; i <= end; i++) {
				sortedNodes.get(i).isUsed = Boolean.TRUE;
				sortedNodes.get(i).orderId = i;
				nds.add(sortedNodes.get(i));
			}
			return new Cluster(clusterId, nds, qp);
		}
		
	}
	
}