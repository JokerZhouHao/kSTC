package algorithm.test;

import java.io.BufferedWriter;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import algorithm.AlgEucDisAdvancedOpticsWu;
import algorithm.AlgEucDisBase;
import algorithm.AlgEucDisBaseOptics;
import algorithm.AlgEucDisBaseOpticsWu;
import algorithm.AlgEucDisFastRange;
import algorithm.AlgInterface;
import entity.AlgType;
import entity.QueryParams;
import entity.Sample;
import sample.SampleChooser;
import services.RunTimeRecordor;
import utility.Global;
import utility.MLog;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * 用来执行算法
 * @author ZhouHao
 * @since 2019年6月24日
 */
public class AlgExcutor implements Runnable{
	private static AlgEucDisBase eucBase = null;
	private static AlgEucDisFastRange eucFast = null;
	
	private static AlgEucDisBaseOpticsWu eucBaseOpticsWu = null;
	public static AlgEucDisAdvancedOpticsWu eucAdvancedOpticsWu = null;
	
	private ArrayBlockingQueue<QueryParams> queueQP = null;
	
	public AlgExcutor(ArrayBlockingQueue<QueryParams> queue) {
		this.queueQP = queue;
	}
	
	@Override
	public void run() {
		try {
			Long startTime = System.currentTimeMillis();
			QueryParams qp = null;
			while(Global.signQueryThreadOver != (qp = queueQP.take())) {
				startTime = System.currentTimeMillis();
				MLog.log("开始查询  " + qp.toString());
				exceute(qp);
				MulAlgTest.decrease();
				MLog.log("over " + qp.toString() + " , 还剩 " + MulAlgTest.currentNumQuery() + " 个query, " + 
						"用时: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("AlgExcutor线程异常退出");
			System.exit(0);
		}
	}
	
	public void exceute(QueryParams qp) throws Exception{
		AlgInterface alg = null;
		switch (qp.algType()) {
			case AlgEucDisBase:
				alg = new AlgEucDisBase(qp);
				break;
			case AlgEucDisFastRange:
				alg = new AlgEucDisFastRange(qp);
				break;
			case AlgEucDisBaseOpticsWu:
				alg = new AlgEucDisBaseOpticsWu(qp);
				break;
			case AlgEucDisAdvancedOpticsWu:
				alg = new AlgEucDisAdvancedOpticsWu(qp);
				break;
			default:
				break;
		}
		
		List<Sample> samples = SampleChooser.load(qp.numSample, qp.numWord);
		
		String resPath = Global.sampleResultPath + QueryParams.resFileName(qp);
		BufferedWriter bw = IOUtility.getBW(resPath);
		bw.write(RunTimeRecordor.getHeader());
		
		for(int i=0; i<samples.size(); i++) {
			qp.runTimeRec = new RunTimeRecordor();
			qp.setCoordAndSWords(samples.get(i).coords, samples.get(i).sWords);
			alg.excuteQuery(qp);
			bw.write(qp.runTimeRec.getTimeStr(i + 1));
			bw.flush();
		}
		alg.free();
		
		bw.close();
	}
}
