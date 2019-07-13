package entity;

public class Id2Distance {
	public int id = -1;
	public double distance = -1;
	public Id2Distance(int id, double distance) {
		super();
		this.id = id;
		this.distance = distance;
	}
	@Override
	public String toString() {
		return "[id=" + id + ", distance=" + distance + "]\n";
	}
}
