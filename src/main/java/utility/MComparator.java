package utility;

import java.util.Comparator;

import entity.Cluster;
import entity.Node;

public class MComparator<T> implements Comparator<T> {
	private int type = -1; // 0 is compare node distance, 1 is compare node score
	
	public MComparator() {}
	
	public MComparator(int type) {
		this.type = type;
	}
	
	@Override
	public int compare(T o1, T o2) {
		if(0==type) {
			Node node1 = (Node)o1;
			Node node2 = (Node)o2;
			if(node1.distance > node2.distance)	return 1;
			else if(node1.distance == node2.distance)	return 0;
			else return -1;
		} else if(1==type) {
			Node node1 = (Node)o1;
			Node node2 = (Node)o2;
			if(node1.score > node2.score)	return 1;
			else if(node1.score == node2.score)	return 0;
			else return -1;
		} else if(o1 instanceof Cluster) {
			Cluster c1 = (Cluster)o1;
			Cluster c2 = (Cluster)o2;
			if(c1.getScore() > c2.getScore()) return 1;
			else if(c1.getScore() == c2.getScore()) return 0;
			else return -1;
		}
		return 0;
	}
	
}
