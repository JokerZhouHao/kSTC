package entity;

import java.util.ArrayList;
import java.util.Collections;

import spatialindex.spatialindex.Point;
import utility.MComparator;

public class SortedClusters {
	private ArrayList<Cluster> clusters = new ArrayList<>();
	private Point qPoint = null;
	private int k = Integer.MAX_VALUE;
	private static final MComparator<Cluster> comp = new MComparator<>();
	
	public SortedClusters(Point qPoint) {
		this.qPoint = qPoint;
	}
	
	public SortedClusters(Point qPoint, int k) {
		this.qPoint = qPoint;
		this.k = k;
	}
	
	public void add(Cluster cluster) {
//		this.clusters.add(cluster);
		int index = Collections.binarySearch(clusters, cluster, comp);
		
		if(index < 0) {
			index = -index - 1;
		} else index++;
		if(index==k) return;
		if(index == clusters.size())	clusters.add(cluster);
		else {
			clusters.add(index, cluster);
		}
		if(clusters.size() > k)	clusters.remove(clusters.size() - 1);
	}
	
	public ArrayList<Cluster> getClusters() {
		return clusters;
	}
	
	public double getTopKScore() {
		if(clusters.size() < k)	return Double.MAX_VALUE;
		else return clusters.get(k-1).getScore();
	}
	
	public Point getqPoint() {
		return qPoint;
	}
	
	public void holdTopK(int k) {
		ArrayList<Cluster> cs = new ArrayList<>();
		if(clusters.size() >= k) {
			for(int i=0; i<k; i++)	cs.add(clusters.get(i));
		} else cs.addAll(clusters);
		this.clusters.clear();
		this.clusters = cs;
	}
	
	public static void main(String[] args) throws Exception{
		SortedClusters sc = new SortedClusters(null, 1);
		sc.add(new Cluster(2, 2));
		sc.add(new Cluster(1, 1));
		sc.add(new Cluster(9, 9));
		sc.add(new Cluster(8, 8));
		sc.add(new Cluster(1, 2));
		sc.add(new Cluster(0, 0));
		
		for(Cluster cl : sc.getClusters()) {
			System.out.println(cl);
		}
	}
}
