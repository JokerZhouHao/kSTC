package entity.optics;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.Map.Entry;

import entity.Node;

public class OrderSeeds {
	private TreeMap<Double, HashSet<Node>> seeds = new TreeMap<>();
	private int size = 0;
	
	public void add(double oldDis, Node nd) {
		HashSet<Node> ths = null;
		if(null != (ths = seeds.get(oldDis))) {
			ths.remove(nd);
			if(ths.isEmpty())	seeds.remove(oldDis);
		}
		if(!ths.contains(nd)) {
			ths.add(nd);
			size++;
		}
	}
	
	
	
	public Node pollFirst() {
		if(0==size)	return null;
		Entry<Double, HashSet<Node>> en = seeds.firstEntry();
		Node nd = en.getValue().iterator().next();
		size--;
		en.getValue().remove(nd);
		if(en.getValue().isEmpty()) {
			seeds.pollFirstEntry();
		}
		return nd;
	}
	
	public Boolean isEmpty() {
		if(0==size)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public int size() {
		return size;
	}
}
