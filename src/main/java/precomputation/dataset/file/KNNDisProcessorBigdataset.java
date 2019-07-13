package precomputation.dataset.file;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import entity.CellSign;
import entity.Circle;
import entity.Id2Distance;
import entity.KSortedCollection;
import entity.Node;
import entity.SGPLInfo;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.MComparator;
import utility.MLog;
import utility.index.rtree.MRTree;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * 用于计算大数据集的KNNDis
 * @author ZhouHao
 * @since 2019年7月13日
 */
public class KNNDisProcessorBigdataset implements Runnable{
	
	private static Point[] allPoints = null;
	private static AtomicInteger numNeedDealNode = null;
	private final static int signOverThread = -1;
	private ArrayBlockingQueue<Integer> queue = null;
	private static SGPLInfo sgpl = null;
	private static Map<Integer, List<Integer>> cell2ids = null;
	private static Id2Distance[][] arrDis = null;
	private static List<Integer> ks = null;
	private static int kMax = -1;
	private static double radiusInit = -1;
	private Circle circle = null;
	
	private KNNDisProcessorBigdataset(List<Integer> ks, int h, double radiusInit, ArrayBlockingQueue<Integer> queue){
		synchronized (KNNDisProcessorBigdataset.class) {
			if(allPoints == null)	allPoints = Global.allLocations;
			if(numNeedDealNode == null)	numNeedDealNode = new AtomicInteger(allPoints.length);
			if(sgpl == null)	sgpl = SGPLInfo.getInstance(h);
			if(cell2ids == null)	cell2ids = FileLoader.loadCell2Nids(sgpl);
			if(KNNDisProcessorBigdataset.ks == null)	KNNDisProcessorBigdataset.ks = ks;
			if(arrDis == null)		arrDis = new Id2Distance[ks.size()][Global.allLocations.length];
			if(kMax == -1)	kMax = ks.get(ks.size() - 1);
			if(KNNDisProcessorBigdataset.radiusInit == -1)	KNNDisProcessorBigdataset.radiusInit = radiusInit;
		}
		this.queue = queue;
		circle = new Circle(radiusInit, allPoints[0], sgpl);
	}
	
	/**
	 * 范围查找
	 * @param circle
	 * @param centerId
	 * @return
	 */
	private List<Id2Distance> fastRange(Circle circle, int centerId) {
		List<CellSign> coveredCellids = sgpl.cover(circle);
		List<Id2Distance> res = new ArrayList<>();
		List<Integer> ids = null;
		double dis = 0;
		for(CellSign cs : coveredCellids) {
			if(null == (ids = cell2ids.get(cs.getId())))	continue;
			for(int id : ids) {
				dis = allPoints[id].getMinimumDistance(allPoints[centerId]);
				if(dis <= circle.radius) {
					res.add(new Id2Distance(centerId, dis));
				}
			}
		}
		return res;
	}
	
	/**
	 * 判断任务是否已经结束
	 * @return
	 */
	private static Boolean over() {
		if(numNeedDealNode.get() == 0)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	/**
	 * decrease
	 * @return
	 */
	private static int decrease() {
		int num = numNeedDealNode.decrementAndGet();
		if(num % 100000 == 0)	MLog.log("last " + num);
		return num;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				int id = queue.take();
				if(id == signOverThread)	break;
				List<Id2Distance> res = null;
				circle.radius = radiusInit;
				circle.center = allPoints[id].m_pCoords;
				while(true) {
					res = fastRange(circle, id);
					
//					MLog.log(circle.radius + "   " + res.size());
					
					if(res.size() >= kMax) {
						KSortedCollection<Id2Distance> col = new KSortedCollection<Id2Distance>(kMax, res);
						for(int i=0; i<arrDis.length; i++) {
							arrDis[i][id] = col.get(ks.get(i));
						}
						break;
					} else circle.radius *= 5;
				}
				decrease();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * 写KNNDistance文件
	 * @param path
	 * @param diss
	 * @throws Exception
	 */
	private static void writeKNNDistance(String path, Id2Distance[] diss) throws Exception {
		BufferedWriter bw = IOUtility.getBW(path);
		bw.write(Global.delimiterPound + String.valueOf(diss.length) + "\n");
		for(Id2Distance id2dis : diss) {
			bw.write(String.valueOf(id2dis.distance));
			bw.write(Global.delimiterLevel1);
			bw.write(String.valueOf(id2dis.id));
			bw.write('\n');
		}
		bw.close();
	}
	
	/**
	 * 计算ks
	 * @param ks
	 * @param h
	 * @param radiusInit
	 * @throws Exception
	 */
	public static void calKNNDistance(List<Integer> ks, int h, double radiusInit) throws Exception{
		MLog.log("start cal " + ks + " KNNDistance . . . ");
		long startTime = System.currentTimeMillis();
		int numThread = 4;
		if(Global.inputPath.contains("places_dump"))	numThread = 80;
		ArrayBlockingQueue<Integer> queueIds = new ArrayBlockingQueue<>(numThread);
		int i=0;
		for(i=0; i<numThread; i++) {
			new Thread(new KNNDisProcessorBigdataset(ks, h, radiusInit, queueIds)).start();
		}
		for(i=0; i<allPoints.length; i++) {
			queueIds.put(i);
		}
		for(i=0; i<numThread; i++) {
			queueIds.put(signOverThread);
		}
		while(true) {
			if(!over())	Thread.sleep(5000);
			else break;
		}
		for(i=0; i<arrDis.length; i++) {
			Arrays.parallelSort(arrDis[i], new MComparator<Id2Distance>());
			String filePath = Global.outPath + "KNNNeighborDis_" + ks.get(i) + ".txt";
			writeKNNDistance(filePath, arrDis[i]);
			MLog.log("success write " + filePath);
		}
		
		MLog.log("over, spend time: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	/**
	 * 主方法
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Global.displayInputOutputPath();
		
		List<Integer> ks = new ArrayList<>();
		ks.add(3);
		ks.add(5);
		ks.add(10);
		ks.add(50);
		ks.add(100);
		Collections.sort(ks);
//		calKNNDistance(ks, 10, 0.0005);	// yelp_buss
//		calKNNDistance(ks, 4, 0.1);		// test_dataset
		calKNNDistance(ks, 16, 0.00005);
	}
}
