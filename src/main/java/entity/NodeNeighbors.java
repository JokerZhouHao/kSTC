package entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class NodeNeighbors{
	private Node centerP = null;
	private LinkedList<Node> neighbors = null;
	private Set<Node> neighborsSet = null;
	
	public NodeNeighbors(Node center, Collection neighbors) {
		this.centerP = center;
		if(null==neighbors)	return;
		if(neighbors instanceof List) {
			neighbors = new LinkedList<>();
			neighbors.addAll(neighbors);
		} else if(neighbors instanceof Set) {
			neighborsSet = new HashSet<>();
			neighborsSet.addAll(neighbors);
		}
	}
	
	public Boolean isAllNeighborClassified() {
		if(null != neighbors) {
			for(Node nd : neighbors) {
				if(!nd.isClassified())	return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
	
	public Boolean noAllNeighborInitState() {
		if(null != neighbors) {
			for(Node nd : neighbors) {
				if(nd.isInitStatus())	return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
	
	public Boolean isCenterPClassified() {
		if(centerP.isClassified())	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	public Node getCenterP() {
		return centerP;
	}
}
