package precomputation.dataset.file.optic;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import entity.CellSign;
import entity.Circle;
import entity.Node;
import entity.QueryParams;
import entity.SGPLInfo;
import entity.fastrange.NgbNodes;
import index.CellidPidWordsIndex;
import index.optic.Term2PidNeighborsIndex;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * 创建计算<term, <<pid, neighbors><pid, neighbors><pid, neighbors> . . . >索引
 * @author ZhouHao
 * @since 2019年4月27日
 */
public class Term2PidNeighborsIndexBuilder implements Runnable{
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
	
	private String[] allTerms = null;
	private int start = 0;
	private int end = 0;
	private QueryParams qParams = null;
	private Circle sCircle = null;
	private String descript = null;
	
	public Term2PidNeighborsIndexBuilder(String[] allTerms, int start, int end, QueryParams qParams, String pathTerm2PidNei, String pathCellidRtreeidOrPidWordsIndex,
			String pathPidNeighborLen) throws Exception{
		descript = String.valueOf(allTerms.length) + " : [" + String.valueOf(start) + ", " + String.valueOf(end) + "]";
		this.addThread();
		this.allTerms = allTerms;
		this.start = start;
		this.end = end;
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
		}
	}
	
	public  void addThread() {
		synchronized (numThread) {
			numThread.x++;
			System.out.println("> 开始处理区间" + descript);
		}
	}
	
	public void reduceThread() throws Exception{
		synchronized (numThread) {
			--numThread.x;
			System.out.println("> Over处理区间 " + descript + ", 剩" + String.valueOf(numThread) + "个区间在处理, 总用时：" + TimeUtility.getGlobalSpendTime());
			if(0 == numThread.x) {
				clean();
				System.out.println("> Over, 共处理" + String.valueOf(allTerms.length) + "个term，numNgbTooLong = " + String.valueOf(numNgbTooLong) + ", 总用时：" + TimeUtility.getGlobalSpendTime());
			}
		}
	}
	
	public static Boolean hasStop() throws Exception {
		if(0 == numThread.x) return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public void clean() throws Exception{
		term2PidNeiIndex.close();
		pidNeighborLenBW.close();
		cellidWIndex.close();
	}
	
	/**
	 * 该run将包含term的所有pid及其附近的neighbors数据拼接在一起，然后放到索引里面去
	 */
	public void run() {
		try {
			String term = null;
			List<String> sTerms = new ArrayList<>();
			int numCur4Bytes = 0;
			List<Node> tList = null, ndList = null;
			for(int termIndex = start; termIndex < end; termIndex++) {
				term = allTerms[termIndex];
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
						
						if(tList.get(0).disToCenter != 0) {
							System.out.println(termIndex + " " + term);
							System.exit(0);
						}
						
						pidNeighbors.put(centerNode.id, tList);
						numCur4Bytes += 2;
//						numCur4Bytes += 2 * tList.size();	// int float
						numCur4Bytes += 3 * tList.size();	// int double
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
				byte[] pidNeighborsBytes = term2PidNeiIndex.pidNeighborsToBytes(pidNeighbors, numCur4Bytes * 4);
				this.writePidNeighborLen(term, pidNeighborsBytes.length);
				this.addDoc(term, pidNeighborsBytes);
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
			for(int termIndex = start; termIndex < end; termIndex++) {
				term = allTerms[termIndex];
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
						
						if(tList.get(0).disToCenter != 0) {
							System.out.println(termIndex + " " + term);
							System.exit(0);
						}
						
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
		if(0==ngb.size())	return null;
		return ngb;
	}
	
	
	public static void main(String[] args) throws Exception{
		/********** 重置参数  ******************/ 
		numThread = new TInt(0);
		numNgbTooLong = new TInt(0);
		term2PidNeiIndex = null;
		pidNeighborLenBW = null;
		sgplInfo = Global.sgplInfo;
		cellidWIndex = null;
		allLocations = null;
		numDealedTerm = new TInt(0);
		
		
		System.out.println("> starting build term2PidNeighborsIndex . . .");
		String[] allTerms = FileLoader.loadAllTerms(Global.pathWidTerms);
//		32102
//		new Thread(new Term2PidNeighborsIndexBuilder(allTerms, 845, 860, Global.opticQParams, 
//				Global.pathTerm2PidNeighborsIndex, Global.pathCellidRtreeidOrPidWordsIndex, Global.pathPidNeighborLen)).start();
		
		
//		if(numThread >= allTerms.length) {
//			new Thread(new Term2PidNeighborsIndexBuilder(allTerms, 0, allTerms.length, Global.opticQParams, 
//					Global.pathTerm2PidNeighborsIndex, Global.pathCellidRtreeidOrPidWordsIndex, Global.pathPidNeighborLen)).start();
//			return;
//		}
//		
		int numThread = 10;
		int start = 0;
		int end = 750;
		int span = (end - start) / numThread;
		for(int i=0; i<=numThread; i++) {
			if(i!=numThread) {
				new Thread(new Term2PidNeighborsIndexBuilder(allTerms, start + span * i, start + span * (i+1), Global.opticQParams, 
						Global.pathTerm2PidNeighborsIndex, Global.pathCellidRtreeidOrPidWordsIndex, Global.pathPidNeighborLen)).start();
			} else {
				if(start + span * i < end) {
					new Thread(new Term2PidNeighborsIndexBuilder(allTerms, start + span * i, end, Global.opticQParams, 
							Global.pathTerm2PidNeighborsIndex, Global.pathCellidRtreeidOrPidWordsIndex, Global.pathPidNeighborLen)).start();
				}
			}
		}
		
		numThread = 10;
		start = end;
		end = allTerms.length;
		span = (end - start) / numThread;
		for(int i=0; i<=numThread; i++) {
			if(i!=numThread) {
				new Thread(new Term2PidNeighborsIndexBuilder(allTerms, start + span * i, start + span * (i+1), Global.opticQParams, 
						Global.pathTerm2PidNeighborsIndex, Global.pathCellidRtreeidOrPidWordsIndex, Global.pathPidNeighborLen)).start();
			} else {
				if(start + span * i < end) {
					new Thread(new Term2PidNeighborsIndexBuilder(allTerms, start + span * i, end, Global.opticQParams, 
							Global.pathTerm2PidNeighborsIndex, Global.pathCellidRtreeidOrPidWordsIndex, Global.pathPidNeighborLen)).start();
				}
			}
		}
		
		while(!Term2PidNeighborsIndexBuilder.hasStop())	Thread.sleep(30000);
	}
}
