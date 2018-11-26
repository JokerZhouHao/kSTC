package utility;

import spatialindex.spatialindex.Point;

public class ShapeUtility {
	private static double[][] allXY = {
			{0.1, 0.1}, // leftBottom
			{0.1, 0.1}, // rightBottom
			{0.1, 0.1}, // leftTop
			{0.1, 0.1}  // rightTop
			};
	private static int i = 0;
	private static double[] center = null;
	
	public static Boolean isRectInCircle(double leftBottomX, double leftBottomY, double rightTopX, double rightTopY, 
										Point centerPoint, double radius) {
		allXY[0][0] = leftBottomX;	allXY[0][1] = leftBottomY;
		allXY[1][0] = rightTopX;	allXY[1][1] = leftBottomY;
		allXY[2][0] = leftBottomX;	allXY[2][1] = rightTopY;
		allXY[3][0] = rightTopX;	allXY[3][1] = rightTopY;
		
		center = centerPoint.getCenter();
		
		for(i=0; i<4; i++) {
//			if(Math.sqrt(Math.pow(allXY[i][0] - cen)))
		}
		return Boolean.TRUE;
	}
}
