package algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.ListenerNotFoundException;

import entity.Circle;
import entity.Node;
import entity.NodeCollection;
import entity.QueryParams;
import entity.SortedClusters;
import entity.fastrange.NgbNodes;
import entity.optics.OrderSeeds;
import index.optic.NeighborsNode;
import index.optic.Pid2Text2NeighborIndex;
import index.optic.Term2PidNeighborsIndex;
import precomputation.dataset.file.FileLoader;
import precomputation.dataset.file.OrginalFileWriter;
import utility.Global;

/**
 * the Alg of Euc Dis Advance dOptics
 * @author ZhouHao
 * @since 2018年12月16日
 */
public class AlgEucDisAdvancedOpticsWu2 extends AlgEucDisBaseOptics {
	private Pid2Text2NeighborIndex pid2Text2Nei = null;
	
	public AlgEucDisAdvancedOpticsWu2() throws Exception{
		super();
		pid2Text2Nei = new Pid2Text2NeighborIndex(Global.pathPid2Terms2NeighborsIndex);
		pid2Text2Nei.openIndexReader();
	}
	
	/**
	 * excute query
	 * @param qParams
	 * @return
	 * @throws Exception
	 */
	public SortedClusters excuteQuery(QueryParams qParams, String pathOrderedFile) throws Exception{
		Global.runTimeRec.timeTotal = System.nanoTime();
		
		// 采用lucene分词产生的wid_terms.txt([-125.0, 28.0], [15.0, 60]文件全部是小写，故输入的查询关键词得先转化为小写
		List<String> tWs = new ArrayList<>();
		for(String w : qParams.sWords)	tWs.add(w.toLowerCase());
		qParams.sWords = tWs;
		
		sCircle.radius = qParams.xi;
		
		Global.runTimeRec.setFrontTime();
		Map<Integer, List<NeighborsNode>> pidNeighbors = new HashMap<>();
		List<Node> nodes = new ArrayList<>();
		
		pid2Text2Nei.searchWords(qParams, allLocations, nodes, pidNeighbors);
		
		System.out.println(pidNeighbors.size());
		
		Global.runTimeRec.timeSearchTerms = Global.runTimeRec.getTimeSpan();
		
		Global.runTimeRec.timeOpticFunc = System.nanoTime();
		List<Node> sortedNodes = optics1(pidNeighbors, nodes, qParams, pathOrderedFile);
		Global.runTimeRec.timeOpticFunc = System.nanoTime() - Global.runTimeRec.timeOpticFunc;
		
		Global.runTimeRec.timeExcuteQueryFunc = System.nanoTime();
		SortedClusters sc = excuteQuery(qParams, pathOrderedFile, nodes, sortedNodes);
		Global.runTimeRec.timeExcuteQueryFunc = System.nanoTime() - Global.runTimeRec.timeExcuteQueryFunc;
		
		Global.runTimeRec.timeTotalPrepareData = Global.runTimeRec.timeSearchTerms + Global.runTimeRec.timeSortByDistance + 
											     Global.runTimeRec.timeSortByScore;
		
		Global.runTimeRec.timeTotal = System.nanoTime() - Global.runTimeRec.timeTotal;
		
		if(null == sc) Global.runTimeRec.numCluster = 0;
		else Global.runTimeRec.numCluster = sc.getSize();
		
		if(null==sc)	Global.runTimeRec.topKScore = 0;
		else Global.runTimeRec.topKScore = sc.getLastScore();
		
		return sc;
	}
	
	public List<Node> optics1(Map<Integer, List<NeighborsNode>> pidNeighbors, List<Node> nodes, QueryParams qParams, String pathOrderedFile)
			throws Exception {
		List<Node> orderedNodes = new ArrayList<>();
		OrginalFileWriter ofw = null;
		if(null!=pathOrderedFile) {
			ofw = new OrginalFileWriter(pathOrderedFile);
		}
		
		Map<Integer, Node> pid2Node = new HashMap<>();
		for(Node nd : nodes)	pid2Node.put(nd.id, nd);
		
		for(Node nd : nodes) {
			if(!nd.isProcessed) {
				expandClusterOrder1(pidNeighbors, pid2Node, nd, qParams, orderedNodes, ofw);
				Global.runTimeRec.numExpandClusterOrder++;
			}
		}
		
		if(null!=pathOrderedFile) {
			ofw.close();
		}
		return orderedNodes;
	}

	public void expandClusterOrder1(Map<Integer, List<NeighborsNode>> pidNeighbors, Map<Integer, Node> pid2Node,
			Node centerNode, QueryParams qParams,
			List<Node> orderedNodes, OrginalFileWriter ofw) throws Exception {
		
		Global.runTimeRec.setFrontTime();
		List<Node> neighbors = fastIndexRange(pidNeighbors.get(centerNode.id), pid2Node, centerNode, sCircle);
		Global.runTimeRec.numOpticLuceneRange++;
		Global.runTimeRec.timeOpticLuceneRange += Global.runTimeRec.getTimeSpan();
		
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
				
				Global.runTimeRec.setFrontTime();
				neighbors = fastIndexRange(pidNeighbors.get(centerNode.id), pid2Node, centerNode, sCircle);
				Global.runTimeRec.numOpticLuceneRange++;
				Global.runTimeRec.timeOpticLuceneRange += Global.runTimeRec.getTimeSpan();
				
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
	
	
	public List<Node> fastIndexRange(List<NeighborsNode> nighbor,  Map<Integer, Node> pid2Node, Node centerNode, Circle circle){
		List<Node> res = new ArrayList<>();
		for(NeighborsNode nn : nighbor) {
			if(nn.disToCenter > circle.radius)	break;
			if(pid2Node.containsKey(nn.id)){
				res.add(pid2Node.get(nn.id));
			}
		}
		return res;
	}

	public SortedClusters excuteQuery(QueryParams qParams, String pathOrderedFile,
			List<Node> nodes, List<Node> sortedNodes) throws Exception {
		// TODO Auto-generated method stub
		return super.excuteQueryByWu(qParams, pathOrderedFile, nodes, sortedNodes);
	}
	
}
