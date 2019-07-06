package precomputation.dataset.file;

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
	
	public static void main(String[] args) throws Exception {
		String fp = Global.pathOrgId2Coord;
		CoordInfoExtractor.showLBAndRU(fp);
	}
}
