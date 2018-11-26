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

import entity.CellCollection;
import entity.Circle;
import entity.Cluster;
import entity.NeighborStack;
import entity.Node;
import entity.NodeCollection;
import entity.NodeNeighbors;
import entity.NoiseRecoder;
import entity.PNodeCollection;
import entity.QueryParams;
import entity.SGPLInfo;
import entity.SortedClusters;
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
	private IdWordsIndex idWordsIndex = null;
	private Term2CellColIndex term2CellCIndex = null;
	private NoiseRecoder noiseRecoder = new NoiseRecoder();
	private SGPLInfo sgplInfo = Global.sgplInfo;
	private MComparator<Double> compDoubleDescend = new MComparator<Double>();
	private Circle sCircle = new Circle(0.0, new double[2]);
	
	public AlgEucDisFastRange() throws Exception{
		allLocations = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
//		allLocations = FileLoader.loadPoints(Global.pathIdCoord);
		idWordsIndex = new IdWordsIndex(Global.pathPidAndRtreeIdWordsIndex);
		idWordsIndex.openIndexReader();
		term2CellCIndex = new Term2CellColIndex(Global.pathTerm2CellColIndex);
		term2CellCIndex.openIndexReader();
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
		List<CellCollection> termsCells = new ArrayList<>();
		for(String term : qParams.sWords) {
			termsCells.add(term2CellCIndex.searchTerm(term));
		}
		
		SortedClusters sClusters = new SortedClusters(qParams);
		Cluster cluster = null;
		int curClusterId = 1;
		
		double[] disAndSco = null;
		double topKScore = Double.MAX_VALUE;
		double bound = Double.MIN_VALUE;
		Node curNode = null;
		
		Boolean signAccessDis = Boolean.TRUE;
		
		Set<Integer> clusteredCells = new HashSet<>();
		Map<Integer, Set<Integer>> cellPids = new HashMap<>();
		
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
			cluster = getCluster(termsCells, qParams, curClusterId, curNode, nodeCol, signAccessDis);
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
	public Cluster getCluster(List<CellCollection> termsCells, Set<Integer> clusteredCells, Map<Integer, Set<Integer>> cellPids, 
			QueryParams qParams, int clusterId, Node qNode, 
			NodeCollection nodeCol, Boolean signAccessDis ) throws Exception{
		NeighborStack ngbStack = new NeighborStack();
		TreeMap<Double, LinkedList<Node>> ngb = null;
		
		ngb = fastRange()
		
		
		
		
		Set<Node> addedNodes = new HashSet<>();
		LinkedList<Node> neighbors = new LinkedList<>();
		LinkedList<Node> ngb = null;
		Node centerNode = null;
		ngb = rtree.rangeQuery(qParams, clusterId, qNode, nodeCol, allLocations);
		if(null == ngb) {
			return null;
		} else if(ngb.size() < qParams.minpts) {
			qNode.setToNoise();
			if(signAccessDis)	noiseRecoder.addDisNoise(new NodeNeighbors(qNode, ngb));
			else noiseRecoder.addScoNoise(new NodeNeighbors(qNode, ngb));
			return null;
		} else {
			qNode.clusterId = clusterId;
			addedNodes.addAll(ngb);
			for(Node nd : ngb) {
				if(nd.isNoise()) {
					nd.clusterId = clusterId;
				} else if(nd.isInitStatus()) {
					nd.clusterId = clusterId;
					neighbors.add(nd);
				}
			}
			
			while(!neighbors.isEmpty()) {
				centerNode = neighbors.pollFirst();
				ngb = rtree.rangeQuery(qParams, clusterId, centerNode, nodeCol, allLocations);
				if(ngb == null) {
					continue;
				} else if (ngb.size() < qParams.minpts) {
					centerNode.setToNoise();
					centerNode.neighbors = ngb;
					continue;
				} else {
					addedNodes.addAll(ngb);
					for(Node nd : ngb) {
						if(nd.isNoise()) {
							nd.clusterId = clusterId;
						} else if(nd.isInitStatus()){
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
	
	public TreeMap<Double, LinkedList<Node>> fastRange(List<CellCollection> termsCells, Set<Integer> clusteredCells, Map<Integer, Set<Integer>> cellPids,
			QueryParams qParams, int clusterId, Node qNode, 
			NodeCollection nodeCol) throws Exception{
		
		
		
		
	}
	
	
	/* test test */
	public static void testTest() throws Exception{
		QueryParams qParams = new QueryParams();
		qParams.k = 4;
		double[] loca = {0.7, 0.1};
		qParams.location = new Point(loca);
		List<String> words = new ArrayList<>();
		words.add("c");
//		words.add("c");
//		words.add("f");
//		words.add("c");
		qParams.sWords = words;
		qParams.minpts = 5;
		qParams.epsilon = 0.5;
		AlgEucDisFastRange alg = new AlgEucDisFastRange();
		SortedClusters sClu = alg.excuteQuery(qParams);
		System.out.println(sClu);
		
		String resPath = Global.outPath + "result.txt";
		IOUtility.writeSortedClusters(resPath, qParams, sClu);
		
		System.out.println("用时：" + TimeUtility.getGlobalSpendTime());
	}
	
	/* test yelp buss */
	public static void testYelpBuss() throws Exception{
		String[] allTxt = FileLoader.loadText(Global.pathIdText);
		Point[] allCoord = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
		
		int id = new Random().nextInt(Global.numNode);
		
		QueryParams qParams = new QueryParams(allCoord[id], allTxt[id], 1, 10, 0.01, 10);
		
//		QueryParams qParams = new QueryParams();
//		double[] loca = {0.32591626714285715, 0.48902884999999996};
//		qParams.location = new Point(loca);
//		List<String> words = new ArrayList<>();
//		words.add("91");
////		words.add("c");
////		words.add("f");
////		words.add("c");
//		qParams.sWords = words;
//		qParams.k = 10;
//		qParams.epsilon = 0.01;
//		qParams.minpts = 10;
		
		AlgEucDisFastRange alg = new AlgEucDisFastRange();
		SortedClusters sClu = alg.excuteQuery(qParams);
		System.out.println(sClu);
		
		String resPath = Global.outPath + "result.txt";
		IOUtility.writeSortedClusters(resPath, qParams, sClu);
		
		System.out.println("共簇：" + sClu.getClusters().size());
		
		System.out.println("用时：" + TimeUtility.getGlobalSpendTime());
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		AlgEucDisFastRange.testYelpBuss();
	}
}
