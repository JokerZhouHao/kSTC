package entity.fastrange;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import entity.NeighborStack;
import entity.Node;
import utility.MComparator;

public class NgbNodes {
	private final static MComparator<Double> compDoubleDescend = new MComparator<Double>();
	private TreeMap<Double, LinkedList<Node>> nodes = new TreeMap<>(compDoubleDescend);
	public final static double signUsedKey = Double.MAX_VALUE;
	public int size = 0;
	
	public void add(double dis, Node node) {
		LinkedList<Node> ns = null;
		if(null == (ns = nodes.get(dis))) {
			ns = new LinkedList<>();
			nodes.put(dis, ns);
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
	
	public int size() {
		return size;
	}
}
