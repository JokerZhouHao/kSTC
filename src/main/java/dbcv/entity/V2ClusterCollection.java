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
		int k = x;
		if(x > y) {
			x = y;
			y = k;
		} else if(x == y) throw new Exception("V2ClusterCollection index : x = y = " + x);
		int index = (2 * (n - 1) - x + 1) * x / 2  + (y - x) - 1;
		if(index >= cluss.length || index < 0)	
			throw new Exception("V2ClusterCollection index 越界: " + n + " (" + x + ", " + y + ") " + index);
		return index;
	}
	
	public void displayAllIndexs() throws Exception{
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				if(i >= j)	System.out.print(String.format("%-4s", ""));
				else System.out.print(String.format("%-4s", index(i, j)));
			}
			System.out.println();
		}
		
	}
	
	public void displayAllDSPC() throws Exception{
		for(int i=0; i<n; i++) {
			for(int j=i+1; j<n; j++) {
				System.out.print(String.format("%-4.5f", cluss[index(i, j)].getDSPC()));
			}
			System.out.println();
		}
	}
	
	public void set(int x, int y, V2Cluster clus) throws Exception {
		int index = index(x, y);
		if(cluss[index] == null) cluss[index] = clus;	
	}
	
	public V2Cluster get(int x, int y) throws Exception {
		return cluss[index(x, y)];
	}
	
	public static void main(String[] args) throws Exception{
		V2ClusterCollection col = new V2ClusterCollection(10);
		col.displayAllIndexs();
	}
}
