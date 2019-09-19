package entity.fastrange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
	
//	private LinkedList<Node> nodes = null;
//	private boolean isAscend = true;
//	public final static double signUsedKey = Double.MAX_VALUE;
//	private Set<Integer> recIds = new HashSet<>();
//	
//	public NgbNodes() {
//		nodes = new LinkedList<>();
//	}
//	
//	public NgbNodes(Boolean isAscend) {
//		nodes = new LinkedList<>();
//		this.isAscend = isAscend;
//	}
//	
//	public void add(double dis, Node node) {
//		if(recIds.contains(node.id))	return;
//		else recIds.add(node.id);
//		
//		node.disToCenter = dis;
//		if(nodes.isEmpty() || dis == NgbNodes.signUsedKey)	{
//			nodes.add(node);
//			return;
//		}
//		int i = 0;
//		for(i=0; i<nodes.size(); i++) {
//			if(nodes.get(i).equals(node))	return;
//			else if(isAscend && nodes.get(i).disToCenter > dis)		break;
//			else if(!isAscend && nodes.get(i).disToCenter < dis)	break;
//		}
//		if(i < nodes.size())	nodes.add(i, node);
//		else nodes.add(node);
//	}
//	
//	public LinkedList<Node> toList(){
//		LinkedList<Node> nds = new LinkedList<>();
//		for(Node nd : nodes) {
//			if(nd.disToCenter == NgbNodes.signUsedKey)	break;
//			nds.add(nd);
//		}
//		return nds;
//	}
//	
//	public LinkedList<Node> toListContainAll(){
//		return nodes;
//	}
//	
//	public int size() {
//		return nodes.size();
//	}
	
	public static void main(String[] args) {
		NgbNodes ngb = new NgbNodes(false);
		
		int i = 1;
		Node nd1 = new Node();
		nd1.id = i;
		nd1.disToCenter = i;
		ngb.add(i, nd1);
		
		i = 5;
		Node nd5 = new Node();
		nd5.id = i;
		nd5.disToCenter = i;
		ngb.add(i, nd5);
		
		i = 4;
		Node nd4 = new Node();
		nd4.id = i;
		nd4.disToCenter = i;
		ngb.add(i, nd4);
		
		i = 3;
		Node nd3 = new Node();
		nd3.id = i;
		nd3.disToCenter = i;
		ngb.add(i, nd3);
		
		i = 2;
		Node nd2 = new Node();
		nd2.id = i;
		nd2.disToCenter = i;
		ngb.add(i, nd2);
		
		i = 6;
		Node nd6 = new Node();
		nd6.id = i;
		nd6.disToCenter = i;
		ngb.add(i, nd6);
		
		LinkedList<Node> list = ngb.toList();
		for(Node nd : list) {
			System.out.println(nd.disToCenter);
		}
		
	}
}
