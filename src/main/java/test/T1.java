package test;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import entity.Node;
import index.optic.NeighborsNode;
import utility.DatasetType;
import utility.MComparator;

class A1{}
class A2 extends A1{}

public class T1 {
	
	
	public static void main(String[] args) {
//		System.out.println(Path.getBasePath());
//		System.out.println(DatasetType.values()[0]);
//		System.out.println(0.19999999999999998/0.1);
//		long lo = 1999999999999999800l;
//		System.out.println(lo);
//		TreeMap<Integer, Integer> tm = new TreeMap<>();
//		tm.put(1, 1);
//		TreeSet<Integer> ts = new TreeSet<>();
//		ts.add(12);
//		ts.add(3);
//		System.out.println(ts.pollFirst());
		
//		HashSet<Integer> hs = new HashSet<>();
//		hs.iterator().next();
//		
//		TreeSet<Node> ts = new TreeSet<>(new MComparator<>(3));
//		ts.add(new Node(5, null, 6, 4));
//		ts.add(new Node(1, null, 3, 5));
//		ts.add(new Node(3, null, 1, 3));
//		System.out.println("zhou");
//		System.out.println((int)(1.7/1));
		
//		Node n1 = new Node(1, null, 1, 0);
//		Node n2 = new Node(2, null, 2, 2);
//		TreeMap<Double, Node> tm = new TreeMap<>();
//		tm.put(1.0, n1);
//		tm.put(2.0, n2);
//		
//		System.out.println(tm.firstEntry().getValue());
//		tm.pollFirstEntry();
//		System.out.println(tm.firstEntry().getValue());
		
//		Set<Node> nds = new HashSet<>();
//		nds.add(new Node(1, null, 0, 0));
//		System.out.println(nds.contains(new NeighborsNode(-1, 1)));
//		System.out.println(Double.MAX_VALUE);
//		System.out.println(1 > Double.MIN_VALUE);
		
//		System.out.println(Math.pow(2, 10000));
		
//		System.out.println(Integer.MAX_VALUE);
//		A2 a2 = new A2();
//		System.out.println(a2.getClass().equals(A2.class));
		
		PriorityQueue<Integer> heap = new PriorityQueue<>();
		heap.add(4);
		heap.add(2);
		System.out.println(heap.poll());
	}
}
