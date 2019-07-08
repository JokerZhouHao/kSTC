package dbcv;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import dbcv.entity.CNode;
import dbcv.entity.V2Cluster;
import dbcv.entity.V2ClusterCollection;
import dbcv.entity.VCluster;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.IOUtility;

/**
 * 计算dbcv
 * @author ZhouHao
 * @since 2019年7月7日
 */
public class DBCVCalculator {
	public static final VCluster signOver = new VCluster();
	
	public List<VCluster> cluss = null;
	public V2ClusterCollection v2ClusCol = null;
	public String path = null;
	public Point[] coords = Global.allLocations;
	
	public DBCVCalculator(String path) throws Exception{
		this.path = path;
		load();
	}
	
	/**
	 * 读取文件
	 * @throws Exception
	 */
	private void load() throws Exception{
		cluss = new ArrayList<>();
		BufferedReader br = IOUtility.getBR(path);
		br.readLine();
		br.readLine();
		int cid = -1;
		List<CNode> nds = null;
		String line = null;
		while(null != (line = br.readLine())) {
			if(line.startsWith("Cluster")) {
				cid++;
				nds = new ArrayList<>();
				cluss.add(new VCluster(coords, cid, nds));
				continue;
			}
			String[] arr = line.split(Global.delimiterLevel1);
			int nid = Integer.parseInt(arr[0]);
			int oid = Integer.parseInt(arr[1]);
			nds.add(new CNode(coords, nid, oid));
		}
		br.close();
	}
	
	private void calDSCOrDSPC() throws Exception {
		int numProcessor = Runtime.getRuntime().availableProcessors();
//		int numProcessor = 1;
		ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<>(numProcessor);
		DSCOrDSPCCalculator cals[] = new DSCOrDSPCCalculator[numProcessor];
		for(int i=0; i<numProcessor; i++) {
			cals[i] = new DSCOrDSPCCalculator(queue);
			new Thread(cals[i]).start();
		}
		
		// 计算DSC
		for(int i=0; i<cluss.size(); i++) {
			queue.put(cluss.get(i));
		}
		
		// 计算DSPC
		this.v2ClusCol = new V2ClusterCollection(cluss.size());
		for(int i=0; i<cluss.size(); i++) {
			for(int j=i+1; j<cluss.size(); j++) {
				this.v2ClusCol.set(i, j, new V2Cluster(cluss.get(i), cluss.get(j)));
				queue.put(this.v2ClusCol.get(i, j));
			}
		}
		
		// 发送结束信号
		for(int i=0; i<numProcessor; i++)	queue.put(signOver);
		while(!queue.isEmpty()) {
			Thread.sleep(2000);
		}
	}
	
	public double DBCV() {
		return 1.0;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		String path1 = Global.outPath + "res" + File.separator + 
				"result_ecu_base_optics_wu_rFanout=50.alpha=0.5.steepD=0.3.h=8.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=1.mpts=5.eps=0.001.xi=0.001.maxPNeiByte=2147483631";
		DBCVCalculator cal1 = new DBCVCalculator(path1);
		
		cal1.calDSCOrDSPC();
		
		ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(23);
		queue.add(1);
		
		Object obj = new VCluster();
		System.out.println(obj.getClass());
		
		System.out.println(Global.allLocations);
	}
}
