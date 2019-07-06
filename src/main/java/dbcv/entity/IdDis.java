package dbcv.entity;

public class IdDis implements Comparable{
	public int id = -1;
	public double dis = 0.0;
	
	public IdDis(int id) {
		super();
		this.id = id;
	}

	public IdDis(int id, double dis) {
		this.id = id;
		this.dis = dis;
	}

	@Override
	public int compareTo(Object o) {
		IdDis idDis1 = (IdDis)o;
		if(dis > idDis1.dis)	return 1;
		else if(dis == idDis1.dis)	return 0;
		else return -1;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", dis=" + dis + "]";
	}
}
