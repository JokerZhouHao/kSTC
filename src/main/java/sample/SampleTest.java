package sample;

import java.io.BufferedWriter;
import java.util.List;

import algorithm.AlgEucDisAdvancedOptics;
import algorithm.AlgEucDisAdvancedOpticsWu;
import algorithm.AlgEucDisBase;
import algorithm.AlgEucDisBaseOptics;
import algorithm.AlgEucDisBaseOpticsWu;
import algorithm.AlgEucDisFastRange;
import algorithm.AlgTest;
import entity.QueryParams;
import utility.io.IOUtility;

public class SampleTest {
	private AlgEucDisBase eucBase = new AlgEucDisBase();
	private AlgEucDisFastRange eucFast = new AlgEucDisFastRange();
	
	private AlgEucDisBaseOptics eucBaseOptics = new AlgEucDisBaseOptics();
	private AlgEucDisBaseOpticsWu eucBaseOpticsWu = new AlgEucDisBaseOpticsWu();
	
	private AlgEucDisAdvancedOptics eucAdvancedOptics = new AlgEucDisAdvancedOptics();
	private AlgEucDisAdvancedOpticsWu eucAdvancedOpticsWu = new AlgEucDisAdvancedOpticsWu();
	
	private List<QueryParams> qps = null;
	private String pathRecordRunTime = null;
	
	public SampleTest(List<QueryParams> qps, String pathRecordRunTime) throws Exception {
		this.qps = qps;
		this.pathRecordRunTime = null;
	}
	
	public void test() throws Exception{
		System.out.println("> 开始测试" + String.valueOf(qps.size()) + "个样本 . . . .");
		BufferedWriter bw = IOUtility.getBW(pathRecordRunTime); 
		
		
		
		
		bw.close();
	}
	
}
