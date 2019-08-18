package algorithm.test;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import entity.QueryParams;
import utility.Global;
import utility.MLog;
import utility.io.TimeUtility;

/**
 * 多线程跑实验
 * @author ZhouHao
 * @since 2019年6月24日
 */
public class MulAlgTest {
	private static int numQuery = 0;
	
	public static synchronized void decrease() {
		numQuery--;
	}
	
	public static synchronized Boolean over() {
		if(0 == numQuery)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public static int currentNumQuery() {
		return numQuery;
	}
	
	public static void test(String[] args) throws Exception {
		if(args.length < 2) {
			MLog.log("参数不符合格式: NumThread  FileName");
			System.exit(0);
		}
		long startTime = System.currentTimeMillis();
		Global.displayInputOutputPath();
		
		int numThread = Integer.parseInt(args[0]);
		String queryPath = Global.sampleResultPath + args[1];
		List<QueryParams> qps = QueryParams.load(queryPath);
		numQuery = qps.size();
		numThread = numThread > qps.size() ? qps.size() : numThread;
		
		MLog.log("开始测试，线程数: " + numThread + "      查询数: " + numQuery);
		QueryParams.displays(qps);
		Thread.sleep(10000);
		
		
		ArrayBlockingQueue<QueryParams> queue = new ArrayBlockingQueue<>(numThread);
		for(int i=0; i<numThread; i++) {
			new Thread(new AlgExcutor(queue)).start();
		}
		
		for(QueryParams qp : qps)
			queue.put(qp);
		
		for(int i=0; i<numThread; i++) {
			queue.put(Global.signQueryThreadOver);
		}
		
		while(!over())	Thread.sleep(10000);
		
		MLog.log("完成测试, 用时: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	public static void main(String[] args) throws Exception {
		test(args);
	}
}
