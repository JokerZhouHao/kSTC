package entity.optics;

public class SteepArea{
	public final static int TYPEUPAREA = 0;
	public final static int TYPEDOWNAREA = 1;
	public final static int TYPENORMAL = 2;
	public int type = TYPENORMAL;
	public int start = -1;
	public int end = -1;
	public double mib = Double.MIN_VALUE;
	public final static int STATUSNOUSED = 0;
	public final static int STATUSHASUSED = 1;
	public final static int STATUSSTOP = 2;
	public int status = STATUSNOUSED;
	public int lastEnd = 0;
	public double lastMib = 0;
	
	public SteepArea() {}

	public SteepArea(int type, int start, int end, double mib, int status, int lastEnd, double lastMib) {
		super();
		this.type = type;
		this.start = start;
		this.end = end;
		this.mib = mib;
		this.status = status;
		this.lastEnd = lastEnd;
		this.lastMib = lastMib;
	}

	public SteepArea set(int type, int start, int end, double mib, int status, int lastEnd, double lastMib) {
		this.type = type;
		this.start = start;
		this.end = end;
		this.mib = mib;
		this.status = status;
		this.lastEnd = lastEnd;
		this.lastMib = lastMib;
		return this;
	}
	
	public Boolean isUpArea() {
		if(type==TYPEUPAREA)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public Boolean isDownArea() {
		if(type==TYPEDOWNAREA)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public Boolean isNormalArea() {
		if(type==TYPENORMAL)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public Boolean hasUsed() {
		if(status == STATUSHASUSED)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public Boolean noUsed() {
		if(status == STATUSNOUSED)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public Boolean isStop() {
		if(status == STATUSSTOP)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public SteepArea copy() {
		return new SteepArea(this.type, this.start, this.end, this.mib,this.status, this.lastEnd, this.lastMib);
	}
	
	@Override
	public String toString() {
		return "SteepArea [type=" + type + ", start=" + start + ", end=" + end + ", mib=" + mib + ", status=" + status
				+ ", lastEnd=" + lastEnd + ", lastMib=" + lastMib + "]";
	}

	public static void main(String[] args) throws Exception{
		SteepArea sa = new SteepArea(SteepArea.TYPEDOWNAREA, 0, 0, 0.1, STATUSNOUSED, 1, 2);
		SteepArea sa1 = sa.copy();
		System.out.println(sa == sa1);
		System.out.println(sa);
		System.out.println(sa1);
	}
}
