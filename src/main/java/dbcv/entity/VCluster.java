package dbcv.entity;

import java.util.ArrayList;
import java.util.List;

import spatialindex.spatialindex.Point;

/**
 * cluster
 * @author ZhouHao
 * @since 2019年7月6日
 */
public class VCluster {
	public final static double d = 2;
	public Point[] coords = null;
	public Integer id = -1;
	public volatile double DSC = Double.MAX_VALUE;
	public List<CNode> nds = null;
	public VGraph graph = null;
	
	public VCluster() {
		nds = new ArrayList<>();
	}
	
	public VCluster(Point[] coords, int id, List<CNode> nds) {
		this.coords = coords;
		this.id = id;
		this.nds = nds;
	}
	
	public VCluster copy() {
		VCluster clus = new VCluster();
		clus.coords = coords;
		clus.id = id;
		clus.DSC = DSC;
		for(CNode nd : nds) {
			clus.nds.add(nd.copy());
		}
		return clus;
	}
	
	private void calAllCoreDis() throws Exception{
		List<Double> distances = new ArrayList<Double>(nds.size());
		for(CNode nd : nds) {
			nd.calCoreDist(d, distances, nds);
			if(distances.size() > nds.size()) {
				throw new Exception("distances's size is more than nds's size");
			}
			distances.clear();
		}
	}
	
	private void displayAllNodes() {
		for(CNode nd : nds)
			System.out.println(nd);
	}
	
	public double mReachDis(CNode nd1, CNode nd2) throws Exception{
		return Math.max(nd1.coreDist(), Math.max(nd2.coreDist(), nd1.minDistance(nd2)));
	}
	
	public double getDSC() throws Exception{
		if(DSC != Double.MAX_VALUE)	return DSC;
		calAllCoreDis();
		graph = new VGraph(coords, nds);
		DSC = graph.maxWeightInMST();
		return DSC;
	}
	
	public int numNode() {
		return nds.size();
	}
	
	public static void main(String[] args) throws Exception {
		Point[] coords = {
				new Point(new double[]{0, 0}),
				new Point(new double[]{3, 4}),
				new Point(new double[]{6, 100}),
				new Point(new double[]{2, 1})
		};
		
		List<CNode> nds = new ArrayList<>();
		nds.add(new CNode(coords, 0, 0));
		nds.add(new CNode(coords, 1, 1));
		nds.add(new CNode(coords, 2, 2));
		nds.add(new CNode(coords, 3, 3));
		
		System.out.println("*****************  VCluster test ****************");
		VCluster clus1 = new VCluster(coords, 1, nds);
		clus1.calAllCoreDis();
		clus1.displayAllNodes();
		System.out.println(clus1.getDSC());
		
		System.out.println("*****************  V2Cluster test ***************");
		VCluster clus2 = new VCluster(coords, 1, nds);
		V2Cluster v2Clu = new V2Cluster(clus1.copy(), clus2.copy());
		System.out.println(v2Clu.getDSPC());
		
		System.out.println("*****************  V2ClusterCollection test ***************");
		VCluster clus3 = new VCluster(coords, 1, nds);
		List<VCluster> cluss = new ArrayList<>();
		cluss.add(clus1);
		cluss.add(clus2);
		cluss.add(clus3);
		V2ClusterCollection clusCol = new V2ClusterCollection(cluss.size());
		for(int i=0; i<cluss.size(); i++) {
			for(int j=i+1; j<cluss.size(); j++) {
				clusCol.set(i, j, new V2Cluster(cluss.get(i).copy(), cluss.get(j).copy()));
				System.out.println(clusCol.get(i, j).getDSPC());
			}
		}
		System.out.println();
	}
}
