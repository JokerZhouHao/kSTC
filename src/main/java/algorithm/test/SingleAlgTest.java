package algorithm.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algorithm.AlgEucDisAdvancedOptics;
import algorithm.AlgEucDisAdvancedOpticsWu;
import algorithm.AlgEucDisAdvancedOpticsWu2;
import algorithm.AlgEucDisBase;
import algorithm.AlgEucDisBaseOptics;
import algorithm.AlgEucDisBaseOpticsWu;
import algorithm.AlgEucDisFastRange;
import dbcv.DBCVCalculator;
import entity.AlgType;
import entity.QueryParams;
import entity.SortedClusters;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.IOUtility;
import utility.io.TimeUtility;

public class SingleAlgTest {
	
	public static AlgEucDisBase eucBase = null;
	public static AlgEucDisFastRange eucFast = null;
	
	public static AlgEucDisBaseOptics eucBaseOptics = null;
	public static AlgEucDisBaseOpticsWu eucBaseOpticsWu = null;
	
	public static AlgEucDisAdvancedOptics eucAdvancedOptics = null;
	public static AlgEucDisAdvancedOpticsWu eucAdvancedOpticsWu = null;
	public static AlgEucDisAdvancedOpticsWu2 eucAdvancedOpticsWu2 = null;
	
	/**
	 * test alg euc
	 * @param algName
	 * @param qParams
	 * @throws Exception
	 */
	public static void testAlgEuc(AlgType algType, QueryParams qParams) throws Exception{
		TimeUtility.reset();
		
		SortedClusters sClusters = null;
		String resPath = null;
		
		switch (algType) {
		case AlgEucDisBase:
			System.out.println("----------------------- AlgEucDisBase ----------------------");
			SingleAlgTest.eucBase = new AlgEucDisBase(qParams);
			sClusters = SingleAlgTest.eucBase.excuteQuery(qParams);
			resPath = Global.outPath + "res" + File.separator + "result_ecu_base_" + qParams.toString();
			break;
		case AlgEucDisBaseAdv1:
			System.out.println("----------------------- AlgEucDisBaseAdv1 ----------------------");
			SingleAlgTest.eucBase = new AlgEucDisBase(qParams);
			sClusters = SingleAlgTest.eucBase.excuteQuery(qParams);
			resPath = Global.outPath + "res" + File.separator + "result_ecu_base_adv1_" + qParams.toString();
			break;
		case AlgEucDisBaseAdv1_2:
			System.out.println("----------------------- AlgEucDisBaseAdv1_2 ----------------------");
			SingleAlgTest.eucBase = new AlgEucDisBase(qParams);
			sClusters = SingleAlgTest.eucBase.excuteQuery(qParams);
			resPath = Global.outPath+ "res" + File.separator  + "result_ecu_base_adv1_2_" + qParams.toString();
			break;
		case AlgEucDisFastRange:
			System.out.println("----------------------- AlgEucDisFastRange ----------------------");
			if(SingleAlgTest.eucFast == null)	SingleAlgTest.eucFast = new AlgEucDisFastRange(qParams);
			sClusters = SingleAlgTest.eucFast.excuteQuery(qParams);
			resPath = Global.outPath+ "res" + File.separator  + "result_ecu_fast_" + qParams.toString();
			break;
		case AlgEucDisBaseOptics:
			System.out.println("----------------------- AlgEucDisBaseOptics ----------------------");
			if(SingleAlgTest.eucBaseOptics == null)	SingleAlgTest.eucBaseOptics = new AlgEucDisBaseOptics(qParams);
			sClusters = SingleAlgTest.eucBaseOptics.excuteQuery(qParams, Global.pathOrderObjects + "_AlgEucDisBaseOptics" +
					"_" + qParams.toString());
//			sClusters = SingleAlgTest.eucBaseOptics.excuteQuery(qParams, Global.outPath+ "res" + File.separator + "_AlgEucDisBaseOptics" +
//					"_" + qParams.toString());
			resPath = Global.outPath+ "res" + File.separator  + "result_ecu_base_optics_" + qParams.toString();
			break;
		case AlgEucDisBaseOpticsWu:
			System.out.println("----------------------- AlgEucDisBaseOpticsWu ----------------------");
			if(SingleAlgTest.eucBaseOpticsWu == null)	SingleAlgTest.eucBaseOpticsWu = new AlgEucDisBaseOpticsWu(qParams);
			sClusters = SingleAlgTest.eucBaseOpticsWu.excuteQuery(qParams, Global.pathOrderObjects + "_AlgEucDisBaseOpticsWu" +
					"_" + qParams.toString());
			resPath = Global.outPath+ "res" + File.separator  + "result_ecu_base_optics_wu_" + qParams.toString();
			break;
		case AlgEucDisAdvancedOpticsWu:
			System.out.println("----------------------- AlgEucDisAdvancedOpticsWu ----------------------");
			if(SingleAlgTest.eucAdvancedOpticsWu == null)	SingleAlgTest.eucAdvancedOpticsWu = new AlgEucDisAdvancedOpticsWu(qParams);
			sClusters = SingleAlgTest.eucAdvancedOpticsWu.excuteQuery(qParams, Global.pathOrderObjects + "_AlgEucDisAdvancedOpticsWu" +
					"_" + qParams.toString());
			resPath = Global.outPath+ "res" + File.separator  + "result_ecu_advanced_optics_wu_" + qParams.toString();
			break;
		default:
			break;
		}
		
		System.out.println("用时：" + TimeUtility.getGlobalSpendMilTime() + "ms");
		
		if(null != sClusters) {
			IOUtility.writeSortedClusters(resPath, qParams, sClusters);
			System.out.println("共簇：" + sClusters.getClusters().size());
			
			// DBCV
//			System.out.println("DBCV: " + new DBCVCalculator(sClusters, (int)qParams.runTimeRec.numNid).DBCV());
		}
	}
	
	/**
	 * get QParamsYelpBussCase
	 * @return
	 * @throws Exception
	 */
	public static QueryParams getQParamsYelpBussCase() throws Exception{
		
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 10;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.0001;
		
		int numSample = 200;
		int type = 4;
		
		int k = 100000;
		int numWord = 0;
		
		int minpts = 10;
		double epsilon = 0.0001;
		double xi = 0.0001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		QueryParams qp = new QueryParams(rtreeFanout, alpha, steepDegree, h, opticMinpts, opticEpsilon,
										numSample, type, k, numWord, minpts, epsilon, 
										xi, maxPidNeighborsBytes);
		
		Point location = null;
		List<String> sWords = new ArrayList<>();
		
		double[] loca = {0.7, 0.7};
		location = new Point(loca);
		
//		sWords.add("Coffee".toLowerCase());
//		sWords.add("Tea".toLowerCase());
//		sWords.add("Breakfast".toLowerCase());
//		sWords.add("Sandwiches".toLowerCase());
//		sWords.add("Good".toLowerCase());
//		sWords.add("Bars".toLowerCase());
//		sWords.add("university".toLowerCase());
		sWords.add("kids".toLowerCase());
		sWords.add("Restaurants".toLowerCase());
		
		qp.numWord = sWords.size();
		
		qp.setCoordAndSWords(location, sWords);
		return qp;
	}
	
	
	/**
	 * get QueryParams1
	 * @return
	 * @throws Exception
	 */
	public static QueryParams getQParamsMeetup() throws Exception{
		
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 10;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.0001;
		
		int numSample = 200;
		int type = 4;
		
		int k = 5000;
		int numWord = 0;
		
		int minpts = 5;
		double epsilon = 0.01;
		double xi = 0.01;
		
		int maxPidNeighborsBytes = 2147483631;
		
		QueryParams qp = new QueryParams(rtreeFanout, alpha, steepDegree, h, opticMinpts, opticEpsilon,
										numSample, type, k, numWord, minpts, epsilon, 
										xi, maxPidNeighborsBytes);
		
		Point location = null;
		List<String> sWords = new ArrayList<>();
		
		double[] loca = {0.09712398229829364, 0.16236140314671882};
		location = new Point(loca);
		
//		sWords.add("Coffee".toLowerCase());
//		sWords.add("Tea".toLowerCase());
//		sWords.add("Breakfast".toLowerCase());
//		sWords.add("Sandwiches".toLowerCase());
//		sWords.add("Good".toLowerCase());
//		sWords.add("Bars".toLowerCase());
//		sWords.add("university".toLowerCase());
		sWords.add("health".toLowerCase());
		sWords.add("food".toLowerCase());
//		sWords.add("interested".toLowerCase());
//		sWords.add("share".toLowerCase());
		
		qp.numWord = sWords.size();
		
		qp.setCoordAndSWords(location, sWords);
		return qp;
	}
	
	public static QueryParams getQParamsArizona() throws Exception{
		
		int rtreeFanout = 50;
		double alpha = 0.5;
		double steepDegree = 0.1;
		int h = 10;
		
		int opticMinpts = 1;
		double opticEpsilon = 0.001;
		
		int numSample = 200;
		int type = 4;
		
		int k = 5000;
		int numWord = 0;
		
		int minpts = 20;
		double epsilon = 0.001;
		double xi = 0.001;
		
		int maxPidNeighborsBytes = 2147483631;
		
		QueryParams qp = new QueryParams(rtreeFanout, alpha, steepDegree, h, opticMinpts, opticEpsilon,
										numSample, type, k, numWord, minpts, epsilon, 
										xi, maxPidNeighborsBytes);
		
		Point location = null;
		List<String> sWords = new ArrayList<>();
		
		double[] loca = {0.09712398229829364, 0.16236140314671882};
		location = new Point(loca);
		
//		sWords.add("Coffee".toLowerCase());
//		sWords.add("Tea".toLowerCase());
//		sWords.add("Breakfast".toLowerCase());
//		sWords.add("Sandwiches".toLowerCase());
//		sWords.add("Good".toLowerCase());
//		sWords.add("Bars".toLowerCase());
//		sWords.add("university".toLowerCase());
		sWords.add("Retail".toLowerCase());
		sWords.add("Goods".toLowerCase());
//		sWords.add("interested".toLowerCase());
//		sWords.add("share".toLowerCase());
		
		qp.numWord = sWords.size();
		
		qp.setCoordAndSWords(location, sWords);
		return qp;
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println(Global.inputPath);
		System.out.println(Global.outPath);
		System.out.println();
		
//		QueryParams qParams = SingleAlgTest.getQParams();
		QueryParams qParams = SingleAlgTest.getQParamsYelpBussCase();
//		QueryParams qParams = SingleAlgTest.getQParamsArizona();
		QueryParams.display(qParams);
		
//		qParams.type = 1;
//		SingleAlgTest.testAlgEuc(AlgType.AlgEucDisBase, qParams);
//		qParams.type = 2;
//		SingleAlgTest.testAlgEuc(AlgType.AlgEucDisBaseAdv1, qParams);
//		qParams.type = 3;
//		SingleAlgTest.testAlgEuc(AlgType.AlgEucDisBaseAdv1_2, qParams);
//		qParams.type = 4;
//		SingleAlgTest.testAlgEuc(AlgType.AlgEucDisFastRange, qParams);
		
		SingleAlgTest.testAlgEuc(AlgType.AlgEucDisBaseOptics, qParams);
//		SingleAlgTest.testAlgEuc("AlgEucDisAdvancedOptics", qParams);
//		SingleAlgTest.testAlgEuc("AlgEucDisAdvancedOpticsWu2", qParams);
		
		SingleAlgTest.testAlgEuc(AlgType.AlgEucDisBaseOpticsWu, qParams);
//		SingleAlgTest.testAlgEuc(AlgType.AlgEucDisAdvancedOpticsWu, qParams);
		
	}

}
