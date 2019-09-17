package precomputation.dataset.file.optic;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import entity.CellSign;
import entity.Circle;
import entity.Node;
import entity.QueryParams;
import entity.SGPLInfo;
import entity.fastrange.NgbNodes;
import entity.optics.OrderSeeds;
import index.CellidPidWordsIndex;
import index.optic.Term2PidNeighborsIndex;
import precomputation.dataset.file.FileLoader;
import precomputation.dataset.file.OrginalFileWriter;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.MLog;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * 创建计算<term, <<pid, neighbors><pid, neighbors><pid, neighbors> . . . >索引
 * @author ZhouHao
 * @since 2019年4月27日
 */
public class Term2PidNeighborsContainReachDisIndexBuilder2Optic implements Runnable{
	private final static String signOver = "~~~~";
	
	// because synchronized can't use Integer as a synchron object, so i create a new int class
	static public class TInt{
		int x = 0;
		public TInt(int x) {
			this.x = x;
		}
		
		public String toString() {
			return String.valueOf(x);
		}
	}
	
	private static TInt numThread = new TInt(0);
	private static TInt numNgbTooLong = new TInt(0);
	private static Term2PidNeighborsIndex term2PidNeiIndex = null;
	private static BufferedWriter pidNeighborLenBW = null;
	private static SGPLInfo sgplInfo = Global.sgplInfo;
	private static CellidPidWordsIndex cellidWIndex = null;
	private static Point[] allLocations = null;
	private static TInt numDealedTerm = new TInt(0);
	private static Boolean hasStop = Boolean.FALSE;
	
	private String[] allTerms = null;
	private static AtomicInteger numTerms = null;
	private static volatile int num1Percent = 0;
	
	private QueryParams qParams = null;
	private Circle sCircle = null;
	
	private ArrayBlockingQueue<String> termQueue = null;
	
	
	public static void reset() {
		numThread = new TInt(0);
		numNgbTooLong = new TInt(0);
		term2PidNeiIndex = null;
		pidNeighborLenBW = null;
		sgplInfo = Global.sgplInfo;
		cellidWIndex = null;
		allLocations = null;
		numDealedTerm = new TInt(0);
		hasStop = Boolean.FALSE;
		numTerms = null;
		num1Percent = 0;
	}
	
	public Term2PidNeighborsContainReachDisIndexBuilder2Optic(String[] allTerms, ArrayBlockingQueue<String> termQueue, QueryParams qParams, String pathTerm2PidNei, String pathCellidRtreeidOrPidWordsIndex,
			String pathPidNeighborLen) throws Exception{
		this.termQueue = termQueue;
		this.addThread();
		this.allTerms = allTerms;
		this.qParams = qParams;
		this.sCircle = new Circle(qParams.epsilon, new double[2], Global.sgplInfo);
		synchronized (sgplInfo) {
			if(null == term2PidNeiIndex) {
				term2PidNeiIndex = new Term2PidNeighborsIndex(pathTerm2PidNei);
				term2PidNeiIndex.openIndexWriter();
			}
			if(null == pidNeighborLenBW) {
				pidNeighborLenBW = IOUtility.getBW(pathPidNeighborLen);
				pidNeighborLenBW.write(Global.delimiterPound + String.valueOf(allTerms.length) + "\n");
			}
			if(null == cellidWIndex) {
				cellidWIndex = new CellidPidWordsIndex(pathCellidRtreeidOrPidWordsIndex);
				cellidWIndex.openIndexReader();
			}
			if(null == allLocations) {
				allLocations = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
			}
			if(null == numTerms) {
				numTerms = new AtomicInteger(allTerms.length);
				num1Percent = numTerms.get() / 100;
			}
		}
	}
	
	public void addDoc(String term, byte[] pidNeighbors) throws Exception{
		synchronized (term2PidNeiIndex) {
			term2PidNeiIndex.addDoc(term, pidNeighbors);
		}
	}
	
	
	
	public void addDoc(String term, Map<Integer, List<Node>> pidNeighbors) throws Exception{
		synchronized (term2PidNeiIndex) {
			for(Entry<Integer, List<Node>> en : pidNeighbors.entrySet()) {
				term2PidNeiIndex.addDoc(term, term2PidNeiIndex.pidNeighborsToBytes(en));
			}
		}
	}
	
	public void writePidNeighborLen(String term, int numBytes) throws Exception{
		synchronized (pidNeighborLenBW) {
			pidNeighborLenBW.write(term);
			pidNeighborLenBW.write(Global.delimiterLevel1);
			pidNeighborLenBW.write(String.valueOf(numBytes));
			pidNeighborLenBW.write('\n');
			pidNeighborLenBW.flush();
		}
	}
	
	public  void addThread() {
		synchronized (numThread) {
			numThread.x++;
		}
	}
	
	public void reduceThread() throws Exception{
		synchronized (numThread) {
			--numThread.x;
			if(0 == numThread.x) {
				clean();
				MLog.log("Over, 共处理" + String.valueOf(allTerms.length) + "个term，numNgbTooLong = " + String.valueOf(numNgbTooLong) + ", 总用时：" + TimeUtility.getGlobalSpendTime());
				hasStop = Boolean.TRUE;
			}
		}
	}
	
	public static Boolean hasStop() throws Exception {
		return hasStop;
	}
	
	public void clean() throws Exception{
		term2PidNeiIndex.close();
		pidNeighborLenBW.close();
		cellidWIndex.close();
	}
	
	/************** 计算reach distance *************/
	private int numCur4Bytes = 0;
	private String curSearchTerm = null;
	
	private List<Node> optics(Map<Integer, List<Node>> cellid2Nodes, QueryParams qParams) throws Exception{
		Map<Integer, Node> id2Node = new HashMap<>();
		for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
			for(Node nd : en.getValue()) {
				id2Node.put(nd.id, nd);
			}
		}
		
		List<Node> orderedNodes = new ArrayList<>();
		
		for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
			for(Node nd : en.getValue()) {
				if(!nd.isProcessed) {
					expandClusterOrder(cellid2Nodes, id2Node, nd, qParams, orderedNodes);
					if(numCur4Bytes > Global.maxPidNeighbors4Bytes)	return null;
				}
			}
		}
		return orderedNodes;
	}
	
	private void expandClusterOrder(Map<Integer, List<Node>> cellid2Nodes, Map<Integer, Node> id2Node,
			Node centerNode, QueryParams qParams, List<Node> orderedNodes) throws Exception{
		NgbNodes neighbors = fastRange(cellid2Nodes, qParams, centerNode);
		if(neighbors == null)	centerNode.neighbors = null;
		else centerNode.neighbors = neighbors.toList();
		centerNode.isProcessed = Boolean.TRUE;
		centerNode.reachabilityDistance = Node.UNDEFINED;
//		centerNode.setCoreDistance(qParams, neighbors);
		centerNode.setCoreDistanceBySorted(qParams, centerNode.neighbors);
		if(centerNode.coreDistance == Node.UNDEFINED)	
			numCur4Bytes += 3;	// center_id core_dis reach_dis size(<id, disToCenter> . . .)
		else numCur4Bytes += 4 + 2 * neighbors.size();	// center_id core_dis reach_dis size(<id, disToCenter> . . .)
		if(numCur4Bytes > Global.maxPidNeighbors4Bytes)	return;
		
		orderedNodes.add(centerNode);
		OrderSeeds orderSeeds = new OrderSeeds();
		
		
		
//		long startTime = System.currentTimeMillis();
		
		
		
		
		if(centerNode.coreDistance != Node.UNDEFINED) {
			orderSeeds.update(id2Node, centerNode.neighbors, centerNode);
			while(!orderSeeds.isEmpty()) {
				
				
				
				
//				if(System.currentTimeMillis() - startTime >= 120 * 1000) {
//					System.out.println(Thread.currentThread() + " " + curSearchTerm + " " + cellid2Nodes.size());
//					break;
//				}
				
				
				
				
				
				centerNode = orderSeeds.pollFirst();
				
				
				
				
				neighbors = fastRange(cellid2Nodes, qParams, centerNode);
				if(neighbors == null)	centerNode.neighbors = null;
				else centerNode.neighbors = neighbors.toList();
				centerNode.isProcessed = Boolean.TRUE;
//				centerNode.setCoreDistance(qParams, neighbors);
				centerNode.setCoreDistanceBySorted(qParams, centerNode.neighbors);
				if(centerNode.coreDistance == Node.UNDEFINED)	
					numCur4Bytes += 3;	// center_id core_dis reach_dis size(<id, disToCenter> . . .)
				else numCur4Bytes += 4 + 2 * neighbors.size();	// center_id core_dis reach_dis size(<id, disToCenter> . . .)
				if(numCur4Bytes > Global.maxPidNeighbors4Bytes)	return;
				
				orderedNodes.add(centerNode);
				if(centerNode.coreDistance != Node.UNDEFINED) {
					orderSeeds.update(id2Node, centerNode.neighbors, centerNode);
				}
			}
		}
	}
	
	private void decreaseLastTerm() {
		int last = 0;
		if((last = numTerms.decrementAndGet()) != allTerms.length && (last % num1Percent) == 0) {
			MLog.log("共" + allTerms.length + ", 剩" + (last / num1Percent) + "%, " + last + "个");
		}
	}
	
	/**
	 * 该run将包含term的所有pid及其附近的neighbors数据拼接在一起，然后放到索引里面去
	 */
	public void run() {
		try {
			String term = null;
			List<String> sTerms = new ArrayList<>();
			while((curSearchTerm = term = termQueue.take()) != signOver) {
				sTerms.clear();
				sTerms.add(term);
				
				Map<Integer, List<Node>> cellid2Nodes = cellidWIndex.searchWords(sTerms, allLocations);
				if(null == cellid2Nodes) {
					this.writePidNeighborLen(term, -1);
					decreaseLastTerm();
					continue;
				}
				
				
				numCur4Bytes = 1;
				List<Node> ngbNodes = optics(cellid2Nodes, qParams);
				
				if(numCur4Bytes > Global.maxPidNeighbors4Bytes) {
					synchronized (numNgbTooLong) {
						numNgbTooLong.x++;
					}
					this.writePidNeighborLen(term, Integer.MAX_VALUE);
					decreaseLastTerm();
					continue;
				}
				byte[] pidNeighborsBytes = term2PidNeiIndex.pidNeighborsToBytes(ngbNodes, numCur4Bytes * 4);
				this.writePidNeighborLen(term, ngbNodes.size());
				this.addDoc(term, pidNeighborsBytes);
				
				decreaseLastTerm();
			}
			this.reduceThread();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * 该run将包含term的所有pid及其附近的neighbors数据不拼接在一起，而是一个节点一个节点依次放入
	 */
	public void run1() {
		try {
			String term = null;
			List<String> sTerms = new ArrayList<>();
			int numCur4Bytes = 0;
			List<Node> tList = null, ndList = null;
			int last = 0;
//			List<Node> tList = null, ndList = null;
			while((curSearchTerm = term = termQueue.take()) != signOver) {
				sTerms.clear();
				synchronized (numDealedTerm) {
//					System.out.print("> 正处理第" + String.valueOf(termIndex) + "个term，");
					if((++numDealedTerm.x) % 10000 == 0) {
						System.out.println("> 已处理" + String.valueOf(numDealedTerm) + "term， 总用时：" + TimeUtility.getGlobalSpendTime());
					}
				}
				sTerms.add(term);
				Map<Integer, List<Node>> cellid2Nodes = cellidWIndex.searchWords(sTerms, allLocations);
				if(null == cellid2Nodes) {
					this.writePidNeighborLen(term, -1);
					continue;
				}
				
//				int nn = 0;
//				for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
//					nn += en.getValue().size();
//				}
//				System.out.println("nn = " + String.valueOf(nn));
				
				numCur4Bytes = 0;
				Map<Integer, List<Node>> pidNeighbors = new HashMap<>();
				numCur4Bytes++;
				for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
					ndList = en.getValue();
					for(Node centerNode : ndList) {
						NgbNodes ngbNodes = fastRange(cellid2Nodes, qParams, centerNode);
						if(null==ngbNodes || ngbNodes.size() < qParams.minpts)	continue;
						tList = ngbNodes.toList();
						
//						if(tList.get(0).disToCenter != 0) {
//							System.out.println(termIndex + " " + term);
//							System.exit(0);
//						}
						
						pidNeighbors.put(centerNode.id, tList);
						numCur4Bytes += 2;
						numCur4Bytes += 3 * tList.size();
						if(numCur4Bytes > Global.maxPidNeighbors4Bytes)	break;
					}
					if(numCur4Bytes > Global.maxPidNeighbors4Bytes)	break;
				}
				if(numCur4Bytes > Global.maxPidNeighbors4Bytes) {
					synchronized (numNgbTooLong) {
						numNgbTooLong.x++;
					}
					this.writePidNeighborLen(term, Integer.MAX_VALUE);
					continue;
				}
				if(pidNeighbors.isEmpty()) {
					this.writePidNeighborLen(term, 0);
					continue;
				}
//				byte[] pidNeighborsBytes = term2PidNeiIndex.pidNeighborsToBytes(pidNeighbors, numCur4Bytes * 4);
				this.writePidNeighborLen(term, numCur4Bytes * 4);
				this.addDoc(term, pidNeighbors);
				
				if((last = numTerms.decrementAndGet() % num1Percent) == 0) {
					MLog.log("共" + allTerms.length + ", 剩" + (last / num1Percent) + "%, " + last + "个");
				}
			}
			this.reduceThread();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public NgbNodes fastRange(Map<Integer, List<Node>> cellid2Nodes, QueryParams qParams, Node qNode) throws Exception{
		sCircle.center = qNode.location.m_pCoords;
		List<CellSign> coveredCellids = sgplInfo.cover(sCircle);
		if(null == coveredCellids)	return null;
		
		/* fast range */
		NgbNodes ngb = new NgbNodes(Boolean.TRUE);
		List<Node> cellNodes = null;
		double dis = 0.0;
		
		for(CellSign cs : coveredCellids) {
			if(null == (cellNodes = cellid2Nodes.get(cs.getId())))	continue;
			for(Node nd : cellNodes) {
				dis = nd.location.getMinimumDistance(qNode.location);
				if(dis <= qParams.epsilon) {
					nd = nd.copy();
					nd.disToCenter = dis;
					ngb.add(dis, nd);
				}
			}
		}
		if(ngb.size() < qParams.minpts)	return null;
		return ngb;
	}
	
	private static void mainM() throws Exception {
		Term2PidNeighborsContainReachDisIndexBuilder2Optic.reset();
		
		MLog.log("starting build term2PidNeighborsIndex . . .");
		String[] allTerms = FileLoader.loadAllTerms(Global.pathWidTerms);
		
		int numThread = 30;

		ArrayBlockingQueue<String> termQueue = new ArrayBlockingQueue<>(numThread);
		for(int i=0; i<=numThread; i++) {
			new Thread(new Term2PidNeighborsContainReachDisIndexBuilder2Optic(allTerms, termQueue, Global.opticQParams, 
					Global.pathTerm2PidNeighborsIndex, Global.pathCellidRtreeidOrPidWordsIndex, Global.pathPidNeighborLen)).start();
		}
		
		for(String term : allTerms)	termQueue.put(term);
		for(int i=0; i<=numThread; i++)	termQueue.put(signOver);
		
		while(!Term2PidNeighborsContainReachDisIndexBuilder2Optic.hasStop())	Thread.sleep(5000);
	}
	
	private static void test() throws Exception {
//		Term2PidNeighborsContainReachDisIndexBuilder2Optic builder = new Term2PidNeighborsContainReachDisIndexBuilder2Optic(new String[2], 0, 0,Global.opticQParams, 
//				Global.pathTerm2PidNeighborsIndex, Global.pathCellidRtreeidOrPidWordsIndex, Global.pathPidNeighborLen);
//		List<String> sTerms = new ArrayList<>();
//		sTerms.add("9744");
//		Map<Integer, List<Node>> cellid2Nodes =  builder.cellidWIndex.searchWords(sTerms, allLocations);
//		if(cellid2Nodes != null) {
//			int num = 0;
//			for(Entry<Integer, List<Node>> en : cellid2Nodes.entrySet()) {
//				num += en.getValue().size();
//			}
//			System.out.println("NumSearchNode: " + num);
//		}
//		builder.numCur4Bytes = 1;
//		List<Node> nds = builder.optics(cellid2Nodes, builder.qParams);
//		System.out.println(nds);
	}
	
	public static void main(String[] args) throws Exception{
		mainM();
//		test();
	}
}
