package entity;

import utility.Global;

public class Term2Fre implements Comparable{
	public int frequency = 0;
	public String term = null;
	public Term2Fre(int frequency, String term) {
		super();
		this.frequency = frequency;
		this.term = term;
	}
	
	@Override
	public int compareTo(Object o) {
		Term2Fre f = (Term2Fre)o;
		if(frequency > f.frequency)	return 1;
		else if(frequency == f.frequency)	return 0;
		return -1;
	}
	
	public String toString() {
		return term + Global.delimiterLevel1 + frequency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Term2Fre other = (Term2Fre) obj;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}
	
}
