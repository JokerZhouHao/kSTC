package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import entity.CellCollection;
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
import entity.fastrange.NgbNodes;
import index.CellidPidWordsIndex;
import index.IdWordsIndex;
import index.Term2CellColIndex;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.MComparator;
import utility.index.rtree.MRTree;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * the distance of the fast range algorithm is caculated by Euclidean Distance
 * @author ZhouHao
 * @since 2018年11月16日
 */
public class AlgEucDisFastRange {
	
	private Point[] allLocations = null;
	private CellidPidWordsIndex cellidWIndex = null;
	private NoiseRecoder noiseRecoder = new NoiseRecoder();
	private SGPLInfo sgplInfo = Global.sgplInfo;
	private Circle sCircle = new Circle(0.0, new double[2]);
	
	public AlgEucDisFastRange() throws Exception{
		allLocations = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
//		allLocations = FileLoader.loadPoints(Global.pathIdCoord);
//		cellidWIndex = new CellidPidWordsIndex(Global.pathCellidPidWordsIndex);
		
		cellidWIndex = new CellidPidWordsIndex(Global.pathCellidRtreeidOrPidWordsIndex);
		
		cellidWIndex.openIndexReader();
	}
	
	/**
	 * excute query
	 * @param qParams
	 * @return
	 * @throws Exception
	 */
	public SortedClusters excuteQuery(QueryParams qParams) throws Exception{
		// 采用lucene分词产生的wid_terms.txt([-125.0, 28.0], [15.0, 60]文件全部是小写，故输入的查询关键词得先转化为小写
		List<String> tWs = new ArrayList<>();
		for(String w : qParams.sWords)	tWs.add(w.toLowerCase());
		qParams.sWords = tWs;
		
		sCircle.radius = qParams.epsilon;
		Map<Integer, List<Node>> cellid2Nodes = cellidWIndex.searchWords(qParams, allLocations);
		if(null == cellid2Nodes)	return null;
		
		/* building sort distance node collection and sort score node collection */
		List<Node> nodes = new ArrayList<>();
		for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
			nodes.addAll(en.getValue());
		}
		PNodeCollection disPNodeCol = new PNodeCollection(nodes).sortByDistance();
		PNodeCollection scorePNodeCol = new PNodeCollection(nodes).sortByScore();
		
		SortedClusters sClusters = new SortedClusters(qParams);
		Cluster cluster = null;
		int curClusterId = 1;
		
		double[] disAndSco = null;
		double topKScore = Double.MAX_VALUE;
		double bound = Double.MIN_VALUE;
		Node curNode = null;
		
		Boolean signAccessDis = Boolean.TRUE;
		
		/* recode clustered cells */
		Map<Integer, Integer> clusteredCells = new HashMap<>();
		
		do {
			if(signAccessDis) {
				curNode = disPNodeCol.next();
			} else {
				curNode = scorePNodeCol.next();
			}
			
			if(curNode == null)	break;	// all nodes have accessed
			else if(curNode.isNoise()) {
				if(signAccessDis)	noiseRecoder.addDisNoise(new NodeNeighbors(curNode, curNode.neighbors));
				else noiseRecoder.addScoNoise(new NodeNeighbors(curNode, curNode.neighbors));
				continue;
			}
			cluster = getCluster(cellid2Nodes, clusteredCells, qParams, curClusterId, curNode, signAccessDis);
			if(null != cluster) {
				sClusters.add(cluster);
				curClusterId++;
			}
			topKScore = sClusters.getTopKScore();
			
			disAndSco = noiseRecoder.getMinDisAndSco();
			
			if(signAccessDis) {
				disAndSco[0] = disAndSco[0] <= disPNodeCol.first(0).distance ? disAndSco[0]:disPNodeCol.first(0).distance;
				disAndSco[1] = disAndSco[1] <= scorePNodeCol.first(1).score ? disAndSco[1]:scorePNodeCol.first(1).score;
			} else {
				disAndSco[0] = disAndSco[0] <= disPNodeCol.first(1).distance ? disAndSco[0]:disPNodeCol.first(1).distance;
				disAndSco[1] = disAndSco[1] <= scorePNodeCol.first(0).score ? disAndSco[1]:scorePNodeCol.first(0).score;
			}
			bound = Global.alpha * disAndSco[0] + (1 - Global.alpha) * disAndSco[1];
			
			if(signAccessDis) signAccessDis = Boolean.FALSE;
			else signAccessDis = Boolean.TRUE;
		} while(bound <= topKScore);
		
		noiseRecoder.clear();
		
		return sClusters;
	}
	

	/**
	 * get cluster
	 * @param cellid2Nodes
	 * @param clusteredCells
	 * @param qParams
	 * @param clusterId
	 * @param qNode
	 * @param signAccessDis
	 * @return
	 * @throws Exception
	 */
	public Cluster getCluster(Map<Integer, List<Node>> cellid2Nodes, Map<Integer, Integer> clusteredCells,
			QueryParams qParams, int clusterId, Node qNode, Boolean signAccessDis ) throws Exception{
		List<Node> addedNodes = new ArrayList<>();
		LinkedList<Node> neighbors = new LinkedList<>();
		NgbNodes ngb = null;
		LinkedList<Node> ngbNodes = null;
		Node centerNode = null;
		ngb = fastRange(cellid2Nodes, clusteredCells, qParams, clusterId, qNode);
		if(null == ngb) {
			return null;
		} else if(ngb.size() < qParams.minpts) {
			qNode.setToNoise();
			ngbNodes = ngb.toList();
			if(signAccessDis)	noiseRecoder.addDisNoise(new NodeNeighbors(qNode, ngbNodes));
			else noiseRecoder.addScoNoise(new NodeNeighbors(qNode, ngbNodes));
			return null;
		} else {
			qNode.clusterId = clusterId;
			addedNodes.add(qNode);
			ngbNodes = ngb.toList();
			for(Node nd : ngbNodes) {
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
				ngb = fastRange(cellid2Nodes, clusteredCells, qParams, clusterId, centerNode);
				if(ngb == null || ngb.size() < qParams.minpts) {
					continue;
				} else {
					ngbNodes = ngb.toList();
					for(Node nd : ngbNodes) {
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
			return new Cluster(clusterId, addedNodes);
		} else return null;
	}
	
	/**
	 * fastRange
	 * @param cellid2Nodes
	 * @param clusteredCells
	 * @param qParams
	 * @param clusterId
	 * @param qNode
	 * @return
	 * @throws Exception
	 */
	public NgbNodes fastRange(Map<Integer, List<Node>> cellid2Nodes, Map<Integer, Integer> clusteredCells,
			QueryParams qParams, int clusterId, Node qNode) throws Exception{
		/* skipping rule， NgbNodes元素是以dis由远及近排序的*/
		sCircle.center = qNode.location.m_pCoords;
		List<CellSign> coveredCellids = sgplInfo.cover(sCircle);
		Boolean sign = Boolean.TRUE;
		
		for(CellSign cs : coveredCellids) {
			if(!clusteredCells.containsKey(cs.getId())) {
				sign = Boolean.FALSE;
				break;
			}
		}
		if(sign)	return null;
		
		/* fast range */
		sCircle.center = qNode.location.m_pCoords;
		NgbNodes ngb = new NgbNodes();
		List<Node> cellNodes = null;
		double dis = 0.0;
		Integer tIn = 0;
		
		for(CellSign cs : coveredCellids) {
			if(null == (cellNodes = cellid2Nodes.get(cs.getId())))	continue;
			if(null != (tIn = clusteredCells.get(cs.getId())) && tIn!=clusterId) continue;
			sign = Boolean.TRUE;
			for(Node nd : cellNodes) {
				if(nd.hasInCluster(clusterId)) {
					ngb.add(NgbNodes.signUsedKey, nd);
				} else if(!nd.isClassified()) {
					if(!cs.getSign())	sign = Boolean.FALSE;
					dis = nd.location.getMinimumDistance(qNode.location);
					if(dis <= qParams.epsilon)	ngb.add(dis, nd);
				}
			}
			if(sign)	clusteredCells.put(cs.getId(), clusterId);
		}
		if(0==ngb.size())	return null;
		return ngb;
	}
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
	}
}