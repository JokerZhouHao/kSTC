package sample;

import java.util.ArrayList;
import java.util.List;

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
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 6;
		
		int opticMinpts = 10;
		double opticEpsilon = 0.001;
		
		int numSample = 200;
		int type = 11;
		
		int k = 5000;
		int numWord = 2;
		
		int minpts = 5;
		double epsilon = 0.0001;
		double xi = 0.0001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
						opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
						epsilon, xi, maxPidNeighborsBytes));
	}
	
	public static void oneGroup() {
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 15;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.0001;
		
		int numSample = 50;
//		int type = 11'
		List<Integer> types = new ArrayList<>();
		types.add(1);
		types.add(2);
		types.add(3);
//		types.add(12);
		
		int k = 5000;
		int numWord = 2;
		
		int minpts = 5;
		double epsilon = 0.001;
		double xi = 0.001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		for(int type : types) {
			System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
					opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
					epsilon, xi, maxPidNeighborsBytes));
		}
	}
	
	
	public static void dbscanMpt() {
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 15;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.0001;
		
		int numSample = 50;
//		int type = 11'
		List<Integer> types = new ArrayList<>();
		types.add(1);
		types.add(2);
		types.add(3);
		types.add(4);
		
		int k = 5000;
		int numWord = 2;
		
		List<Integer> mpts = new ArrayList<>();
		mpts.add(10);
		mpts.add(20);
		mpts.add(50);
		mpts.add(100);
		mpts.add(200);
		
		
		double epsilon = 0.001;
		double xi = 0.001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		for(int type : types) {
			for(int minpts : mpts) {
				System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
						opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
						epsilon, xi, maxPidNeighborsBytes));
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
//		QueryGenerator.one();
//		QueryGenerator.oneGroup();
		dbscanMpt();
	}
}
