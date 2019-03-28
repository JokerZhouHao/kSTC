package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import entity.Node;
import entity.QueryParams;
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
public class AlgEucDisAdvancedOptics extends AlgEucDisBaseOptics{
	private Term2PidNeighborsIndex term2PNgb = null;
	private Map<String, Integer> ngbLens = null;
	
	public AlgEucDisAdvancedOptics() throws Exception{
		super();
		term2PNgb = new Term2PidNeighborsIndex(Global.pathTerm2PidNeighborsIndex);
		term2PNgb.openIndexReader();
		
		ngbLens = FileLoader.loadPidNgbLens(Global.pathPidNeighborLen);
	}
	
	@Override
	public List<Node> optics(Map<Integer, List<Node>> cellid2Nodes, QueryParams qParams, String pathOrderedFile)
			throws Exception {
		List<Node> orderedNodes = new ArrayList<>();
		OrginalFileWriter ofw = null;
		if(null!=pathOrderedFile) {
			ofw = new OrginalFileWriter(pathOrderedFile);
		}
		
		int minLen = Integer.MAX_VALUE;
		String minTerm = null;
		for(String tm : qParams.sWords) {
			if(ngbLens.get(tm) <= minLen) {
				minLen = ngbLens.get(tm);
				minTerm = tm;
			}
		}
		
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
			Map<Integer, List<NeighborsNode>> pid2Ngb = term2PNgb.searchTerm(minTerm);
			Map<Integer, Node> pid2Node = new HashMap<>();
			for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
				for(Node nd : en.getValue()) {
					pid2Node.put(nd.id, nd);
				}
			}
			
			for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
				for(Node nd : en.getValue()) {
					if(!nd.isProcessed) {
						expandClusterOrder(cellid2Nodes, nd, qParams, orderedNodes, ofw, pid2Ngb, pid2Node);
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
			List<Node> orderedNodes, OrginalFileWriter ofw, Map<Integer, List<NeighborsNode>> pid2Ngb,
			Map<Integer, Node> pid2Node) throws Exception {

		
//		if(centerNode.id == 59048) {
//			System.out.println(centerNode);
//		}
			
		
		List<Node> neighbors = fastIndexRange(pid2Ngb, pid2Node, centerNode);
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
					
					
				neighbors = fastIndexRange(pid2Ngb, pid2Node, centerNode);
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
	
	
	public List<Node> fastIndexRange(Map<Integer, List<NeighborsNode>> pidNeighbors, Map<Integer, Node> pid2Node, Node centerNode){
		List<Node> res = new ArrayList<>();
		List<NeighborsNode> ngb = pidNeighbors.get(centerNode.id);
		if(ngb==null)	return res;
		Node nd = null;
		for(NeighborsNode nn : ngb) {
			if(nd.disToCenter > sCircle.radius)	break;	// disToCenter is bigger than xi
			if(null != (nd = pid2Node.get(nn.id))) {
				nd.disToCenter = nn.disToCenter;
				res.add(nd);
			}
		}
		return res;
	}
}
