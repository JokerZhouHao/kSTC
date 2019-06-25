package algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import entity.Node;
import entity.QueryParams;
import entity.SortedClusters;
import entity.fastrange.NgbNodes;
import entity.optics.OrderSeeds;
import index.optic.NeighborsNode;
import index.optic.Term2PidNeighborsIndex;
import precomputation.dataset.file.FileLoader;
import precomputation.dataset.file.OrginalFileWriter;
import utility.Global;

/**
 * the Alg of Euc Dis Advance dOptics
 * @author ZhouHao
 * @since 2018年12月16日
 */
public class AlgEucDisAdvancedOpticsWu extends AlgEucDisBaseOptics {
	private Term2PidNeighborsIndex term2PNgb = null;
	private Map<String, Integer> ngbLens = null;
	
//	public AlgEucDisAdvancedOpticsWu(QueryParams qp) throws Exception{
//		super(qp);
//		term2PNgb = new Term2PidNeighborsIndex(Global.pathTerm2PidNeighborsIndex);
//		term2PNgb.openIndexReader();
//		
//		ngbLens = FileLoader.loadPidNgbLens(Global.pathPidNeighborLen);
//	}
	
	public AlgEucDisAdvancedOpticsWu(QueryParams qp) throws Exception{
		super(qp);
		String path = Global.getPathTerm2PidNeighborsIndex(qp.maxPidNeighborsBytes);
		term2PNgb = new Term2PidNeighborsIndex(path);
		term2PNgb.openIndexReader();
		
		path = Global.getPathPidNeighborLen(qp.maxPidNeighborsBytes);
		ngbLens = FileLoader.loadPidNgbLens(path);
	}
	
	
	@Override
	public List<Node> optics(Map<Integer, List<Node>> cellid2Nodes, QueryParams qParams, String pathOrderedFile)
			throws Exception {
		List<Node> orderedNodes = new ArrayList<>();
		OrginalFileWriter ofw = null;
		if(null!=pathOrderedFile) {
			ofw = new OrginalFileWriter(pathOrderedFile);
		}
		
//		查交集
		int minLen = Integer.MAX_VALUE;
		String minTerm = null;
		for(String tm : qParams.sWords) {
//			System.out.print(ngbLens.get(tm) + " ");
			if(ngbLens.get(tm) <= minLen) {
				minLen = ngbLens.get(tm);
				minTerm = tm;
			}
		}
//		System.out.println(minLen);
		qp.runTimeRec.numMinByteOfTermPNgb = minLen;
		
//		 改为查并集
//		int minLen = Integer.MAX_VALUE;
//		int maxLen = Integer.MIN_VALUE;
//		for(String tm : qParams.sWords) {
//			if(ngbLens.get(tm) <= minLen) {
//				minLen = ngbLens.get(tm);
//			}
//			if(ngbLens.get(tm) >= maxLen)
//				maxLen = ngbLens.get(tm);
//		}
		
		if(minLen <= 0) { // the all points of containing the term aren't core points
			for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
				for(Node nd : en.getValue()) {
					if(!nd.isProcessed) {
						nd.isProcessed = Boolean.TRUE;
						nd.reachabilityDistance = Node.UNDEFINED;
						nd.coreDistance = Node.UNDEFINED;
						if(null != ofw)	ofw.writeIdCoreAndDirectDis(nd.id, nd.coreDistance, nd.reachabilityDistance); 
						orderedNodes.add(nd);
					}
				}
			}
		} else if (minLen == Integer.MAX_VALUE) { // the term ngb too long
			for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
				for(Node nd : en.getValue()) {
					if(!nd.isProcessed) {
						super.expandClusterOrder(cellid2Nodes, nd, qParams, orderedNodes, ofw);
					}
				}
			}
		} else {	// the term ngb in index
			qp.runTimeRec.timeSearchTermPNgb = System.nanoTime();
//			查交集
			List<Map<Integer, List<NeighborsNode>>> pid2Ngbs = new ArrayList<>();
			pid2Ngbs.add(term2PNgb.searchTerm(minTerm, qp));
//			查并集
//			List<Map<Integer, List<NeighborsNode>>> pid2Ngbs = new ArrayList<>();
//			for(String tm : qParams.sWords) {
//				pid2Ngbs.add(term2PNgb.searchTerm(tm));
//			}
			qp.runTimeRec.timeSearchTermPNgb = System.nanoTime() - qp.runTimeRec.timeSearchTermPNgb; 
			
			Map<Integer, Node> pid2Node = new HashMap<>();
			for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
				for(Node nd : en.getValue()) {
					pid2Node.put(nd.id, nd);
				}
			}
			
			
			
//			System.out.println("> advance optic wu NODE : " + pid2Node.size());
			
			
			
			for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
				for(Node nd : en.getValue()) {
					if(!nd.isProcessed) {
						expandClusterOrder(cellid2Nodes, nd, qParams, orderedNodes, ofw, pid2Ngbs, pid2Node);
						qp.runTimeRec.numExpandClusterOrder++;
					}
				}
			}
		}
		if(null!=pathOrderedFile) {
			ofw.close();
		}
		return orderedNodes;
	}

	public void expandClusterOrder(Map<Integer, List<Node>> cellid2Nodes, Node centerNode, QueryParams qParams,
			List<Node> orderedNodes, OrginalFileWriter ofw, List<Map<Integer, List<NeighborsNode>>> pid2Ngbs,
			Map<Integer, Node> pid2Node) throws Exception {
		
		qp.runTimeRec.setFrontTime();
		List<Node> neighbors = fastIndexRange(pid2Ngbs, pid2Node, centerNode);
		qp.runTimeRec.numOpticLuceneRange++;
		qp.runTimeRec.timeOpticLuceneRange += qp.runTimeRec.getTimeSpan();
		
		centerNode.isProcessed = Boolean.TRUE;
		centerNode.reachabilityDistance = Node.UNDEFINED;
		centerNode.setCoreDistanceBySorted(qParams, neighbors);
		if(null != ofw)	ofw.writeIdCoreAndDirectDis(centerNode.id, centerNode.coreDistance, centerNode.reachabilityDistance); 
		orderedNodes.add(centerNode);
		OrderSeeds orderSeeds = new OrderSeeds();
		if(centerNode.coreDistance != Node.UNDEFINED) {
			orderSeeds.update(neighbors, centerNode);
			while(!orderSeeds.isEmpty()) {
				centerNode = orderSeeds.pollFirst();

				
//				if(centerNode.id == 59048) {
//					System.out.println(centerNode);
//				}
				qp.runTimeRec.setFrontTime();
				neighbors = fastIndexRange(pid2Ngbs, pid2Node, centerNode);
				qp.runTimeRec.numOpticLuceneRange++;
				qp.runTimeRec.timeOpticLuceneRange += qp.runTimeRec.getTimeSpan();
				
				centerNode.isProcessed = Boolean.TRUE;
				centerNode.setCoreDistanceBySorted(qParams, neighbors);
				if(null != ofw)	ofw.writeIdCoreAndDirectDis(centerNode.id, centerNode.coreDistance, centerNode.reachabilityDistance);
				orderedNodes.add(centerNode);
				if(centerNode.coreDistance != Node.UNDEFINED) {
					orderSeeds.update(neighbors, centerNode);
				}
			}
		}
	}
	
	
	public List<Node> fastIndexRange(List<Map<Integer, List<NeighborsNode>>> pid2Ngbs, Map<Integer, Node> pid2Node, Node centerNode){
		NgbNodes recNgb = new NgbNodes(Boolean.TRUE);
//		List<Node> ngbList = new ArrayList<>();
		
		for(Map<Integer, List<NeighborsNode>> pidNeighbors : pid2Ngbs) {
			List<NeighborsNode> ngb = pidNeighbors.get(centerNode.id);
			if(ngb==null)	continue;
			Node nd = null;
			for(NeighborsNode nn : ngb) {
				if(nn.disToCenter > sCircle.radius)	break;	// disToCenter is bigger than xi
				if(null != (nd = pid2Node.get(nn.id))) {
					nd.disToCenter = nn.disToCenter;
					recNgb.add(nn.disToCenter, nd);
//					ngbList.add(nd);
				}
			}
		}
//		System.out.println(centerNode.id + " : " + recNgb.toList().size());
		return recNgb.toList();
//		return ngbList;
	}

	@Override
	public SortedClusters excuteQuery(QueryParams qParams, String pathOrderedFile,
			Map<Integer, List<Node>> cellid2Nodes, List<Node> sortedNodes) throws Exception {
		// TODO Auto-generated method stub
		return super.excuteQueryByWu(qParams, pathOrderedFile, cellid2Nodes, sortedNodes);
	}
	
}
