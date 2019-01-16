package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import spatialindex.spatialindex.Point;
import utility.MComparator;

public class KSortedCollection<T> {
	private int k = 0;
	private ArrayList<T> nodes = null;
	private final static MComparator<Node> cptNode = new MComparator<>(0);
	private final static MComparator<Node> cptNodeDisToCenter = new MComparator<>(4);
	private int typeCpt = -1; // 0 is cptNodeDisToCenter
	public final static int CPTNODEDISTOCENTER = 0;
	
	public KSortedCollection(int k) {
		this.k = k;
		nodes = new ArrayList<>();
	}
	
	public KSortedCollection(int k, Collection<T> collection) {
		this(k);
		for(T t : collection) {
			this.add(t);
		}
	}
	
	public KSortedCollection(int typeCpt, int k, Collection<T> collection) {
		this.typeCpt = typeCpt;
		this.k = k;
		this.nodes = new ArrayList<>();
		for(T t : collection) {
			this.add(t);
		}
	}
	
	public void add(T t) {
		int index = 0;
		if(typeCpt==CPTNODEDISTOCENTER) index = Collections.binarySearch((ArrayList<Node>)nodes, (Node)t, cptNodeDisToCenter);
		else if(t instanceof Node) index = Collections.binarySearch((ArrayList<Node>)nodes, (Node)t, cptNode);
		else if(t instanceof Integer) index = Collections.binarySearch((ArrayList<Integer>)nodes, (Integer)t);
		else if(t instanceof Double) index = Collections.binarySearch((ArrayList<Double>)nodes, (Double)t);
		else if(t instanceof Float) index = Collections.binarySearch((ArrayList<Float>)nodes, (Float)t);
		
		if(index < 0) {
			index = -index - 1;
		} else index++;
		if(index==k) return;
		if(index == nodes.size())	nodes.add(t);
		else {
			nodes.add(index, t);
		}
		if(nodes.size() > k)	nodes.remove(nodes.size() - 1);
	}
	
	public T getK() {
		if(nodes.size() == k)	return nodes.get(k - 1);
		else return null;
	}
	
	public T last() {
		if(nodes.isEmpty())	return null;
		else return nodes.get(nodes.size() - 1);
	}
	
	public int size() {
		return nodes.size();
	}
	
	public List<T> toList(){
		return nodes;
	}
	
	@Override
	public String toString() {
		return "KSortedCollection [k=" + k + ", nodes=" + nodes + "]";
	}
	
	public static void main(String[] args) throws Exception{
		double[] ds =  {1.0, 2.0};
		Point po = new Point(ds);
		Node nd1 = new Node(2, po, 2, 3);
		Node nd2 = new Node(1, po, 1, 4);
		Node nd3 = new Node(3, po, 3, 1);
		Node nd4 = new Node(4, po, 4, 0);
		KSortedCollection<Node> kNodes = new KSortedCollection<>(3);
		List<Node> nodes = new ArrayList<>();
		nodes.add(nd1);
		nodes.add(nd2);
		nodes.add(nd3);
		nodes.add(nd4);
		kNodes = new KSortedCollection<>(Integer.MAX_VALUE, nodes);
		System.out.println(nodes);
	}
	
}
