package utility;

/**
 * 地理距离计算工具
 * @author ZhouHao
 * @since 2019年6月25日
 */
public class GeoDisUtilityPlacedumpArizona {
	private static final int GEODIS_2_1LNG = 111000;
	private static final double TOTAL_LNG = 6;
	private static final double TOTAL_GEODIS = GEODIS_2_1LNG * TOTAL_LNG;
	private static final double ANGLE = 34 / 180.0 * Math.PI;
	
	private static final String signGeoDis = "GeoDis";
	private static final String signDis = "Dis";
	
	private static final String formator = "%-30s%-30s\n%-30s%-30s";
	
	public static void display(double geoDis, double dis) {
		System.out.println(String.format(formator, signGeoDis, signDis, geoDis + " m", dis));
	}
	
	public static void dis2GeoDis(double dis) {
		double geo = TOTAL_GEODIS * dis * Math.cos(ANGLE);
		display(geo, dis);
	}
	
	public static void geoDis2dis(double geoDis) {
		double dis = geoDis / TOTAL_GEODIS / Math.cos(ANGLE);
		display(geoDis, dis);
	}
	
	
	public static void main(String[] args) {
		dis2GeoDis(0.001);
//		dis2GeoDis(0.001);
//		dis2GeoDis(0.00005);
		
//		geoDis2dis(1000);
		
	}
}
