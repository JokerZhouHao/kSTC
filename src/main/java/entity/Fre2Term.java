package entity;

import utility.Global;

public class Fre2Term implements Comparable{
	public int frequency = 0;
	public String term = null;
	public Fre2Term(int frequency, String term) {
		super();
		this.frequency = frequency;
		this.term = term;
	}
	
	@Override
	public int compareTo(Object o) {
		Fre2Term f = (Fre2Term)o;
		if(frequency > f.frequency)	return 1;
		else if(frequency == f.frequency)	return 0;
		return -1;
	}
	
	public String toString() {
		return term + Global.delimiterLevel1 + frequency;
	}
	
}
