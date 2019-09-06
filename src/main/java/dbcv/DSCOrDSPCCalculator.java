package dbcv;

import java.util.concurrent.ArrayBlockingQueue;

import dbcv.entity.V2Cluster;
import dbcv.entity.V2ClusterCollection;
import dbcv.entity.VCluster;

/**
 * 计算DSC、DSPC
 * @author ZhouHao
 * @since 2019年7月7日
 */
public class DSCOrDSPCCalculator implements Runnable{
	private ArrayBlockingQueue<Object> queue = null;
	
	public DSCOrDSPCCalculator(ArrayBlockingQueue<Object> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			while(true) {
				Object obj = queue.take();
				if(obj == DBCVCalculator.signOver) {
					System.out.println("get signOver");
					return;
				}
				if(obj.getClass().equals(VCluster.class))	((VCluster)obj).getDSC();
				else if(obj.getClass().equals(V2Cluster.class))	((V2Cluster)obj).getDSPC();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
