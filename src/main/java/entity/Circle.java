package entity;

import spatialindex.spatialindex.Point;
import utility.Global;

public class Circle {
	public double radius = 0.0;
	public double[] center = new double[2];
	private SGPLInfo sInfo = null;
	
	public Circle(double radius, double[] center, SGPLInfo sInfo) {
		this.radius = radius;
		this.center = center;
		this.sInfo = sInfo;
	}
	
	public Circle(double radius, Point center, SGPLInfo sInfo) {
		this.radius = radius;
		this.center = center.m_pCoords;
		this.sInfo = sInfo;
	}
	
	public double[] passXX(double y) {
		double k = Math.abs(center[1] - y);
		if(Global.compareDouble(radius, k) <= 0)	return null;
		double[] ds = new double[2];
		k = Math.sqrt(radius * radius - k * k);
		ds[0] = center[0] - k;
		ds[1] = center[0] + k;
		return ds;
	}
	
	public static void main(String[] args) {
//		double d1 = 1.12;
//		double d2 = 1.12;
//		System.out.println(d1%d2 == 0.0);
		double[] center = {0.0, 0.0};
		Circle cir = new Circle(1, center, null);
		System.out.println(cir.passXX(0.5)[0] + " " + cir.passXX(0.5)[1]);
		
	}
	
}
