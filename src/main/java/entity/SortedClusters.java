package entity;

import java.util.ArrayList;
import java.util.Collections;

import spatialindex.spatialindex.Point;
import utility.Global;
import utility.MComparator;

public class SortedClusters {
	private ArrayList<Cluster> clusters = new ArrayList<>();
	private QueryParams qParams = null;
	private static final MComparator<Cluster> comp = new MComparator<>();
	
	public SortedClusters(Point qPoint) {
		this.qParams = new QueryParams();
		this.qParams.location = qPoint;
	}
	
	public SortedClusters(Point qPoint, int k) {
		this.qParams = new QueryParams();
		this.qParams.location = qPoint;
		this.qParams.k = k;
	}
	
	public SortedClusters(QueryParams qParams) {
		this.qParams = qParams;
	}
	
	public void add(Cluster cluster) {
//		this.clusters.add(cluster);
		int index = Collections.binarySearch(clusters, cluster, comp);
		
		if(index < 0) {
			index = -index - 1;
		} else index++;
		if(index==qParams.k) return;
		if(index == clusters.size())	clusters.add(cluster);
		else {
			clusters.add(index, cluster);
		}
		if(clusters.size() > qParams.k)	clusters.remove(clusters.size() - 1);
	}
	
	public ArrayList<Cluster> getClusters() {
		return clusters;
	}
	
	public double getTopKScore() {
		if(clusters.size() < qParams.k)	return Double.MAX_VALUE;
		else return clusters.get(qParams.k-1).getScore();
	}
	
	public Point getqPoint() {
		return this.qParams.location;
	}
	
	public int getSize() {
		return clusters.size();
	}
	
	public void holdTopK(int k) {
		ArrayList<Cluster> cs = new ArrayList<>();
		if(clusters.size() >= k) {
			for(int i=0; i<k; i++)	cs.add(clusters.get(i));
		} else cs.addAll(clusters);
		this.clusters.clear();
		this.clusters = cs;
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(Global.delimiterPound);
		sb.append(qParams.toString());
		sb.append('\n');
		
		for(Cluster clu : clusters) {
			sb.append("Cluster:" + String.valueOf(clu.getId()) + " " + 
					 String.valueOf(clu.getMinDisAndScore()[0]) + " " + 
					 String.valueOf(clu.getMinDisAndScore()[1]) + " " + 
					 String.valueOf(clu.getScore()) + 
					 "\n");
			for(Node nd : clu.getPNodes()) {
				sb.append(nd.toString());
				
//				sb.append(String.valueOf(nd.clusterId));
//				sb.append(Global.delimiterLevel1);
//				sb.append(String.valueOf(nd.id));
//				sb.append(Global.delimiterLevel1);
//				sb.append(String.valueOf(nd.location.getCoord(0)));
//				sb.append(Global.delimiterSpace);
//				sb.append(String.valueOf(nd.location.getCoord(1)));
				
//				sb.append(Global.delimiterSpace);
//				sb.append(String.valueOf(nd.distance));
//				sb.append(Global.delimiterSpace);
//				sb.append(String.valueOf(nd.score));
//				sb.append(Global.delimiterSpace);
//				sb.append(String.valueOf(0.5 * nd.distance + 0.5 * nd.score));
				
				sb.append('\n');
			}
		}
		return sb.toString();
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
