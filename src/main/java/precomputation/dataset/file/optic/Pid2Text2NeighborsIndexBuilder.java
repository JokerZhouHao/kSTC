package precomputation.dataset.file.optic;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import entity.Cell;
import entity.CellSign;
import entity.Circle;
import entity.Node;
import entity.QueryParams;
import entity.SGPLInfo;
import entity.fastrange.NgbNodes;
import index.CellidPidWordsIndex;
import index.optic.Pid2Text2NeighborIndex;
import index.optic.Term2PidNeighborsIndex;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.index.rtree.MRTree;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * 创建<words, <<pid + neighbors>, <pid + neighbors>, <pid + neighbors>>索引索引
 * @author ZhouHao
 * @since 2019年4月27日
 */
public class Pid2Text2NeighborsIndexBuilder implements Runnable{
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
	private static Pid2Text2NeighborIndex pid2TermsNeighborsIndex = null;
	private static Point[] allLocations = null;
	private static TInt numDealedPid = new TInt(0);
	private static Map<Integer, List<Integer>> cellid2Pids = null;
	
	private String[] allTexts = null;
	private int start = 0;
	private int end = 0;
	private QueryParams qParams = null;
	private Circle sCircle = null;
	private String descript = null;
	
	private MRTree rtree= null;
	
	public Pid2Text2NeighborsIndexBuilder(String[] allTexts, int start, int end, QueryParams qParams, String pathPid2Terms2Nei) throws Exception{
		descript = String.valueOf(allTexts.length) + " : [" + String.valueOf(start) + ", " + String.valueOf(end) + "]";
		this.addThread();
		this.allTexts = allTexts;
		this.start = start;
		this.end = end;
		this.qParams = qParams;
		this.sCircle = new Circle(qParams.epsilon, new double[2]);
		this.rtree = MRTree.getInstanceInDisk(Boolean.FALSE);
		synchronized (Pid2Text2NeighborsIndexBuilder.class) {
			if(null == pid2TermsNeighborsIndex) {
				pid2TermsNeighborsIndex = new Pid2Text2NeighborIndex(pathPid2Terms2Nei);
				pid2TermsNeighborsIndex.openIndexWriter();
			}
			if(null == cellid2Pids) {
				cellid2Pids = FileLoader.loadCellid2Pids(Global.pathCell2Pids);
			}
			if(null == allLocations) {
				allLocations = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
			}
		}
	}
	
	public void addDoc(String text, int pid, List<Node> neighbors) throws Exception{
		synchronized (pid2TermsNeighborsIndex) {
			pid2TermsNeighborsIndex.addDoc(text, pid, neighbors);
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
				System.out.println("> Over, 共处理" + String.valueOf(allTexts.length) + "个pid, 总用时：" + TimeUtility.getGlobalSpendTime());
			}
		}
	}
	
	public void clean() throws Exception{
		pid2TermsNeighborsIndex.close();
	}
	
	/**
	 * 该run将包含term的所有pid及其附近的neighbors数据拼接在一起，然后放到索引里面去
	 */
	public void run() {
		try {
			String text = null;
			for(int tIndex = start; tIndex < end; tIndex++) {
				text = allTexts[tIndex];
				synchronized (numDealedPid) {
//					System.out.println("> 正处理第" + String.valueOf(numDealedPid) + "个pid，");
					if((++numDealedPid.x) % 10000 == 0) {
						System.out.println("> 已处理" + String.valueOf(numDealedPid) + "pid， 总用时：" + TimeUtility.getGlobalSpendTime());
					}
				}
				
				this.sCircle.center = allLocations[tIndex].m_pCoords;
				List<Node> neighbors = fastRange(allLocations[tIndex], sCircle);
				addDoc(text, tIndex, neighbors);
			}
			this.reduceThread();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public List<Node> fastRange(Point qPoint, Circle circle) throws Exception{
		NgbNodes ngb = new NgbNodes(Boolean.TRUE);
		List<CellSign> cells = Global.sgplInfo.cover(circle);
		List<Integer> pids = null;
		double dis = 0;
		for(CellSign cell : cells) {
			if(null != (pids = cellid2Pids.get(cell.getId()))) {
				for(Integer pid : pids) {
					dis = qPoint.getMinimumDistance(allLocations[pid]);
					if(dis <= circle.radius) {
						ngb.add(dis, new Node(pid, allLocations[pid], dis, 0));
					}
				}
			}
		}
		return ngb.toList();
	}
	
	
	public static void main(String[] args) throws Exception{
		System.out.println("> starting build Pid2Text2NeighborsIndex . . .");
		
		String[] allText = FileLoader.loadAllTerms(Global.pathIdText);
//		
		int numThread = 20;
		int start = 0;
		int end = allText.length;
		int span = (end - start) / numThread;
		for(int i=0; i<=numThread; i++) {
			if(i!=numThread) {
				new Thread(new Pid2Text2NeighborsIndexBuilder(allText, start + span * i, start + span * (i+1), Global.opticQParams, 
						Global.pathPid2Terms2NeighborsIndex)).start();
			} else {
				if(start + span * i < end) {
					new Thread(new Pid2Text2NeighborsIndexBuilder(allText, start + span * i, end, Global.opticQParams, 
							Global.pathPid2Terms2NeighborsIndex)).start();
				}
			}
		}
	}
}
