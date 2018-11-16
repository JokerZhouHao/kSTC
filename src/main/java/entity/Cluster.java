package entity;

import java.util.Collection;
import java.util.List;

import utility.Global;

public class Cluster extends PNodeCollection{
	private int id = -1;
	private double score = Double.MAX_VALUE;
	
	public Cluster(int id) {
		this.id = id;
	}
	
	public Cluster(int id, double score) {
		this.id = id;
		this.score = score;
	}
	
	public Cluster(int id, Collection<Node> nodes) {
		super(nodes);
		this.id = id;
		this.calScore();
	}
	
	public void calScore() {
		double[] disScore = this.getMinDisAndScore();
		this.score = Global.alpha * disScore[0] + (1-Global.alpha) * disScore[1];
	}
	
	public int getId() {
		return id;
	}

	public double getScore() {
		return score;
	}

	@Override
	public String toString() {
		return "Cluster [id=" + id + ", score=" + score + "]";
	}
}
