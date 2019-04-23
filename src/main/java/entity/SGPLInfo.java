package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import spatialindex.spatialindex.Point;
import utility.Global;

public class SGPLInfo implements Serializable{
	
	/**
	 * copy from https://github.com/ilkcan/ktmstc_query_processing/blob/master/src/topkclusterquery/models/SGPLInfo.java
	 */
	private static final long serialVersionUID = 6411549802970603504L;
	private double minLat;
	private double latStep;
	private double halfLatStep;
	private double minLng;
	private double lngStep;
	private int zOrder;
	
	private static SGPLInfo sgplInfo = null;
	
	public SGPLInfo(double minLng, double lngStep, double minLat, double latStep, int zOrder){
		this.minLat = minLat;
		this.minLng = minLng;
		
		this.latStep = latStep;
		this.halfLatStep = latStep/2;
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
	
	public int nearestBelowCellY(double yCoord) {
		if(yCoord<=0.0)	return 0;
//		return (int)((yCoord+Global.minPositiveDouble)/latStep);
		return (int)((yCoord)/latStep);
	}
	
	public int nearestAboveCellY(double yCoord) {
		if(yCoord >= Global.zorderHeight * latStep)	return Global.zorderHeight;
//		int y = (int)((yCoord+Global.minPositiveDouble)/latStep);
		int y = (int)((yCoord)/latStep);
		if(Global.isZero(y*latStep - yCoord)) return y;
		else return y+1;
	}
	
	public int nearestLeftCellX(double xCoord) {
		if(Global.compareDouble(xCoord, Global.minPositiveDouble) <= 0) return 0;
//		return (int)((xCoord+Global.minPositiveDouble)/lngStep);
		return (int)((xCoord)/lngStep);
	}
	
	public int nearestRightCellX(double xCoord) {
		if(Global.compareDouble(xCoord, Global.minPositiveDouble) <= 0) return 0;
		if(xCoord >= Global.zorderWidth * lngStep) { return Global.zorderWidth;}
//		int x = (int)((xCoord+Global.minPositiveDouble)/lngStep);
		int x = (int)((xCoord)/lngStep);
		if(Global.isZero(x*lngStep - xCoord))	return x;
		else return x+1;
	}
	
	public List<CellSign> cover(Circle circle){
		int maxY = nearestAboveCellY(circle.center[1] + circle.radius);
		int minY = nearestBelowCellY(circle.center[1] - circle.radius);
		int Y;
		double maxYCoord = maxY * latStep;
		double YCoord1, YCoord2;
		List<CellSign> zids = new ArrayList<>();
		int minX, maxX, X1, X2, leftX=0, rightX=0;
		double[] passXX = null;
		for(Y=minY, YCoord1=minY*latStep, YCoord2=YCoord1 + latStep; Global.compareDouble(YCoord2, maxYCoord) <= 0; 
			Y++, YCoord1=YCoord2, YCoord2 += latStep) {
			if(Global.compareDouble(circle.center[1] - YCoord1, halfLatStep) >= 0) {
				passXX = circle.passXX(YCoord1);
				if(null == passXX) leftX = Integer.MAX_VALUE;
				else {
					leftX = nearestRightCellX(passXX[0]);
					rightX = nearestLeftCellX(passXX[1]);
				}
				passXX = circle.passXX(YCoord2);
				if(null==passXX) {
					minX =  nearestLeftCellX(circle.center[0] - circle.radius);
					maxX = nearestRightCellX(circle.center[0] + circle.radius);
				} else {
					minX =  nearestLeftCellX(passXX[0]);
					maxX = nearestRightCellX(passXX[1]);
				}
			} else {
				passXX = circle.passXX(YCoord2);
				if(null == passXX) leftX = Integer.MAX_VALUE;
				else {
					leftX = nearestRightCellX(passXX[0]);
					rightX = nearestLeftCellX(passXX[1]);
				}
				passXX = circle.passXX(YCoord1);
				if(null==passXX) {
					minX =  nearestLeftCellX(circle.center[0] - circle.radius);
					maxX = nearestRightCellX(circle.center[0] + circle.radius);
				} else {
					minX =  nearestLeftCellX(passXX[0]);
					maxX = nearestRightCellX(passXX[1]);
				}
			}
			
			for(X1 = minX, X2 = X1+1; X2 <= maxX; X1 = X2, X2++) {
				if(X1 >= leftX && X2 < rightX) {
					zids.add(new CellSign(getZOrderId(X1, Y), Boolean.TRUE));
				} else zids.add(new CellSign(getZOrderId(X1, Y), Boolean.FALSE));
//				zids.add(new CellSign(getZOrderId(X1, Y), Boolean.FALSE));
			}
		}
		if(zids.isEmpty())	return null;
		else return zids;
	}
	
	/**
	 * 完全计算
	 * @param circle
	 * @return
	 */
	public List<CellSign> coverTest(Circle circle){
		int maxY = nearestAboveCellY(circle.center[1] + circle.radius);
		int minY = nearestBelowCellY(circle.center[1] - circle.radius);
		int minX, maxX;
		
		minX =  nearestLeftCellX(circle.center[0] - circle.radius);
		maxX = nearestRightCellX(circle.center[0] + circle.radius);
		
		List<CellSign> zids = new ArrayList<>();
		
		for(int x=minX; x < maxX; x++) {
			for(int y=minY; y < maxY; y++) {
				zids.add(new CellSign(getZOrderId(x, y), Boolean.FALSE));
			}
		}
		return zids;
	}
	
	
	@Override
	public String toString() {
		return "SGPLInfo [minLat=" + minLat + ", latStep=" + latStep + ", minLng=" + minLng + ", lngStep=" + lngStep
				+ ", zOrder=" + zOrder + "]";
	}

	public static void main(String[] args) {
		SGPLInfo info = Global.sgplInfo;
//		SGPLInfo info = new SGPLInfo(0, 0.125, 0, 0.125, 64);
//		System.out.println(info.getZOrderId(0, 0));
//		System.out.println(info.getZOrderId(1, 1));
//		System.out.println(info.getZOrderId(1, 0));
//		System.out.println(info.getZOrderId(4, 3));
//		System.out.println(info.getZOrderId(6, 6));
		System.out.println(info.getZOrderId(0.8, 0.9));
		
//		System.out.println(info.getLngStep());
//		System.out.println(info.getLatStep());
//		System.out.println();
//		
//		System.out.println(info.nearestAboveCellY(0.35));
//		System.out.println(info.nearestBelowCellY(0.35));
//		System.out.println();
//		
//		System.out.println(info.nearestLeftCellX(0.75));
//		System.out.println(info.nearestRightCellX(0.75));
		
		double[] center = {0.35, 0.47};
		double radius = 0.15;
		List<CellSign> zids = sgplInfo.cover(new Circle(radius, center));
		for(CellSign cs : zids) {
			System.out.println(cs);
		}
		
	}
}
