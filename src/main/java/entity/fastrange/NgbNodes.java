package entity.fastrange;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import entity.Node;
import utility.MComparator;

public class NgbNodes {
	private final static MComparator<Double> compDoubleDescend = new MComparator<Double>();
	private TreeMap<Double, LinkedList<Node>> nodes = null;
	public final static double signUsedKey = Double.MAX_VALUE;
	public int size = 0;
	
	public NgbNodes() {
		nodes = new TreeMap<>(compDoubleDescend);
	}
	
	public NgbNodes(Boolean isAscend) {
		if(isAscend) {
			nodes = new TreeMap<>();
		} else nodes = new TreeMap<>(compDoubleDescend);
	}
	
	public void add(double dis, Node node) {
		LinkedList<Node> ns = null;
		if(null == (ns = nodes.get(dis))) {
			ns = new LinkedList<>();
			nodes.put(dis, ns);
		} else {
			for(Node nd : ns) {
				if(nd.equals(node)) return;
			}
		}
		ns.add(node);
		size++;
	}
	
	public LinkedList<Node> toList(){
		LinkedList<Node> nds = new LinkedList<>();
		for(Entry<Double, LinkedList<Node>> en : nodes.entrySet()) {
			if(en.getKey() != NgbNodes.signUsedKey) {
				nds.addAll(en.getValue());
			}
		}
		return nds;
	}
	
	public LinkedList<Node> toListContainAll(){
		LinkedList<Node> nds = new LinkedList<>();
		for(Entry<Double, LinkedList<Node>> en : nodes.entrySet()) {
			nds.addAll(en.getValue());
		}
		return nds;
	}
	
	public int size() {
		return size;
	}
}
