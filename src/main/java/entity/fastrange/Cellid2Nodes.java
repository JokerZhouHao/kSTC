package entity.fastrange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Node;

public class Cellid2Nodes {
	public Map<Integer, List<Node>> cellid2Nodes = new HashMap<>();
	public List<Node> pNodes = new ArrayList<>();
	private List<Node> tList = null;
	
	public void add(Node node) {
		if(node.id < 0)	return;
		pNodes.add(node);
		if(null == (tList = cellid2Nodes.get(node.cellId))) {
			tList = new ArrayList<>();
			cellid2Nodes.put(node.cellId, tList);
		}
		tList.add(node);
	}
	
	public Boolean isEmpty() {
		if(cellid2Nodes.isEmpty())	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public int numCell() {
		return cellid2Nodes.size();
	}
	
	public int numNode() {
		return pNodes.size();
	}
}
