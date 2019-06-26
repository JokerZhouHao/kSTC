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
	protected List<Node> pNodes = new ArrayList<>();	// just record coordinate node
	private int curIndex = -1; 
	private static final MComparator<Node> cptDis = new MComparator<>(0);
	private static final MComparator<Node> cptScore = new MComparator<>(1);
	
	private int indexFirstNoUsed = 0;
	
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
	
	public Node first(int offset) {
		int i = curIndex + offset;
		if(i<0) return pNodes.get(0);
		else if(i>=pNodes.size())	return pNodes.get(pNodes.size()-1);
		else return pNodes.get(i);
	}
	
	public Node next() {
		do {
			curIndex++;
		} while(curIndex != pNodes.size() && pNodes.get(curIndex).isClassified());
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
	
	public int refreshFirstIndex() {
		for(int i=indexFirstNoUsed; i<pNodes.size(); i++) {
			if(!pNodes.get(i).isUsed) {
				indexFirstNoUsed = i;
				return i;
			}
		}
		return -1;
	}
	
	public double getFirstNoUsedDis() {
		if(indexFirstNoUsed==pNodes.size())	return 1000;
		return pNodes.get(indexFirstNoUsed).distance;
	}
	
	public double getFirstNoUsedScore() {
		if(indexFirstNoUsed==pNodes.size())	return 1000;
		return pNodes.get(indexFirstNoUsed).score;
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
