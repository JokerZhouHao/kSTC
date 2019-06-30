package algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import entity.Cell;
import entity.CellSign;
import entity.Circle;
import entity.Cluster;
import entity.Node;
import entity.NodeCollection;
import entity.NodeNeighbors;
import entity.NoiseRecoder;
import entity.PNodeCollection;
import entity.QueryParams;
import entity.SGPLInfo;
import entity.SortedClusters;
import index.CellidPidWordsIndex;
import index.IdWordsIndex;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.index.rtree.MRTree;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * the distance of the algorithm is caculated by Euclidean Distance
 * @author ZhouHao
 * @since 2018年11月16日
 */
public class AlgEucDisBase implements AlgInterface{
	
	private Point[] allLocations = null;
//	private IdWordsIndex idWordsIndex = null;
	private CellidPidWordsIndex cellidIndex = null;
	private MRTree rtree = null;
	private NoiseRecoder noiseRecoder = new NoiseRecoder();
	private QueryParams qp = null;
	
	private SGPLInfo sgplInfo = null;
	private Circle sCircle = null;
	private Map<Integer, Integer> tempClusteredCells = null;
	
//	public AlgEucDisBase() throws Exception{
//		allLocations = FileLoader.loadPoints(Global.pathIdNormCoord);
////		allLocations = FileLoader.loadPoints(Global.pathIdCoord);
////		idWordsIndex = new IdWordsIndex(Global.pathPidAndRtreeIdWordsIndex);
////		idWordsIndex.openIndexReader();
//		
//		cellidIndex = new CellidPidWordsIndex(Global.pathCellidRtreeidOrPidWordsIndex);
//		cellidIndex.openIndexReader();
//		
//		rtree = MRTree.getInstanceInDisk();
//	}
	
	
	public AlgEucDisBase(QueryParams qp) throws Exception {
		this.qp = qp;
		if(qp.type > 1) {
			this.sgplInfo = qp.sgplInfo;
			this.sCircle = new Circle(0.0, new double[2], sgplInfo);
		}
		if(qp.type == 4) {
			this.tempClusteredCells = new HashMap<>();
		}
		init();
	}
	
	private void init() throws Exception{
		if(Global.allLocations == null)	allLocations = FileLoader.loadPoints(Global.pathIdNormCoord);
		else allLocations = Global.allLocations;
		
		String path =  Global.getPathCellidRtreeidOrPidWordsIndex(qp.rtreeFanout, qp.h);
		cellidIndex = new CellidPidWordsIndex(path);
		cellidIndex.openIndexReader();
		
		rtree = MRTree.getInstanceInDisk(Boolean.FALSE);
	}
	
	public void free() throws Exception{
		cellidIndex.close();
	}
	
	/**
	 * excute query
	 * @param qParams
	 * @return
	 * @throws Exception
	 */
	public SortedClusters excuteQuery(QueryParams qParams) throws Exception{
		if(qParams.sWords.isEmpty())	return null;
		
		qp.runTimeRec.timeTotal = System.nanoTime();
		qp.runTimeRec.timeTotalPrepareData = System.nanoTime();
		
		// 采用lucene分词产生的wid_terms.txt([-125.0, 28.0], [15.0, 60]文件全部是小写，故输入的查询关键词得先转化为小写
		List<String> tWs = new ArrayList<>();
		for(String w : qParams.sWords)	tWs.add(w.toLowerCase());
		qParams.sWords = tWs;
		
//		NodeCollection nodeCol = idWordsIndex.searchWords(qParams, allLocations);
		
		qp.runTimeRec.setFrontTime();
		NodeCollection nodeCol = cellidIndex.searchWordsReNodeCol(qParams, allLocations);
		if(nodeCol == null) {
			qp.runTimeRec.timeTotal = 0;
			qp.runTimeRec.timeTotalPrepareData = 0;
			return null;
		}
		qp.runTimeRec.numNid = nodeCol.size();
		qp.runTimeRec.timeSearchTerms = qp.runTimeRec.getTimeSpan();
		
		qp.runTimeRec.setFrontTime();
		PNodeCollection disPNodeCol = nodeCol.getPNodeCollection().sortByDistance();
		qp.runTimeRec.timeSortByDistance = qp.runTimeRec.getTimeSpan();
		
		qp.runTimeRec.setFrontTime();
		PNodeCollection scorePNodeCol = nodeCol.getPNodeCollection().copy().sortByScore();
		qp.runTimeRec.timeSortByScore = qp.runTimeRec.getTimeSpan();
		
		qp.runTimeRec.timeTotalPrepareData = System.nanoTime() - qp.runTimeRec.timeTotalPrepareData;
		
		qp.runTimeRec.timeTotalGetCluster = System.nanoTime();
		
		SortedClusters sClusters = new SortedClusters(qParams);
		Cluster cluster = null;
		int curClusterId = 1;
		
		double[] disAndSco = null;
		
		double topKScore = Double.MAX_VALUE;
		double bound = Double.MIN_VALUE;
		Node curNode = null;
		
		Boolean signAccessDis = Boolean.TRUE;
		
		// 针对adv1、adv2添加的参数
		Map<Integer, List<Node>> cellid2Nodes = null;
		Map<Integer, Integer> clusteredCells = null;
		if(qp.type > 1) {
			if(qp.type == 2 || qp.type == 3) {
				cellid2Nodes = nodeCol.toCellid2Nodes();
			}
			sCircle.radius = qParams.epsilon; // 设置半径
			clusteredCells = new HashMap<>();
		}
		
		
		do {
			if(signAccessDis) {
				curNode = disPNodeCol.next();
			} else {
				curNode = scorePNodeCol.next();
			}
			
			if(curNode == null)	break;	// all nodes are accessed
			else if(curNode.isNoise()) {
				if(signAccessDis)	noiseRecoder.addDisNoise(new NodeNeighbors(curNode, curNode.neighbors));
				else noiseRecoder.addScoNoise(new NodeNeighbors(curNode, curNode.neighbors));
				continue;
			}
			cluster = getCluster(qParams, curClusterId, curNode, nodeCol, signAccessDis, cellid2Nodes, clusteredCells);
			qp.runTimeRec.numGetCluster++;
			if(null != cluster) {
				sClusters.add(cluster);
				curClusterId++;
			}
			topKScore = sClusters.getTopKScore();
			
			disAndSco = noiseRecoder.getMinDisAndSco();
			disAndSco[0] = disAndSco[0] <= disPNodeCol.first(1).distance ? disAndSco[0]:disPNodeCol.first(1).distance;
			disAndSco[1] = disAndSco[1] <= scorePNodeCol.first(1).score ? disAndSco[1]:scorePNodeCol.first(1).score;
			
			bound = qp.alpha * disAndSco[0] + (1 - qp.alpha) * disAndSco[1];
			
			signAccessDis = !signAccessDis;
		} while(bound <= topKScore);
		noiseRecoder.clear();
		
		qp.runTimeRec.numCluster = sClusters.getSize();
		qp.runTimeRec.timeTotalGetCluster = System.nanoTime() - qp.runTimeRec.timeTotalGetCluster;
		qp.runTimeRec.timeTotal = System.nanoTime() - qp.runTimeRec.timeTotal;
		if(0 == sClusters.getSize())	qp.runTimeRec.topKScore = 0;
		else qp.runTimeRec.topKScore = sClusters.getLastScore();
		return sClusters;
	}
	
	private List<CellSign> cellHasNode(Map<Integer, List<Node>> cellid2Nodes, List<CellSign> coveredCellids){
		List<CellSign> cells = new ArrayList<>();
		for(CellSign cs : coveredCellids) {
			if(!cellid2Nodes.containsKey(cs.getId()))	continue;
			else cells.add(cs);
		}
		if(cells.isEmpty())	return null;
		else return cells;
	}
	
	
	/**
	 * adv1 skip rule
	 * @param cellid2Nodes
	 * @param clusteredCells
	 * @param coveredCellids
	 * @return
	 */
	private Boolean adv1(Map<Integer, List<Node>> cellid2Nodes, Map<Integer, Integer> clusteredCells, 
							List<CellSign> availableCellids) {
		Boolean canSkip = Boolean.TRUE;
		for(CellSign cs : availableCellids) {
			if(!clusteredCells.containsKey(cs.getId())) {
				canSkip = Boolean.FALSE;
				break;
			}
		}
			
//		System.out.println("canSkip: " + canSkip);
		
		return canSkip;
	}
	
	/**
	 * adv2
	 * @param clusterId
	 * @param cellid2Nodes
	 * @param clusteredCells
	 * @param coveredCellids
	 * @return
	 */
	private LinkedList<Node> adv2(QueryParams qParams, int clusterId, Node centerNode, Map<Integer, List<Node>> cellid2Nodes, 
								Map<Integer, Integer> clusteredCells, List<CellSign> availableCellids) {
//		System.out.println("adv2");
		
		
		LinkedList<Node> coverNodes = new LinkedList<>();
		List<Node> nds = null;
		Integer cid = null;
		for(CellSign cs : availableCellids) {
			nds = cellid2Nodes.get(cs.getId());
			// 没有分簇、已经分到当前簇、所在cell中，可能有部分点在当前簇中
			if(null == (cid = clusteredCells.get(cs.getId())) 
			   || cid == clusterId || cid == Cluster.SIGN_CELL_MUL_CLUSTER) {
				coverNodes.addAll(nds);
				if(coverNodes.size() >= qParams.minpts)	return null;
			}
		}
		LinkedList<Node> coverNodes1 = new LinkedList<>();
		double dis = 0.0;
		for(Node nd : coverNodes) {
			if(nd.isClassified() && nd.clusterId != clusterId)	continue;
			dis = centerNode.location.getMinimumDistance(nd.location);
			if(dis > qParams.epsilon)	continue;
			coverNodes1.add(nd);
		}
		return coverNodes1;
	}
	
	/**
	 * 更新被分类的cells
	 * @param clusterId
	 * @param cellid2Nodes
	 * @param clusteredCells
	 * @param availableCellids
	 */
	private void updateClusteredCells(int clusterId, Map<Integer, List<Node>> cellid2Nodes, Map<Integer, Integer> clusteredCells, 
									List<CellSign> availableCellids) {
		List<Node> nds = null;
		int sign = -1;	// 0表示有些的还没有分簇, -1表示都在clusterId里面；-2表示部分在clusterid里，部分在其他簇里；
		for(CellSign cs : availableCellids) {
			if(!clusteredCells.containsKey(cs.getId())) {
				if(cs.getSign())	clusteredCells.put(cs.getId(), clusterId);
				else {
					sign = -1;
					nds = cellid2Nodes.get(cs.getId());
					for(Node nd : nds) {
						if(nd.isClassified()) {
							if(sign == -1)	sign = nd.clusterId;
							else if(sign != -2) {
								if(sign != nd.clusterId)	sign = -2;
							}
						} else {
							sign = 0;
							break;
						}
					}
					if(sign == clusterId)	clusteredCells.put(cs.getId(), clusterId);
					else if(sign == -2)	clusteredCells.put(cs.getId(), Cluster.SIGN_CELL_MUL_CLUSTER);
				}
			}
		}
	}
	
	
	
	/**
	 * get cluster
	 * @param qParams
	 * @param clusterId
	 * @param qNode
	 * @param nodeCol
	 * @return
	 * @throws Exception
	 */
	public Cluster getCluster(QueryParams qParams, int clusterId, Node qNode, NodeCollection nodeCol, Boolean signAccessDis, 
			Map<Integer, List<Node>> cellid2Nodes, Map<Integer, Integer> clusteredCells) throws Exception{
		List<Node> addedNodes = new ArrayList<>();
		LinkedList<Node> neighbors = new LinkedList<>();
		LinkedList<Node> ngb = null;
		Node centerNode = null;
		
		// adv1、adv2
		List<CellSign> coveredCellids = null;
		List<CellSign> availableCellids = null;
		if(qParams.type >= 2) {
			sCircle.center = qNode.location.m_pCoords;
			coveredCellids = sgplInfo.cover(sCircle);
			availableCellids = cellHasNode(cellid2Nodes, coveredCellids);
			// adv1
			if(qParams.type >= 2 && adv1(cellid2Nodes, clusteredCells, availableCellids))	return null;
			// adv2
			if(qParams.type >= 3) {
				ngb = adv2(qParams, clusterId, qNode, cellid2Nodes, clusteredCells, availableCellids);
				if(ngb != null) {
					qNode.setToNoise();
					if(signAccessDis)	noiseRecoder.addDisNoise(new NodeNeighbors(qNode, ngb));
					else noiseRecoder.addScoNoise(new NodeNeighbors(qNode, ngb));
					return null;
				}
			}
		}
		
		
		
		qp.runTimeRec.setFrontTime();
		if(qParams.type == 1)	ngb = rtree.rangeQuery(qParams, clusterId, qNode, nodeCol, allLocations);
		else if(qParams.type == 2 || qParams.type == 3) {
			ngb = rtree.rangeQueryReDescendNodes(qParams, clusterId, qNode, nodeCol, allLocations);
//			ngb = rtree.rangeQuery(qParams, clusterId, qNode, nodeCol, allLocations);
		}
		qp.runTimeRec.numRangeRtree++;
		qp.runTimeRec.timeRangeRtree += qp.runTimeRec.getTimeSpan();
		
		if(null == ngb) {
			return null;
		} else if(ngb.size() < qParams.minpts) {
			qNode.setToNoise();
			if(signAccessDis)	noiseRecoder.addDisNoise(new NodeNeighbors(qNode, ngb));
			else noiseRecoder.addScoNoise(new NodeNeighbors(qNode, ngb));
			return null;
		} else {
			qNode.clusterId = clusterId;
			addedNodes.add(qNode);
			for(Node nd : ngb) {
				if(nd.isNoise()) {
					addedNodes.add(nd);
					nd.clusterId = clusterId;
				} else if(nd.isInitStatus()) {
					addedNodes.add(nd);
					nd.clusterId = clusterId;
					neighbors.add(nd);
				}
			}
			
			if(qParams.type >= 2)	updateClusteredCells(clusterId, cellid2Nodes, clusteredCells, availableCellids);
			
			while(!neighbors.isEmpty()) {
				centerNode = neighbors.pollFirst();
				
				if(qParams.type == 1) {
					qp.runTimeRec.setFrontTime();
					ngb = rtree.rangeQuery(qParams, clusterId, centerNode, nodeCol, allLocations);
					qp.runTimeRec.numRangeRtree++;
					qp.runTimeRec.timeRangeRtree += qp.runTimeRec.getTimeSpan();
				} else if(qParams.type == 2 || qParams.type == 3) {	// adv1、adv2
					sCircle.center = centerNode.location.m_pCoords;	// 在executeQuery方法开始已设置radius
					coveredCellids = sgplInfo.cover(sCircle);
					availableCellids = cellHasNode(cellid2Nodes, coveredCellids);
					// adv1
					if(qParams.type >= 2 && adv1(cellid2Nodes, clusteredCells, availableCellids))	continue;
					ngb = null;
					// adv2
					if(qParams.type >= 3) {
						ngb = adv2(qParams, clusterId, centerNode, cellid2Nodes, clusteredCells, availableCellids);
					}
					if(null == ngb) {
						qp.runTimeRec.setFrontTime();
						ngb = rtree.rangeQueryReDescendNodes(qParams, clusterId, centerNode, nodeCol, allLocations);
//						ngb = rtree.rangeQuery(qParams, clusterId, centerNode, nodeCol, allLocations);
						qp.runTimeRec.numRangeRtree++;
						qp.runTimeRec.timeRangeRtree += qp.runTimeRec.getTimeSpan();
					}
				}
				
                if(ngb == null || ngb.size() < qParams.minpts) {
                    continue;
                } else {
					for(Node nd : ngb) {
						if(nd.isNoise()) {
							addedNodes.add(nd);
							nd.clusterId = clusterId;
						} else if(nd.isInitStatus()){
							addedNodes.add(nd);
							nd.clusterId = clusterId;
							neighbors.add(nd);
						}
					} 
					if(qParams.type >= 2)	updateClusteredCells(clusterId, cellid2Nodes, clusteredCells, availableCellids);
				}
			}
		}
		if(!addedNodes.isEmpty()) {
			return new Cluster(clusterId, addedNodes, qp);
		} else return null;
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
	}
}
