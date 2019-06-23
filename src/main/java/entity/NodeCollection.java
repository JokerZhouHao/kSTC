package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeCollection extends PNodeCollection{
	private Map<Integer, Node> id2Node = new HashMap<>(); // record coordinate node and rtree node
	
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
}
