package test;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import entity.Node;
import utility.DatasetType;
import utility.MComparator;

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
		
		HashSet<Integer> hs = new HashSet<>();
		hs.iterator().next();
		
		TreeSet<Node> ts = new TreeSet<>(new MComparator<>(3));
		ts.add(new Node(5, null, 6, 4));
		ts.add(new Node(1, null, 3, 5));
		ts.add(new Node(3, null, 1, 3));
		System.out.println("zhou");
	}
}
