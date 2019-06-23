package entity;

import spatialindex.spatialindex.Point;

/**
 * 
 * @author ZhouHao
 * @since 2018年11月7日
 */
public class Rectangle {
	
	public double westSouthLont = 0;	// west south lontitude
	public double westSouthLat = 0; 	// west south latitude
	public double eastNorthLont = 0;	// east north longitude
	public double eastNorthLat = 0;	// east north latitude
	
	public Rectangle(Point westSouthLocation, Point eastNorthLocation) {
		this(westSouthLocation.m_pCoords[0], westSouthLocation.m_pCoords[1], eastNorthLocation.m_pCoords[0], eastNorthLocation.m_pCoords[1]);
	}
	
	public Rectangle(double westSouthLont, double westSouthLat, double eastNorthLont, double eastNorthLat) {
		super();
		this.westSouthLont = westSouthLont;
		this.westSouthLat = westSouthLat;
		this.eastNorthLont = eastNorthLont;
		this.eastNorthLat = eastNorthLat;
	}
	
	public Boolean contain(double longitude, double latitude) {
		if(westSouthLat<=latitude && latitude<=eastNorthLat) {
			if(westSouthLont<=eastNorthLont) {
				if(westSouthLont<=longitude && longitude<=eastNorthLont)	return Boolean.TRUE;
			} else {
				if((westSouthLont<=longitude && longitude<=180) ||
				   (-180<=longitude && longitude<=eastNorthLont))	return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	public String toString() {
		return "[" + String.valueOf(westSouthLont) + "," + String.valueOf(westSouthLat) + "]," +
				"[" + String.valueOf(eastNorthLont) + "," + String.valueOf(eastNorthLat) + "]";
	}
	
	public static void main(String[] args) throws Exception{
		Rectangle rec = new Rectangle(30, 10, 90, 50);
		System.out.println(rec);
	}
}
