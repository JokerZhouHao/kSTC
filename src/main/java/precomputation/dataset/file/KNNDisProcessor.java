package precomputation.dataset.file;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import entity.KSortedCollection;
import entity.Node;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.MLog;
import utility.index.rtree.MRTree;
import utility.io.IOUtility;
import utility.io.TimeUtility;

public class KNNDisProcessor implements Runnable{
	public static AtomicInteger numTask = new AtomicInteger(0);
	private int k = 0;
	
	public KNNDisProcessor(int k) {
		numTask.incrementAndGet();
		this.k = k;
	}
	
	public static Boolean over() {
		if(numTask.get() == 0)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	@Override
	public void run() {
		try {
			generateKNeighborDisFile(k);
			numTask.decrementAndGet();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
	}

	/**
	 * generate K Neighbor Dis File
	 * @param filePath
	 * @param k
	 * @throws Exception
	 */
	public void generateKNeighborDisFile(int k) throws Exception{
		String filePath = Global.outPath + "KNNNeighborDis_" + k + ".txt";
		MLog.log("start build " + filePath);
		Point[] allPoints = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
		MRTree rtree = MRTree.getInstanceInDisk(Boolean.FALSE);
		List<entity.Node> neighbors = null;
		KSortedCollection<entity.Node> tNodes = null;
		KSortedCollection<entity.Node> allSortedNodes = new KSortedCollection<>(Integer.MAX_VALUE);
		double radius = 0.00002;
		for(int i=0; i<allPoints.length; i++) {
			radius = 0.00002;
			neighbors = null;
			while(null == neighbors || neighbors.size() < k) {
				radius *= 5;
				neighbors = rtree.rangeQuery(allPoints[i], radius, allPoints);
			}
			tNodes = new KSortedCollection<entity.Node>(k, neighbors);
			
			//// test 
//			Node nd = tNodes.getK();
//			if(nd.distance == 0.0) {
//				System.out.println("center id: " + i);
//				List<Node> nds = tNodes.toList();
//				for(Node no : nds) {
//					System.out.println("id: " + no.id);
//				}
//				return;
//			}
			
			allSortedNodes.add(new entity.Node(i, allPoints[i], tNodes.getK().distance, 0.0));
		}
		
		BufferedWriter bw = IOUtility.getBW(filePath);
		bw.write(Global.delimiterPound + String.valueOf(allSortedNodes.size()) + "\n");
		for(entity.Node nd : allSortedNodes.toList()) {
			bw.write(String.valueOf(nd.distance));
			bw.write(Global.delimiterLevel1);
			bw.write(String.valueOf(nd.id));
			bw.write('\n');
		}
		bw.close();
		MLog.log("over k=" + k + ", spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	public static void main(String[] args) throws Exception {
		List<Integer> ks = new ArrayList<>();
		ks.add(3);
		ks.add(5);
//		ks.add(10);
//		ks.add(50);
//		ks.add(100);
		long start = System.currentTimeMillis();
		MLog.log("开始计算" + ks + " KNNDis . . . ");
		for(int k : ks) {
			new Thread(new KNNDisProcessor(k)).start();
		}
		
		do {
			Thread.sleep(2000);
		}while(!KNNDisProcessor.over());
		
		MLog.log("over, spend time: " + TimeUtility.getSpendTimeStr(start, System.currentTimeMillis()));
		
//		generateKNeighborDisFile(3);
////		generateKNeighborDisFile(4);
//		generateKNeighborDisFile(5);
////		generateKNeighborDisFile(6);
////		generateKNeighborDisFile(7);
//		generateKNeighborDisFile(10);
//		generateKNeighborDisFile(50);
//		generateKNeighborDisFile(100);
////		generateKNeighborDisFile(150);
	}
}
