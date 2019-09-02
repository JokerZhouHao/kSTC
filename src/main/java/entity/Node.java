package entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import entity.fastrange.NgbNodes;
import index.optic.NeighborsNode;
import spatialindex.spatialindex.Point;
import utility.Global;

public class Node implements Serializable, Comparable{
	public Integer id = 0;	// >=0 is pid, <0 is rtree node
	public Integer cellId = 0;
	public int orderId = -1;
	public Point location = null;
	public double distance = 0;	// the distance of the node to searched location
	public double score = 0;	// 1 - the relation of the node to searched words
	public int clusterId = 0;	// the clusterId of the node, -1 is noise, 0 is init value, >0 is clusterId
	
	public static final double UNDEFINED = 1.5;
	public double coreDistance = UNDEFINED;
	public double reachabilityDistance = UNDEFINED;
	public Boolean isProcessed = Boolean.FALSE;
	public double disToCenter = 0.0;
	
	public Boolean isUsed = Boolean.FALSE;
	
	public LinkedList<Node> neighbors = null;
	
	public Node() {}
	
	public Node(int id, Point location, double distance, double score) {
		this.id = id;
		this.location = location;
		this.distance = distance;
		this.score = score;
	}
	
	public Node(int id, Point location, double distance, double score, int ceId) {
		this.id = id;
		this.location = location;
		this.distance = distance;
		this.score = score;
		this.cellId = ceId;
	}
	
	@Override
	public int compareTo(Object o) {
		Node nd = (Node)o;
		if(this.id > nd.id)	return 1;
		else if(this.id == nd.id)	return 0;
		else return -1;
	}


	public Node copy() {
		Node nd = new Node(id, location, distance, score);
		nd.clusterId = clusterId;
		nd.coreDistance = coreDistance;
		nd.reachabilityDistance = reachabilityDistance;
		nd.isProcessed = isProcessed;
		nd.disToCenter = disToCenter;
		nd.isUsed = isUsed;
		return nd;
	}
	
	public Boolean hasInCluster(int clusterId) {
		if(this.clusterId == clusterId)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public Boolean isInitStatus() {
		if(clusterId == 0)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public Boolean isNoise() {
		if(clusterId < 0)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public Boolean isClassified() {
		if(clusterId > 0)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public void setToNoise() {
		this.clusterId = -1;
	}
	
	public void setToInit() {
		this.clusterId = 0;
	}
	
	public void setCoreDistance(QueryParams qParams, List<Node> neighbors) {
		if(neighbors == null || neighbors.size() < qParams.minpts)	this.coreDistance = UNDEFINED;
		else {
			this.coreDistance = new KSortedCollection<Node>(KSortedCollection.CPTNODEDISTOCENTER, qParams.minpts, neighbors).getK().disToCenter;
		}
	}
	
	public void setCoreDistanceBySorted(QueryParams qParams, List<Node> neighbors) {
		if(neighbors == null || neighbors.size() < qParams.minpts)	this.coreDistance = UNDEFINED;
		else {
			this.coreDistance = neighbors.get(qParams.minpts - 1).disToCenter;
		}
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Node) {
			if(id == ((Node)obj).id)	return Boolean.TRUE;
			else return Boolean.FALSE;
		} else if(obj instanceof NeighborsNode) {
			if(id == ((NeighborsNode)obj).id)	return Boolean.TRUE;
			else return Boolean.FALSE;
		} else return false;
	}
	
	@Override
	public String toString() {
		return orderId + Global.delimiterLevel1 + id + Global.delimiterLevel1 + location.getCoord(0) + Global.delimiterSpace + location.getCoord(1);
//		return "SearchedNode [id=" + id + ", distance=" + distance + ", score=" + score + ", clusterId=" + clusterId
//				+ "][" + String.valueOf(location.getCoord(0)) + ", " + String.valueOf(location.getCoord(1) + "]");
	}
	
	public static void main(String[] args) {
		double[] location = {1, 2};
		Point po = new Point(location);
		Set<Node> set = new HashSet<>();
		Node node1 = new Node(0, po, 0, 0);
		Node node2 = new Node(0, po, 2, 0);
		Node node3 = new Node(0, po, 4, 5);
		set.add(node1);
		set.add(node2);
		set.add(node3);
		
//		Node tnode = new Node(0, po, 0, 0);
//		System.out.println(set.contains(tnode));
		System.out.println(set.size());
		
	}
}
