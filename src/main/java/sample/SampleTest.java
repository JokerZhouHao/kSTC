package sample;

import java.io.BufferedWriter;
import java.util.List;

import algorithm.AlgEucDisAdvancedOptics;
import algorithm.AlgEucDisAdvancedOpticsWu;
import algorithm.AlgEucDisBase;
import algorithm.AlgEucDisBaseOptics;
import algorithm.AlgEucDisBaseOpticsWu;
import algorithm.AlgEucDisFastRange;
import algorithm.AlgInterface;
import algorithm.AlgTest;
import entity.AlgType;
import entity.QueryParams;
import entity.SortedClusters;
import services.RunTimeRecordor;
import utility.Global;
import utility.TimeUtility;
import utility.io.IOUtility;

public class SampleTest {
	private AlgEucDisBase eucBase = new AlgEucDisBase();
	private AlgEucDisFastRange eucFast = new AlgEucDisFastRange();
	
	private AlgEucDisBaseOptics eucBaseOptics = new AlgEucDisBaseOptics();
	private AlgEucDisBaseOpticsWu eucBaseOpticsWu = new AlgEucDisBaseOpticsWu();
	
	private AlgEucDisAdvancedOptics eucAdvancedOptics = new AlgEucDisAdvancedOptics();
	private AlgEucDisAdvancedOpticsWu eucAdvancedOpticsWu = new AlgEucDisAdvancedOpticsWu();
	
	private List<QueryParams> qps = null;
	
	public SampleTest() throws Exception {
	}
	
	/**
	 * 测试
	 * @param qps
	 * @param algType
	 * @param pathRecordRunTime
	 * @throws Exception
	 */
	public void test(List<QueryParams> qps, AlgType algType, String pathRecordRunTime) throws Exception{
		test(qps, algType, pathRecordRunTime, null);
	}
	
	/**
	 * 测试
	 * @param qps
	 * @param algType
	 * @param pathRecordRunTime
	 * @param pathResultCluster
	 * @throws Exception
	 */
	public void test(List<QueryParams> qps, AlgType algType, String pathRecordRunTime, String pathResultCluster) throws Exception{
		System.out.println("> 开始用" + algType + "测试" + String.valueOf(qps.size()) + "个样本 . . . . " + TimeUtility.getTime());
		long startTime = System.currentTimeMillis();
		this.qps = qps;
		AlgInterface alg = null;
		switch (algType) {
			case AlgEucDisBase:
				alg = eucBase;
				break;
			case AlgEucDisFastRange:
				alg = eucFast;
				break;
			case AlgEucDisBaseOpticsWu:
				alg = eucBaseOpticsWu;
				break;
			case AlgEucDisAdvancedOpticsWu:
				alg = eucAdvancedOpticsWu;
				break;
			default:
				break;
		}
		SortedClusters sCluster = null;
		BufferedWriter bw = IOUtility.getBW(pathRecordRunTime); 
		bw.write(RunTimeRecordor.getHeader());
		for(int i=0; i<qps.size(); i++) {
			Global.runTimeRec = new RunTimeRecordor();
			sCluster = alg.excuteQuery(qps.get(i));
			bw.write(Global.runTimeRec.getTimeStr(i+1));
			if(null != pathResultCluster) {
				IOUtility.writeSortedClusters(pathResultCluster, qps.get(i), sCluster);
			}
		}
		bw.close();
		System.out.println("> Over测试, 用时 : " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()) + "    " + TimeUtility.getTime());
	}
	
	/**
	 * 主方法
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		SampleTest samT = new SampleTest();
		
		String pathSample = Global.samplePath + "test.txt";
		String pathRecordRunTime = Global.sampleResultPath + "run_time.csv";
		String pathSampleResult = Global.sampleResultPath + "res.txt";
		List<QueryParams> qps = SampleLoader.load(pathSample);
		
		// AlgEucDisBase
//		pathRecordRunTime = Global.sampleResultPath + "run_time_base_euc.csv";
//		pathSampleResult = Global.sampleResultPath + "res_base_euc.txt";
//		samT.test(qps, AlgType.AlgEucDisBase, pathRecordRunTime, pathSampleResult);
		
		// AlgEucDisFastRange
//		pathRecordRunTime = Global.sampleResultPath + "run_time_base_fast.csv";
//		pathSampleResult = Global.sampleResultPath + "res_base_fast.txt";
//		samT.test(qps, AlgType.AlgEucDisFastRange, pathRecordRunTime, pathSampleResult);
		
		// AlgEucDisBaseOpticsWu
//		pathRecordRunTime = Global.sampleResultPath + "run_time_base_optic_wu.csv";
//		pathSampleResult = Global.sampleResultPath + "res_base_optic_wu.txt";
//		samT.test(qps, AlgType.AlgEucDisBaseOpticsWu, pathRecordRunTime, pathSampleResult);
		
		// AlgEucDisAdvancedOpticsWu
		pathRecordRunTime = Global.sampleResultPath + "run_time_advanced_optic_wu.csv";
		pathSampleResult = Global.sampleResultPath + "res_advanced_optic_wu.txt";
		samT.test(qps, AlgType.AlgEucDisAdvancedOpticsWu, pathRecordRunTime, pathSampleResult);
	}
}











