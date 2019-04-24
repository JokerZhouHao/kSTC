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
	public long timeSearchTerms = 0;
	
	public long timeSortByDistance = 0;
	public long timeSortByScore = 0;
	
	/****************  base 参数 ******************/
	public int numGetCluster = 0;
	
	public long timeRangeRtree = 0;
	public long timeRangeZCurve = 0;
	
	/**************** optic 参数 ******************/
	public long timeOpticFunc = 0;
	public long timeSearchTermPNgb = 0;
	public long timeOpticRange = 0;
	public long timeExcuteQueryFunc = 0;
	
	/**************** 总时间 *********************/
	public long timeTotalPrepareData = 0;
	public long timeTotalGetCluster = 0;
	public long timeTotal = 0;
	
	
	public static String getHeader() {
		return "id,timeSearchTerms,timeSortByDistance,timeSortByScore," +
				"numGetCluster,timeRangeRtree,timeRangeZCurve," +
				"timeOpticFunc,timeSearchTermPNgb,timeOpticRange,timeExcuteQueryFunc," +
				"timeTotalPrepareData,timeTotalGetCluster,timeTotal," +
				"\n";
	}
	
	public String getTimeStr(int id) {
		return getTimeStr(id, 1000000);		// ms级
	}
	
	public String getTimeStr(int id, long base) {
		return  String.valueOf(id) + "," + String.valueOf(timeSearchTerms/base) + "," + 
				String.valueOf(timeSortByDistance/base) + "," + 
				String.valueOf(timeSortByScore/base) + "," + 
				String.valueOf(numGetCluster) + "," + 
				String.valueOf(timeRangeRtree/base) + "," + 
				String.valueOf(timeRangeZCurve/base) + "," + 
				String.valueOf(timeOpticFunc/base) + "," + 
				String.valueOf(timeSearchTermPNgb/base) + "," + 
				String.valueOf(timeOpticRange/base) + "," + 
				String.valueOf(timeExcuteQueryFunc/base) + "," + 
				String.valueOf(timeTotalPrepareData/base) + "," + 
				String.valueOf(timeTotalGetCluster/base) + "," + 
				String.valueOf(timeTotal/base) + "," + 
				"\n";
	}
}
