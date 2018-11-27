package entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import entity.fastrange.NgbNodes;

import java.util.TreeMap;

import utility.MComparator;

public class NeighborStack {
	private LinkedList<TreeMap<Double, LinkedList<Node>>> dis2Nodes = new LinkedList<>();
	TreeMap<Double, LinkedList<Node>> nearestTree = new TreeMap<>();
	
	public void push(TreeMap<Double, LinkedList<Node>> nodes) {
		dis2Nodes.add(nodes);
	}
	
	public Node pollFirst() {
		if(dis2Nodes.isEmpty())	return null;
		TreeMap<Double, LinkedList<Node>> dNodes = null;
		Entry<Double, LinkedList<Node>> en = null;
		Node nd = null;
		if(nearestTree.isEmpty()) {
			while(Boolean.TRUE) {
				dNodes = dis2Nodes.getFirst();
				while(Boolean.TRUE) {
					en = dNodes.firstEntry();
					if(en.getKey() == NgbNodes.signUsedKey) {
						dNodes.pollFirstEntry();
						if(dNodes.isEmpty()) {
							dis2Nodes.removeFirst();
							if(dis2Nodes.isEmpty())	return null;
						}
					} else {
						nearestTree = dNodes;
						nd = en.getValue().removeFirst();
						if(en.getValue().isEmpty()) {
							dNodes.pollFirstEntry();
							if(dNodes.isEmpty()) {
								dis2Nodes.removeFirst();
								if(dis2Nodes.isEmpty())	return null;
							}
						}
						break;
					}
				}
				if(null != nd)	break;
			}
		} else {
			en = nearestTree.firstEntry();
			nd = en.getValue().pollFirst();
			if(en.getValue().isEmpty()) {
				nearestTree.pollFirstEntry();
				if(nearestTree.isEmpty()) {
					dis2Nodes.removeFirst();
				}
			}
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
