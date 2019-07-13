package test;

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
		
		Rectangle subRect = new Rectangle(0, 1, 0, 1);
		System.out.println(subRect);
		
		String line = "\nss";
		line = line.replace('\n', ' ');
		System.out.println(line);
		
		System.out.println("23,23".replaceAll(",", " "));
		
		
		float a = 1.123456789f;
		System.out.println(a);
	}
}
