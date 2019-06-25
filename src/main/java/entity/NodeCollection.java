package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import index.CellidPidWordsIndex;
import utility.Global;

public class NodeCollection extends PNodeCollection{
	public Map<Integer, Node> id2Node = new HashMap<>(); // record coordinate node and rtree node
	
	public NodeCollection() {
		super();
	}
	
	public void add(Node node) {
		if(node.id >= 0) super.add(node);
		id2Node.put(node.id, node);
	}
	
	public Node get(int nid) {
		return id2Node.get(nid);
	}
	
	public PNodeCollection getPNodeCollection() {
		return (PNodeCollection)this;
	}
	
	public int size() {
		return id2Node.size();
	}
	
	public Map<Integer, Node> id2Node() {
		return id2Node;
	}
	
	public Map<Integer, List<Node>> toCellid2Nodes(){
		Map<Integer, List<Node>> cellid2Nds = new HashMap<>();
		List<Node> list = null;
		for(Entry<Integer, Node> en : id2Node.entrySet()) {
			if(en.getValue().cellId == CellidPidWordsIndex.signRtreeNode)	continue;
			if(null == (list = cellid2Nds.get(en.getValue().cellId))) {
				list = new ArrayList<>();
				cellid2Nds.put(en.getValue().cellId, list);
			}
			list.add(en.getValue());
		}
		return cellid2Nds;
	}
	
	public List<Node> toListNoRtreeNode(){
		List<Node> nds = new ArrayList<>();
		for(Entry<Integer, Node> en : id2Node.entrySet()) {
			if(en.getValue().id < 0)	continue;
			nds.add(en.getValue());
		}
		return nds;
	}
}
