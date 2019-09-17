package algorithm.test;

import java.util.List;

import algorithm.AlgEucDisBaseOptics;
import algorithm.AlgEucDisBaseOpticsWu;
import algorithm.AlgEucDisFastRange;
import algorithm.AlgInterface;
import dbcv.DBCVCalculator;
import entity.QueryParams;
import entity.Sample;
import entity.SortedClusters;
import sample.SampleChooser;
import services.RunTimeRecordor;
import utility.Global;
import utility.MLog;
import utility.io.TimeUtility;

/**
 * 用于计算DBCV
 * @author ZhouHao
 * @since 2019年7月8日
 */
public class DBCVTest {
	private int numEachGroup = -1;
	private List<QueryParams> qps = null;
	private double[][] dbcvs = null;
	
	private final static int numSpace = 10;
	private final static String formatHead = "%-" + numSpace + "s";
	private final static String formatDouble = "%-1." + (numSpace - 4) + "f";
	
	private AlgInterface[] algs = new AlgInterface[3]; 
	
	public DBCVTest(String path, int numEachGroup) throws Exception{
		this.numEachGroup = numEachGroup;
		qps = QueryParams.load(path);
	}
	
	public void showDBCV() {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<qps.size(); i++) {
			sb.append(String.format(formatHead, qps.get(i).numWord));
		}
		sb.append(String.format(formatHead, "AvgTotal") + "\n");
		
		for(int i=0; i<algs.length; i++) {
			for(int j=0; j<qps.size() + 1; j++) {
				sb.append(String.format(formatHead, String.format(formatDouble, dbcvs[i][j])));
			}
			sb.append('\n');
		}
		System.out.println(sb.toString());
	}
	
	public void showQPs() {
		QueryParams.displays(qps);
	}
	
	/**
	 * 用qp更新algs
	 * @param qp
	 * @throws Exception
	 */
	private void updateAlgs(QueryParams qp) throws Exception{
		for(int i=0; i<algs.length; i++) {
			if(algs[i] != null)	algs[i].free();
		}
		algs[0] = new AlgEucDisFastRange(qp);
		algs[1] = new AlgEucDisBaseOptics(qp);
		algs[2] = new AlgEucDisBaseOpticsWu(qp);
	}
	
	public void calDBCV() throws Exception{
		dbcvs = new double[algs.length][qps.size() + 1];
		for(int i=0; i<qps.size(); i++) {
			QueryParams qp = qps.get(i);
			updateAlgs(qp);
			List<Sample> samples = SampleChooser.load(qp.numSample, qp.numWord);
			double num = 0;
			for(int j=0; j<samples.size(); j++) {
				double[] tempDV = new double[algs.length];
				int k=0;
				for(; k<algs.length; k++) {
					qp.runTimeRec = new RunTimeRecordor();
					qp.setCoordAndSWords(samples.get(j).coords, samples.get(j).sWords);
					SortedClusters sClu = algs[k].excuteQuery(qp);
					if(sClu == null)	break;
					MLog.logNoln(qps.get(i).numWord + " " + j + " " + k + " " + sClu.getSize());
					
//					if(Global.inputPath.contains("yelp_academic") && sClu.getSize() >= 500) {
//						System.out.println(" Size >= 500");
//						break;	// 避免计算时间过长 yelp_buss
//					}
					if(Global.inputPath.contains("meetup") && sClu.getSize() >= 200) {
						System.out.println(" Size >= 200");
						break;	// 避免计算时间过长 meetup
					}
//					if(Global.inputPath.contains("places_dump") && sClu.getSize() >= 500) {
//						System.out.println(" Size >= 500");
//						break;	// 避免计算时间过长 places_dump
//					}
					
					tempDV[k] = DBCVCalculator.DBCV(sClu, (int)qp.runTimeRec.numNid);
					if(Double.isNaN(tempDV[k])) {
						System.out.println(" NaN");
						break;
					}
					
					System.out.println(" " + tempDV[k]);
				}
				if(k == algs.length) {
					num++;
					for(k=0; k<algs.length; k++) {
						dbcvs[k][i] += tempDV[k];
					}
					if(num == numEachGroup) {
						for(k=0; k<algs.length; k++) {
							dbcvs[k][i] /= numEachGroup;
						}
						break;
					}
				}
			}
			MLog.blackLine();
		}
		for(int i=0; i<algs.length; i++) {
			dbcvs[i][qps.size()] = 0;
			for(int j=0; j<qps.size(); j++) {
				dbcvs[i][qps.size()] += dbcvs[i][j];
			}
			dbcvs[i][qps.size()] /= qps.size();
		}
	}
	
	public static void displayDBCV(String path, int numEachGroup) throws Exception{
		long startTime = System.currentTimeMillis();
		DBCVTest test = new DBCVTest(path, numEachGroup);
		test.showQPs();
		test.calDBCV();
		test.showDBCV();
		MLog.log("Over, spend time: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	public static void main(String[] args) throws Exception {
//		System.out.println(String.format("%-1.5f", 0.00000001));
		Global.displayInputOutputPath();
		String path = Global.sampleResultPath + "dbcvtest.txt";
		int numEachGroup = 1;
		if(args.length > 0)		numEachGroup = Integer.parseInt(args[0]);
		else numEachGroup = 10;
		MLog.log("NumEachGroup: " + numEachGroup);
		DBCVTest.displayDBCV(path, numEachGroup);
	}
	
}

