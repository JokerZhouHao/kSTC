package services;

import utility.Global;

// 记录运行的相关情况
public class RunTimeRecordor {
	public static long startTime = System.nanoTime();
	public static long timeBase = 1000000;	// ms
	
	public long frontTime = 0;
	public void setFrontTime() {
		frontTime = System.nanoTime();
	}
	
	public long getTimeSpan() {
		return System.nanoTime() - frontTime;
	}
	
	/**************** 准备数据 *********************/
	public long numNid = 0;
	public long numCellid = 0;
	public long timeSearchTerms = 0;
	
	public long timeSortByDistance = 0;
	public long timeSortByScore = 0;
	
	/****************  base 参数 ******************/
	public int numGetCluster = 0;
	
	public int numRangeRtree = 0;
	public long timeRangeRtree = 0;
	public int numRangeZCurve = 0;
	public long timeRangeZCurve = 0;
	
	/**************** optic 参数 ******************/
	public int numExpandClusterOrder = 0;
	public long numMinByteOfTermPNgb = 0;
	public long numByteOfTermPNgb = 0;
	public long timeReadTermPNgb = 0;
	public long timeSearchTermPNgb = 0;
	public long numOpticFastRange = 0;
	public long timeOpticFastRange = 0;
	public long numOpticLuceneRange = 0;
	public long timeOpticLuceneRange = 0;
	public long timeOpticFunc = 0;
	public long timeExcuteQueryFunc = 0;
	
	/**************** 要排除的时间  ****************/
	public long excludeTimeOpticAdvToCellidNodes = 0;
	
	
	/**************** 总时间 *********************/
	public long timeTotalPrepareData = 0;	// timeSearchTerms + timeSortByDistance + timeSortByScore
	public long timeTotalGetCluster = 0;
	public long timeTotal = 0;
	public int numCluster = 0;
	
	public double topKScore = 0;
	
	public static String getHeader() {
		return  "id," +
				"numNid,numCellid," +
				"timeSearchTerms,timeSortByDistance,timeSortByScore," +
				"numGetCluster,numRangeRtree,timeRangeRtree,numRangeZCurve,timeRangeZCurve," +
				"numExpandClusterOrder,numMinByteOfTermPNgb,numByteOfTermPNgb,timeReadTermPNgb,timeSearchTermPNgb," + 
				"numOpticFastRange,timeOpticFastRange,numOpticLuceneRange,timeOpticLuceneRange,timeOpticFunc,timeExcuteQueryFunc," +
				"timeTotalPrepareData,timeTotalGetCluster,timeTotal,numCluster,topKScore," +
				"\n";
	}
	
	public String getTimeStr(int id) {
		return getTimeStr(id, 1000000);		// ms级
	}
	
	public String getTimeStr(int id, long base) {
		timeTotal -= excludeTimeOpticAdvToCellidNodes;
		timeOpticFunc -= excludeTimeOpticAdvToCellidNodes;
		
		return  String.valueOf(id) + "," + 
				String.valueOf(numNid) + "," + String.valueOf(numCellid) + "," +
				String.valueOf(timeSearchTerms/base) + "," + 
				String.valueOf(timeSortByDistance/base) + "," + 
				String.valueOf(timeSortByScore/base) + "," + 
				String.valueOf(numGetCluster) + "," + 
				String.valueOf(numRangeRtree) + "," + 
				String.valueOf(timeRangeRtree/base) + "," + 
				String.valueOf(numRangeZCurve) + "," + 
				String.valueOf(timeRangeZCurve/base) + "," + 
				String.valueOf(numExpandClusterOrder) + "," + 
				String.valueOf(numMinByteOfTermPNgb) + "," +
				String.valueOf(numByteOfTermPNgb) + "," + 
				String.valueOf(timeReadTermPNgb/base) + "," + 
				String.valueOf(timeSearchTermPNgb/base) + "," + 
				String.valueOf(numOpticFastRange) + "," + 
				String.valueOf(timeOpticFastRange/base) + "," + 
				String.valueOf(numOpticLuceneRange) + "," + 
				String.valueOf(timeOpticLuceneRange/base) + "," + 
				String.valueOf(timeOpticFunc/base) + "," + 
				String.valueOf(timeExcuteQueryFunc/base) + "," + 
				String.valueOf(timeTotalPrepareData/base) + "," + 
				String.valueOf(timeTotalGetCluster/base) + "," + 
				String.valueOf(timeTotal/base) + "," + 
				String.valueOf(numCluster) + "," + 
				String.valueOf(topKScore) + "," + 
				"\n";
	}
}
