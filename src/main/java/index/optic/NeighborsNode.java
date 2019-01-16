package index.optic;

import entity.Node;

public class NeighborsNode {
	public int id = 0;
	public double disToCenter = 0.0;
	public NeighborsNode(int id, double disToCenter) {
		this.id = id;
		this.disToCenter = disToCenter;
	}
	
	public NeighborsNode(Node node) {
		this(node.id, node.disToCenter);
	}
	
	@Override
	public String toString() {
		return "NeighborsNode [id=" + id + ", disToCenter=" + disToCenter + "]";
	}
	
}
