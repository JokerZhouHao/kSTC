package utility;

import java.util.Random;

/**
 * 
 * @author Monica
 * @since 2018/3/8
 * 功能 : 参数随机数
 */
public class RandomNumGenerator {
	private int startNum = 0;
	private int span = Integer.MAX_VALUE;
	private static Random random = new Random();
	
	public RandomNumGenerator(int startNum, int endNum) {
		this.startNum = startNum;
		this.span = endNum - startNum;
	}
	
	public int getRandomInt() {
		return startNum + (int)(random.nextFloat() * span);
	}
	
	public static int getRandomInt(int start, int end) {
		return start + (int)(new Random().nextFloat() * (end - start + 1));
	}
	
	public static float getRandomFloat() {
		return random.nextFloat();
	}
	
	public static double getRandomDouble() {
		return random.nextDouble();
	}
	
	public static double getRandomCoordDouble(double max) {
//		return getRandomDouble() * max;
		if(getRandomInt(0, 1) == 0) {
			return max - getRandomDouble() * max / 10;
		} else	return max + getRandomDouble() * (1 - max) / 10;
	}
	
	public static int getRandomInt(int len) {
		return (int)(random.nextFloat() * (len-1)) + 1;
	}
	
	public static int getRInt(int bound) {
		return random.nextInt(bound);
	}
	
	public static void main(String[] args) {
		
//		for(int i=0; i<5; i++) {
//			System.out.println(RandomNumGenerator.getRandomInt(1, 1));
//		}
		
//		for(int i=0; i<5; i++) {
//			System.out.println(RandomNumGenerator.getRandomDouble(0.5));
//		}
		
//		for(int i=0; i<10; i++) {
//			System.out.println(RandomNumGenerator.getRInt(6));
//		}
		
		
//		RandomNumGenerator rand = new RandomNumGenerator(0, 10);
//		for(int i=0; i<10; i++) {
//			System.out.println(RandomNumGenerator.getRandomFloat());
////			System.out.println(rand.getRandomInt());
//		}
	}
}
