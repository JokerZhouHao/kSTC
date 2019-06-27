package algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import entity.Cluster;
import entity.Node;
import entity.NodeCollection;
import entity.NodeNeighbors;
import entity.NoiseRecoder;
import entity.PNodeCollection;
import entity.QueryParams;
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
		init();
	}
	
	private void init() throws Exception{
		if(Global.allLocations == null)	allLocations = FileLoader.loadPoints(Global.pathIdNormCoord);
		else allLocations = Global.allLocations;
		
		String path =  Global.getPathCellidRtreeidOrPidWordsIndex(qp.rtreeFanout, qp.zorderWidth, qp.zorderHeight);
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
			cluster = getCluster(qParams, curClusterId, curNode, nodeCol, signAccessDis);
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
	
	/**
	 * get cluster
	 * @param qParams
	 * @param clusterId
	 * @param qNode
	 * @param nodeCol
	 * @return
	 * @throws Exception
	 */
	public Cluster getCluster(QueryParams qParams, int clusterId, Node qNode, NodeCollection nodeCol, Boolean signAccessDis ) throws Exception{
		List<Node> addedNodes = new ArrayList<>();
		LinkedList<Node> neighbors = new LinkedList<>();
		LinkedList<Node> ngb = null;
		Node centerNode = null;
		
		qp.runTimeRec.setFrontTime();
		ngb = rtree.rangeQuery(qParams, clusterId, qNode, nodeCol, allLocations);
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
			
			while(!neighbors.isEmpty()) {
				centerNode = neighbors.pollFirst();
				
				qp.runTimeRec.setFrontTime();
				ngb = rtree.rangeQuery(qParams, clusterId, centerNode, nodeCol, allLocations);
				qp.runTimeRec.numRangeRtree++;
				qp.runTimeRec.timeRangeRtree += qp.runTimeRec.getTimeSpan();
				
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
