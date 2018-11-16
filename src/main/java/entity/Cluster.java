package entity;

public class Cluster extends PNodeCollection{
	private int id = -1;
	
	public Cluster(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
}
