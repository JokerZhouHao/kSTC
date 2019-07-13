package entity;

import utility.Global;

/**
 * 用于过滤重复坐标
 * @author ZhouHao
 * @since 2019年7月10日
 */
public class MCoord {
	public double lon = 0.0;
	public double lat = 0.0;
	
	public MCoord(double[] coord) {
		this.lon = coord[0];
		this.lat = coord[1];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		MCoord other = (MCoord) obj;
		if(Global.compareDouble(lon, other.lon) == 0 &&
		   Global.compareDouble(lat, other.lat) == 0)	return true;
		return false;
	}
	
}
