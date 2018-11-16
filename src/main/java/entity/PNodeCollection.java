package entity;

import java.util.ArrayList;
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
	
	public PNodeCollection(List<Node> nodes) {
		this.pNodes.addAll(nodes);
	}
	
	public void add(Node node) {
		pNodes.add(node);
	}
	
	public void sortByDistance() {
		pNodes.sort(cptDis);
	}
	
	public void sortByScore() {
		pNodes.sort(cptScore);
	}
	
	public Node next() {
		if(curIndex==pNodes.size())	return null;
		else return pNodes.get(curIndex++);
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
