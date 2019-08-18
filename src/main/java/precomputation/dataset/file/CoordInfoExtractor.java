package precomputation.dataset.file;

import entity.Rectangle;
import utility.Global;

/**
 * 用于提取坐标文件的相关信息
 * @author ZhouHao
 * @since 2019年7月5日
 */
public class CoordInfoExtractor {
	/**
	 * 展示整个数据集的左下角(LB)和右上角(RU)坐标(以经纬度为准)
	 */
	public static void showLBAndRU(String fp) throws Exception{
		double[][] allCoords = FileLoader.loadCoords(fp);
		double[] lb = {Double.MAX_VALUE, Double.MAX_VALUE}, ru = {Double.MIN_VALUE, Double.MIN_VALUE};
		for(double[] coord : allCoords) {
			lb[0] = lb[0] <= coord[0] ? lb[0] : coord[0];
			lb[1] = lb[1] <= coord[1] ? lb[1] : coord[1];
			ru[0] = ru[0] >= coord[0] ? ru[0] : coord[0];
			ru[1] = ru[1] >= coord[1] ? ru[1] : coord[1];
		}
		System.out.println("LeftBottom: " + lb[0] + ", " + lb[1]);
		System.out.println("RightUp: " + ru[0] + ", " + ru[1]);
	}
	
	
	public static void showNumObjectInRect(String fp, Rectangle rect) throws Exception{
		double[][] allCoords = FileLoader.loadCoords(fp);
		int num = 0;
		for(int i=0; i<allCoords.length; i++) {
			if(rect.contain(allCoords[i][0], allCoords[i][1]))	num++;
		}
		System.out.println("NumObjectIn " + rect + ": " + num);
	}
	
	public static void showOrginalCoord(Rectangle rect, double[][] coords) {
		double spanLon = rect.eastNorthLont - rect.westSouthLont;
		double spanLat = rect.eastNorthLat - rect.westSouthLat;
		for(double[] cod : coords) {
			System.out.println("normal coord: " + cod[0] + " " + cod[1]);
			cod[0] = rect.westSouthLont + cod[0] * spanLon;
			cod[1] = rect.westSouthLat + cod[1] * spanLat;
			System.out.println("orginal coord: " + cod[0] + " " + cod[1]);
			System.out.println("orginal coord: " + cod[1] + "," + cod[0] + '\n');
		}
	}
	
	
	public static void main(String[] args) throws Exception {
//		System.out.println(new Rectangle(-125, 31.2, -109, 42.2));
		
//		String fp = Global.pathOrgId2Coord;
//		CoordInfoExtractor.showLBAndRU(fp);
		
		
//		String fp = Global.pathOrgId2Coord;
//		showNumObjectInRect(fp, new Rectangle(-130, 30, -65, 45));	// 美国
//		showNumObjectInRect(fp, new Rectangle(-114, 31, -108, 37));	// 亚利桑那州
//		showNumObjectInRect(fp, new Rectangle(-125, 31.2, -109, 42.2));	// 加利福利亚、内华达、犹他州、亚利桑那州
		
		
		
//		xlim = [0.31042, 0.31086]
//		ylim = [0.4200, 0.4210]
//		Rectangle rect = new Rectangle(-125, 28, 15, 60);
//		double[][] coords = {{0.31042, 0.4200},
//				{0.31086, 0.4210}
//		};
//		showOrginalCoord(rect, coords);
		
//		xlim = [0.525914, 0.530711]
//		ylim = [0.1980, 0.2016]
		Rectangle rect = new Rectangle(-114, 31, -108, 37);
		double[][] coords = {{0.525914, 0.1980},
				{0.530711, 0.2016}
		};
		showOrginalCoord(rect, coords);
		
	}
}

















