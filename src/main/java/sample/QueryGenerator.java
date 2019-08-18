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
		int rtreeFanout = 800;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 18;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.0001;
		
		int numSample = 2;
//		int type = 11'
		List<Integer> types = new ArrayList<>();
		types.add(1);
		types.add(2);
		types.add(3);
		types.add(4);
		
		types.add(11);
//		types.add(12);
		
		int k = 5000;
		int numWord = 1;
		
		int minpts = 5;
		double epsilon = 0.00003;
		double xi = 0.00003;
		
		int maxPidNeighborsBytes = 2147483631;
		
		for(int type : types) {
			System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
					opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
					epsilon, xi, maxPidNeighborsBytes));
		}
	}
	
	public static void dbscanNumWord() {
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 14;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.001;
		
		int numSample = 100;
		List<Integer> types = new ArrayList<>();
		types.add(1);
		types.add(2);
		types.add(3);
		types.add(4);
		
		int k = 10;
		List<Integer> numWords = new ArrayList<>();
		numWords.add(1);
		numWords.add(2);
		numWords.add(3);
		numWords.add(4);
		
		int minpts = 50;
		
		double epsilon = 0.001;
		
		double xi = 0.001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		for(int type : types) {
			for(int numWord : numWords) {
				System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
						opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
						epsilon, xi, maxPidNeighborsBytes));
			}
			System.out.println();
		}
	}
	
	public static void dbscanEpsilon() {
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 14;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.001;
		
		int numSample = 100;
//		int type = 11'
		List<Integer> types = new ArrayList<>();
		types.add(1);
		types.add(2);
		types.add(3);
		types.add(4);
		
		int k = 10;
		int numWord = 2;
		
		int minpts = 50;
		
		List<Double> epsilons = new ArrayList<>();
		epsilons.add(0.0001);
		epsilons.add(0.0005);
		epsilons.add(0.001);
		epsilons.add(0.005);
		epsilons.add(0.01);
		
		double xi = 0.001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		for(int type : types) {
			for(double epsilon : epsilons) {
				System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
						opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
						epsilon, xi, maxPidNeighborsBytes));
			}
			System.out.println();
		}
	}
	
	public static void dbscanMpt() {
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 14;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.001;
		
		int numSample = 100;
//		int type = 11'
		List<Integer> types = new ArrayList<>();
		types.add(1);
		types.add(2);
		types.add(3);
		types.add(4);
		
		int k = 10;
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
	
	public static void dbscanH() {
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		
		List<Integer> hs = new ArrayList<>();
		hs.add(4);
		hs.add(6);
		hs.add(8);
		hs.add(10);
		hs.add(12);
		hs.add(14);
		hs.add(16);
		
		int opticMinpts = 1;
		double opticEpsilon = 0.001;
		
		int numSample = 100;
//		int type = 11'
		List<Integer> types = new ArrayList<>();
		types.add(1);
		types.add(2);
		types.add(3);
		types.add(4);
		
		int k = 10;
		int numWord = 2;
		
		int minpts = 50;
		
		
		double epsilon = 0.001;
		double xi = 0.001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		for(int type : types) {
			for(int h : hs) {
				System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
						opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
						epsilon, xi, maxPidNeighborsBytes));
			}
			System.out.println();
		}
	}
	
	public static void opticNumWord() {
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 10;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.0001;
		
		int numSample = 200;
		List<Integer> types = new ArrayList<>();
//		types.add(11);
		types.add(4);
		
		int k = 100000;
		List<Integer> numWords = new ArrayList<>();
		numWords.add(1);
		numWords.add(2);
		numWords.add(3);
		numWords.add(4);
		
		int minpts = 5;
		
		double epsilon = 0.0001;
		
		double xi = 0.0001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		for(int type : types) {
			for(int numWord : numWords) {
				System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
						opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
						epsilon, xi, maxPidNeighborsBytes));
			}
			System.out.println();
		}
	}
	
	
	public static void dbcvYelpbuss() {
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 10;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.0001;
		
		int numSample = 200;
		List<Integer> types = new ArrayList<>();
//		types.add(11);
		types.add(4);
		
		int k = 100000;
		List<Integer> numWords = new ArrayList<>();
		numWords.add(1);
		numWords.add(2);
		numWords.add(3);
		numWords.add(4);
		
		int minpts = 5;
		
		double epsilon = 0.0001;
		
		double xi = 0.0001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		for(int type : types) {
			for(int numWord : numWords) {
				System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
						opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
						epsilon, xi, maxPidNeighborsBytes));
			}
			System.out.println();
		}
	}
	
	public static void dbcvArizona() {
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 10;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.001;
		
		int numSample = 400;
		List<Integer> types = new ArrayList<>();
//		types.add(11);
		types.add(4);
		
		int k = 5000;
		List<Integer> numWords = new ArrayList<>();
		numWords.add(1);
		numWords.add(2);
		numWords.add(3);
		numWords.add(4);
		
		int minpts = 5;
		
		double epsilon = 0.001;
		
		double xi = 0.001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		for(int type : types) {
			for(int numWord : numWords) {
				System.out.println(QueryParams.generateTestQuery(rtreeFanout, alpha, steepDegree, h, 
						opticMinpts, opticEpsilon, numSample, type, k, numWord, minpts, 
						epsilon, xi, maxPidNeighborsBytes));
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
//		one();
//		oneGroup();
		
		/*********************   dbscan  *********************/
//		dbscanNumWord();
//		dbscanEpsilon();
//		dbscanMpt();
//		dbscanH();
		
		/*********************   optic  *********************/
//		opticNumWord();
		
		/*********************   dbcv  *********************/
//		dbcvYelpbuss();
		dbcvArizona();
	}
}
