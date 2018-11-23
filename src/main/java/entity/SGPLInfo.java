package entity;

import java.io.Serializable;

import utility.Global;

public class SGPLInfo implements Serializable{
	
	/**
	 * copy from https://github.com/ilkcan/ktmstc_query_processing/blob/master/src/topkclusterquery/models/SGPLInfo.java
	 */
	private static final long serialVersionUID = 6411549802970603504L;
	private double minLat;
	private double latStep;
	private double minLng;
	private double lngStep;
	private int zOrder;
	
	private static SGPLInfo sgplInfo = null;
	
	public SGPLInfo(double minLng, double lngStep, double minLat, double latStep, int zOrder){
		this.minLat = minLat;
		this.minLng = minLng;
		this.latStep = latStep;
		this.lngStep = lngStep;
		this.zOrder = zOrder;
	}

	public static SGPLInfo getGlobalInstance() {
		if(sgplInfo == null) {
			double i = 1.0;
			sgplInfo = new SGPLInfo(0, i/Global.zorderWidth, 0, i/Global.zorderHeight, Global.zorderWidth * Global.zorderHeight);
		}
		return sgplInfo;
	}
	
	public double getMinLat() {
		return minLat;
	}

	public double getLatStep() {
		return latStep;
	}

	public double getMinLng() {
		return minLng;
	}

	public double getLngStep() {
		return lngStep;
	}
	
	public int getZOrder() {
		return zOrder;
	}
	
	public int getZOrderId(int x, int y) {
		int z = 0;
	
		for (int i = 0; i < Integer.SIZE; i++) {
			int x_masked_i = (x & (1 << i));
			int y_masked_i = (y & (1 << i));
			z |= (x_masked_i << i);
			z |= (y_masked_i << (i + 1));
		}
		return z;
	}
	
	public int getZOrderId(double lon, double lat) {
		return this.getZOrderId((int)(lon/lngStep), (int)(lat/latStep));
	}
	
	public int getZOrderId(double[] lonLat) {
		return getZOrderId(lonLat[0], lonLat[1]);
	}
	
	@Override
	public String toString() {
		return "SGPLInfo [minLat=" + minLat + ", latStep=" + latStep + ", minLng=" + minLng + ", lngStep=" + lngStep
				+ ", zOrder=" + zOrder + "]";
	}

	public static void main(String[] args) {
		SGPLInfo info = new SGPLInfo(0, 0.125, 0, 0.125, 64);
		System.out.println(info.getZOrderId(0, 0));
		System.out.println(info.getZOrderId(1, 1));
		System.out.println(info.getZOrderId(1, 3));
		System.out.println(info.getZOrderId(3, 1));
		System.out.println(info.getZOrderId(4, 3));
		System.out.println(info.getZOrderId(6, 6));
		System.out.println(info.getZOrderId(0.126, 0.126));
	}
}
