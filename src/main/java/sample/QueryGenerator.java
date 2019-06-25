package sample;

import entity.QueryParams;

/**
 * 实验用的查询生成器
 * @author ZhouHao
 * @since 2019年6月24日
 */
public class QueryGenerator {
//	int rtreeFanout = 50;
//	double steepDegree = 0.1;
//	int zorderWidth = 1000;
//	int zorderHeight = 1000;
//	
//	int numSample = 200;
//	int type = 0;
//	
//	int k = 5;
//	int numWord = 0;
//	
//	int minpts = 5;
//	double epsilon = 0.001;
//	double xi = 0.001;
//	
//	int maxPidNeighborsBytes = 50000000;
	
	public static void one() {
		int rtreeFanout = 50;
		double steepDegree = 0.1;
		int zorderWidth = 1000;
		int zorderHeight = 1000;
		
		int numSample = 200;
		int type = 1;
		
		int k = 10000;
		int numWord = 2;
		
		int minpts = 5;
		double epsilon = 0.001;
		double xi = 0.001;
		
		int maxPidNeighborsBytes = 50000000;
		
		System.out.println(QueryParams.generateTestQuery(rtreeFanout, steepDegree, 
						zorderWidth, zorderHeight, numSample, type, k, numWord, minpts, 
						epsilon, xi, maxPidNeighborsBytes));
	}
	
	public static void main(String[] args) {
		QueryGenerator.one();
	}
}
