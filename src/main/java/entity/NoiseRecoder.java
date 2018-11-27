package entity;

import java.util.LinkedList;

public class NoiseRecoder {
	private LinkedList<NodeNeighbors>  disList = new LinkedList<>();
	private LinkedList<NodeNeighbors> scoList = new LinkedList<>();
	
	public NoiseRecoder() {}
	
	public double[] getMinDisAndSco() {
		this.refresh();
		double[] disAndSco = {Double.MAX_VALUE, Double.MAX_VALUE};
		if(!disList.isEmpty())	disAndSco[0] = disList.getFirst().getCenterP().distance;
		if(!scoList.isEmpty())	disAndSco[1] = scoList.getFirst().getCenterP().score;
		return disAndSco;
	}
	
	public void refresh() {
		NodeNeighbors nNei = null;
		while(!disList.isEmpty()) {
			nNei = disList.getFirst();
			if(nNei.isCenterPClassified() || nNei.noAllNeighborInitState())	disList.pollFirst();
			else {
				break;
			}
		}
		
		while(!scoList.isEmpty()) {
			nNei = scoList.getFirst();
			if(nNei.isCenterPClassified() || nNei.noAllNeighborInitState())	scoList.pollFirst();
			else {
				break;
			}
		}
	}
	
	public void clear() {
		this.disList.clear();
		this.scoList.clear();
	}
	
	public void addDisNoise(NodeNeighbors nn) {
		this.disList.add(nn);
	}
	
	public void addScoNoise(NodeNeighbors nn) {
		this.scoList.add(nn);
	}
}
