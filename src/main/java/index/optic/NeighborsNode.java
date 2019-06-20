package index.optic;

import entity.Node;

public class NeighborsNode {
	public Integer id = 0;
	public double disToCenter = 0.0;
	public NeighborsNode(int id, double disToCenter) {
		this.id = id;
		this.disToCenter = disToCenter;
	}
	
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Node) {
			if(id == ((Node)obj).id)	return Boolean.TRUE;
			else return Boolean.FALSE;
		} else if(obj instanceof NeighborsNode) {
			if(id == ((NeighborsNode)obj).id)	return Boolean.TRUE;
			else return Boolean.FALSE;
		} else return false;
	}



	public NeighborsNode(Node node) {
		this(node.id, node.disToCenter);
	}
	
	@Override
	public String toString() {
		return "NeighborsNode [id=" + id + ", disToCenter=" + disToCenter + "]";
	}
	
}
