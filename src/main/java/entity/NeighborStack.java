package entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import utility.MComparator;

public class NeighborStack {
	private LinkedList<TreeMap<Double, LinkedList<Node>>> dis2Nodes = new LinkedList<>();
	
	public void push(TreeMap<Double, LinkedList<Node>> nodes) {
		dis2Nodes.add(nodes);
	}
	
	public Node pollFirst() {
		if(dis2Nodes.isEmpty())	return null;
		TreeMap<Double, LinkedList<Node>> dNodes = dis2Nodes.getFirst();
		LinkedList<Node> nodes = dNodes.firstEntry().getValue();
		Node nd = nodes.removeFirst();
		if(nodes.isEmpty()) {
			dNodes.pollFirstEntry();
			if(dNodes.isEmpty())	dis2Nodes.pollFirst();
		}
		return nd;
	}
	
	public static void main(String[] args) throws Exception{
		TreeMap<Double, String> disNodes = new TreeMap<>(new MComparator<Double>());
		disNodes.put(1.0, "1");
		disNodes.put(2.0, "2");
		disNodes.put(3.0, "3");
		
		for(Entry<Double, String> en : disNodes.entrySet()) {
			System.out.println(en.getKey() + " " + en.getValue());
		}
	}
}
