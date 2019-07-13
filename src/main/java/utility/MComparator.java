package utility;

import java.util.Comparator;

import entity.Cluster;
import entity.Id2Distance;
import entity.Node;

public class MComparator<T> implements Comparator<T> {
	private int type = -1; // 0 is compare node distance, 1 is compare node score，3 is compare node id, 4 is compare node disToCenter
	
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
		} else if(3==type) {
			Node node1 = (Node)o1;
			Node node2 = (Node)o2;
			if(node1.id > node2.id)	return 1;
			else if(node1.id == node2.id)	return 0;
			else return -1;
		} else if(4==type) {
			Node node1 = (Node)o1;
			Node node2 = (Node)o2;
			if(node1.disToCenter > node2.disToCenter)	return 1;
			else if(node1.disToCenter == node2.disToCenter)	return 0;
			else return -1;
		} else if(o1 instanceof Cluster) {
			Cluster c1 = (Cluster)o1;
			Cluster c2 = (Cluster)o2;
			if(c1.getScore() > c2.getScore()) return 1;
			else if(c1.getScore() == c2.getScore()) return 0;
			else return -1;
		} else if(o1 instanceof Double) {
			Double d1 = (Double)o1;
			Double d2 = (Double)o2;
			if(d1 > d2) return -1;
			else if(d1==d2)	return 0;
			else return 1;
		} else if(o1 instanceof Id2Distance) {
			Id2Distance i2d1 = (Id2Distance)o1;
			Id2Distance i2d2 = (Id2Distance)o2;
			if(i2d1.distance > i2d2.distance)	return 1;
			else if(i2d1.distance == i2d2.distance)	return 0;
			else return -1;
		}
		return 0;
	}
	
}
