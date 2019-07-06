package dbcv.entity;

import java.util.List;

public class V2Cluster extends VCluster {
	private static final int MAX_NUM_CLUSTER = 10000;	// 簇数量上界
	private static final double INVALID_DSPC = Double.MAX_VALUE;
	
	public int id = -1;
	private double DSPC = INVALID_DSPC;
	public VCluster clus1 = null;
	public VCluster clus2 = null;
	
	public V2Cluster(VCluster clus1, VCluster clus2) {
		this.clus1 = clus1;
		this.clus2 = clus2;
	}
	
	private void calAllCoreDis() {
		List<CNode> nds = clus1.nds;
		for(CNode nd : nds) {
			nd.calCoreDist(d, clus1.nds, clus2.nds);
		}
		nds = clus2.nds;
		for(CNode nd : nds) {
			nd.calCoreDist(d, clus1.nds, clus2.nds);
		}
	}
	
	public double getDSPC() throws Exception{
		if(DSPC != INVALID_DSPC)	return DSPC;	
		calAllCoreDis();
		DSPC = INVALID_DSPC;
		for(CNode nd1 : clus1.nds) {
			for(CNode nd2 : clus2.nds) {
				double mReachDis = mReachDis(nd1, nd2);
				DSPC = DSPC <= mReachDis ? DSPC : mReachDis;
			}
		}
		return DSPC;
	}
	
}
