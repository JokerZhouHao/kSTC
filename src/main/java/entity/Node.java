package entity;

import java.util.HashSet;
import java.util.Set;

import spatialindex.spatialindex.Point;

public class Node {
	public Integer id = 0;	// >=0 is pid, <0 is rtree node
	public Point location = null;
	public double distance = 0;	// the distance of the node to searched location
	public double score = 0;	// the relation of the node to searched words
	public int clusterId = 0;	// the clusterId of the node, -1 is noise, 0 is init value, >0 is clusterId
	
	public Node(int id, Point location, double distance, double score) {
		this.id = id;
		this.location = location;
		this.distance = distance;
		this.score = score;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(id == ((Node)obj).id)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}

	@Override
	public String toString() {
		return "SearchedNode [id=" + id + ", distance=" + distance + ", score=" + score + ", clusterId=" + clusterId
				+ "][" + String.valueOf(location.getCoord(0)) + ", " + String.valueOf(location.getCoord(1) + "]");
	}
	
	public static void main(String[] args) {
		double[] location = {1, 2};
		Point po = new Point(location);
		Set<Node> set = new HashSet<>();
		Node node1 = new Node(0, po, 0, 0);
		Node node2 = new Node(0, po, 0, 0);
		Node node3 = new Node(0, po, 0, 0);
		set.add(node1);
		set.add(node2);
		set.add(node3);
		
		Node tnode = new Node(0, po, 0, 0);
		System.out.println(set.contains(tnode));
		
	}
}