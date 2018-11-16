package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spatialindex.spatialindex.Point;
import utility.MComparator;

public class PNodeCollection {
	private List<Node> pNodes = new ArrayList<>();	// just record coordinate node
	private int curIndex = 0; 
	private static final MComparator<Node> cptDis = new MComparator<>(0);
	private static final MComparator<Node> cptScore = new MComparator<>(1);
	
	public PNodeCollection() {}
	
	public PNodeCollection(Collection<Node> nodes) {
		this.pNodes.addAll(nodes);
	}
	
	public void add(Node node) {
		pNodes.add(node);
	}
	
	public PNodeCollection sortByDistance() {
		pNodes.sort(cptDis);
		return this;
	}
	
	public PNodeCollection sortByScore() {
		pNodes.sort(cptScore);
		return this;
	}
	
	public Node first() {
		return pNodes.get(curIndex);
	}
	
	public Node next() {
		while(curIndex != pNodes.size() && !pNodes.get(curIndex++).isInitStatus());
		if(curIndex==pNodes.size())	return null;
		return pNodes.get(curIndex);
	}
	
	public double[] getMinDisAndScore() {
		double[] disAndScore = {Double.MAX_VALUE, Double.MAX_VALUE};
		for(Node nd : this.pNodes) {
			if(disAndScore[0] > nd.distance) disAndScore[0] = nd.distance;
			if(disAndScore[1] > nd.score) disAndScore[1] = nd.score;
		}
		return disAndScore;
	}
	
	
	
	public List<Node> getPNodes() {
		return pNodes;
	}

	public PNodeCollection copy() {
		return new PNodeCollection(pNodes);
	}
	
	public static void main(String args[]) throws Exception{
		double[] location = {1, 2};
		Point po = new Point(location);
		PNodeCollection pc = new NodeCollection();
		Node node1 = new Node(2, po, 2, 10);
		Node node2 = new Node(3, po, 3, 9);
		Node node3 = new Node(1, po, 1, 1);
		pc.add(node1);
		pc.add(node2);
		pc.add(node3);
//		pc.sortByDistance();
		pc.sortByScore();
		
		for(Node node : pc.getPNodes()) {
			System.out.println(node);
		}
		
		
	}
}
