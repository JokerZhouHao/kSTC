package entity.optics;

import java.util.HashSet;
import java.util.List;
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
		if(null == (ths = seeds.get(nd.reachabilityDistance))) {
			ths = new HashSet<>();
			seeds.put(nd.reachabilityDistance, ths);
		}
		ths.add(nd);
	}
	
	public void add(Node nd) {
		HashSet<Node> ths = null;
		if(null == (ths = seeds.get(nd.reachabilityDistance))) {
			ths = new HashSet<>();
			seeds.put(nd.reachabilityDistance, ths);
		}
		ths.add(nd);
		size++;
	}
	
	public Boolean contain(Node nd) {
		HashSet<Node> ths = null;
		if(null != (ths = seeds.get(nd.reachabilityDistance)))	return ths.contains(nd);
		else return Boolean.FALSE;
	}
	
	public Node pollFirst() {
		if(0==size)	return null;
		
		if(seeds.isEmpty()) {
			System.out.println();
		}
		
		Entry<Double, HashSet<Node>> en = seeds.firstEntry();
		Node nd = en.getValue().iterator().next();
		size--;
		en.getValue().remove(nd);
		if(en.getValue().isEmpty()) {
			seeds.pollFirstEntry();
		}
		return nd;
	}
	
	public void update(List<Node> neighbors, Node centerNode) {
		double cDist = centerNode.coreDistance;
		double newRDist = 0.0;
		double tDist = 0.0;
		for(Node nd : neighbors) {
			if(!nd.isProcessed) {
				newRDist = Math.max(cDist, centerNode.location.getMinimumDistance(nd.location));
				if(nd.reachabilityDistance == Node.UNDEFINED) {
					nd.reachabilityDistance = newRDist;
					this.add(nd);
				} else {
					if(newRDist < nd.reachabilityDistance) {
						tDist = nd.reachabilityDistance;
						nd.reachabilityDistance = newRDist;
						this.add(tDist, nd);
					}
				}
			}
		}
	}
	
	public Boolean isEmpty() {
		if(0==size)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public int size() {
		return size;
	}
}
