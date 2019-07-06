package entity;

public class Pair<T1, T2> {
	public T1 key;
	public T2 value;
	
	public Pair(T1 k, T2 v) {
		this.key = k;
		this.value = v;
	}
}
