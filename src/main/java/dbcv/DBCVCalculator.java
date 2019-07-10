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
import entity.Cluster;
import entity.Node;
import entity.SortedClusters;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.SystemInfoUtility;
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
	public final static double INVALID_DBCV = Double.MAX_VALUE; 
	private double dbcv = INVALID_DBCV;
	private boolean hasCalDSCOrDSPC = Boolean.FALSE;
	private int numTotalNode = 0;
	
	public DBCVCalculator(String path, int numTotalNode) throws Exception{
		this.path = path;
		this.numTotalNode = numTotalNode;
		load();
	}
	
	public DBCVCalculator(SortedClusters sClu, int numTotalNode) throws Exception {
		this.numTotalNode = numTotalNode;
		cluss = new ArrayList<>();
		int cid = -1;
		List<CNode> nds = null;
		int id = 0;
		for(Cluster clu : sClu.getClusters()) {
			cid++;
			nds = new ArrayList<>();
			cluss.add(new VCluster(coords, cid, nds));
			for(Node nd : clu.getPNodes()) {
				nds.add(new CNode(coords, id++, nd.id));
			}
		}
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
		int id = 0;
		while(null != (line = br.readLine())) {
			if(line.startsWith("Cluster")) {
				cid++;
				nds = new ArrayList<>();
				cluss.add(new VCluster(coords, cid, nds));
				continue;
			}
			String[] arr = line.split(Global.delimiterLevel1);
//			int nid = Integer.parseInt(arr[0]);
			int oid = Integer.parseInt(arr[1]);
			nds.add(new CNode(coords, id++, oid));
		}
		br.close();
	}
	
	/**
	 * calDSCOrDSPC
	 * @throws Exception
	 */
	private void calDSCOrDSPC() throws Exception {
		if(hasCalDSCOrDSPC)	return;
		
		int numProcessor = 56;
//		if(SystemInfoUtility.isWindow())	numProcessor = 4;
		
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
		
		hasCalDSCOrDSPC = Boolean.TRUE;
	}
	
	/**
	 * 计算VC
	 * @param i
	 * @return
	 * @throws Exception
	 */
	private double VC(int i) throws Exception{
		double min = Double.MAX_VALUE;
		for(int j=0; j < cluss.size(); j++) {
			if(i == j)	continue;
			min = min <= v2ClusCol.get(i, j).getDSPC() ? min : v2ClusCol.get(i, j).getDSPC();
		}
		double dsc = cluss.get(i).getDSC();
		return (min - dsc) / (min >= dsc ? min : dsc);
	}
	
	/**
	 * 计算DBCV
	 * @return
	 * @throws Exception
	 */
	public double DBCV() throws Exception{
		if(dbcv != INVALID_DBCV)	return dbcv;
		if(!hasCalDSCOrDSPC)	calDSCOrDSPC();
		dbcv = 0.0;
		for(int i=0; i<cluss.size(); i++) {
			dbcv += cluss.get(i).numNode() * VC(i);
		}
		dbcv /= numTotalNode;
		return dbcv;
	}
	
	public static double DBCV(String path, int numTotalNode) throws Exception{
		DBCVCalculator cal = new DBCVCalculator(path, numTotalNode);
		return cal.DBCV();
	}
	
	public static double DBCV(SortedClusters sClu, int numTotalNode) throws Exception{
		DBCVCalculator cal = new DBCVCalculator(sClu, numTotalNode);
		return cal.DBCV();
	}
	
	public static void main(String[] args) throws Exception {
		
		String path = Global.outPath + "res" + File.separator + 
				"result_ecu_base_optics_rFanout=50.alpha=0.5.steepD=0.3.h=8.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=1.mpts=5.eps=0.001.xi=0.01.maxPNeiByte=2147483631";
		System.out.println(path + "\n" + DBCVCalculator.DBCV(path, 188523) + "\n");
		
		path = Global.outPath + "res" + File.separator + 
				"result_ecu_base_optics_wu_rFanout=50.alpha=0.5.steepD=0.3.h=8.om=1.oe=1.0E-4.ns=200.t=4.k=100000.nw=1.mpts=5.eps=0.001.xi=0.01.maxPNeiByte=2147483631";
		System.out.println(path + "\n" + DBCVCalculator.DBCV(path, 188523) + "\n");
		
	}
}
