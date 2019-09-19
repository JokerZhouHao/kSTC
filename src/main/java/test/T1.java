package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import entity.Node;
import entity.Rectangle;
import index.optic.NeighborsNode;
import utility.DatasetType;
import utility.MComparator;

class A1{}
class A2 extends A1{}

public class T1 {
	
	public static void main(String[] args) {
//		System.out.println(Double.isNaN(0.0/0.0));
//		System.out.println(1/0.000000000000000000001);
		
//		Rectangle subRect = new Rectangle(0, 1, 0, 1);
//		System.out.println(subRect);
//		
//		String line = "\nss";
//		line = line.replace('\n', ' ');
//		System.out.println(line);
//		
//		System.out.println("23,23".replaceAll(",", " "));
//		
//		
//		float a = 1.123456789f;
//		System.out.println(a);
		
//		HashMap<Node, Integer> map = new HashMap<>();
//		int x = 16164;
//		Integer xx = x;
//		int y = 16164;
//		Integer yy = y;
//		System.out.println(xx.hashCode() + " " + yy.hashCode());
		
		
//		Node nd1 = new Node(16164, null, 0.0, 0.0);
//		Node nd2 = new Node(16164, null, 0.0, 0.0);
//		
//		System.out.println(nd1.equals(nd2));
//		
//		map.put(nd1, nd1.id);
//		System.out.println(map.containsKey(nd2));
		
//		ArrayList<Integer> list = new ArrayList<>();
//		list.add(1);
//		list.add(3);
//		list.add(5);
//		System.out.println(Collections.binarySearch(list, 4));
		
		ArrayList<String> list1 = new ArrayList<>();
		list1.add("a");		list1.add("b");
		ArrayList<String> list2 = new ArrayList<>();
		list2.add("a");		list2.add("b");
		System.out.println(list1.hashCode() == list2.hashCode());
		System.out.println(list1.equals(list2));
	}
}
