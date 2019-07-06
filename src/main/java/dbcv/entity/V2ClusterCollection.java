package dbcv.entity;

import java.util.List;

/**
 * V2Cluster集合
 * @author ZhouHao
 * @since 2019年7月6日
 */
public class V2ClusterCollection {
	private int n = 0;
	private V2Cluster[] cluss = null;
	
	public V2ClusterCollection(int n) {
		this.n = n;
		cluss = new V2Cluster[n * (n - 1) / 2];
	}
	
	private int index(int x, int y) throws Exception{
		int index = (2 * n - x - 1) / 2 * x + (y - x) - 1;
		if(index >= cluss.length || index < 0)	
			throw new Exception("V2ClusterCollection index 越界: " + n + " (" + x + ", " + y + ") " + index);
		return index;
	}
	
	public void set(int x, int y, V2Cluster clus) throws Exception {
		int index = index(x, y);
		if(cluss[index] == null) cluss[index] = clus;	
	}
	
	public V2Cluster get(int x, int y) throws Exception {
		return cluss[index(x, y)];
	}
}
