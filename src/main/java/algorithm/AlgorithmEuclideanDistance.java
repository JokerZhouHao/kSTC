package algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import entity.Cluster;
import entity.Node;
import entity.NodeCollection;
import entity.PNodeCollection;
import entity.QueryParams;
import entity.SortedClusters;
import index.IdWordsIndex;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.index.rtree.MRTree;

/**
 * the distance is caculated by Euclidean Distance
 * @author ZhouHao
 * @since 2018年11月16日
 */
public class AlgorithmEuclideanDistance {
	
	private Point[] allLocations = null;
	private IdWordsIndex idWordsIndex = null;
	private MRTree rtree = null;
	
	public AlgorithmEuclideanDistance() throws Exception{
		allLocations = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
		idWordsIndex = new IdWordsIndex(Global.pathPidAndRtreeIdWordsIndex);
		idWordsIndex.openIndexReader();
		rtree = MRTree.getInstanceInDisk();
	}
	
	/**
	 * excute query
	 * @param qParams
	 * @return
	 * @throws Exception
	 */
	public SortedClusters excuteQuery(QueryParams qParams) throws Exception{
		NodeCollection nodeCol = idWordsIndex.searchWords(qParams, allLocations);
		PNodeCollection disPNodeCol = nodeCol.getPNodeCollection().sortByDistance();
		PNodeCollection scorePNodeCol = nodeCol.getPNodeCollection().copy().sortByScore();
		
		SortedClusters sClusters = new SortedClusters(qParams.location, qParams.k);
		Cluster cluster = null;
		int curClusterId = 1;
		
		double topKScore = Double.MAX_VALUE;
		double bound = Double.MIN_VALUE;
		Node curNode = null;
		
		Boolean signAccessDis = Boolean.TRUE;
		
		do {
			if(signAccessDis) {
				curNode = disPNodeCol.next();
				signAccessDis = Boolean.FALSE;
			} else {
				curNode = scorePNodeCol.next();
				signAccessDis = Boolean.TRUE;
			}
			if(curNode == null)	break;	// all nodes are accessed
			
			cluster = getCluster(qParams, curClusterId, curNode, nodeCol);
			if(null != cluster) {
				sClusters.add(cluster);
				curClusterId++;
			}
			topKScore = sClusters.getTopKScore();
			bound = Global.alpha * disPNodeCol.first().distance + (1 - Global.alpha) * scorePNodeCol.first().score;
		} while(bound <= topKScore);
		
		return sClusters;
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
	public Cluster getCluster(QueryParams qParams, int clusterId, Node qNode, NodeCollection nodeCol ) throws Exception{
		Set<Node> addedNodes = new HashSet<>();
		LinkedList<Node> neighbors = new LinkedList<>();
		LinkedList<Node> ngb = rtree.rangeQuery(qParams, qNode, nodeCol, allLocations);
		Node centerNode = null;
		if(ngb.size() < qParams.minpts) {
			qNode.setToNoise();
			return null;
		} else {
			for(Node nd : ngb) {
				nd.clusterId = clusterId;
				addedNodes.add(nd);
			}
			neighbors.addAll(ngb);
			
			while(!neighbors.isEmpty()) {
				centerNode = neighbors.pollFirst();
				ngb = rtree.rangeQuery(qParams, centerNode, nodeCol, allLocations);
				if(ngb.size() >= qParams.minpts) {
					for(Node nd : ngb) {
						if(nd.isNoise())	addedNodes.add(nd);
						else if(!addedNodes.contains(nd)){
							addedNodes.add(nd);
							nd.clusterId = clusterId;
							neighbors.add(nd);
						}
					} 
				}
			}
		}
		if(!addedNodes.isEmpty()) {
			return new Cluster(clusterId, addedNodes);
		} else return null;
	}
}





















