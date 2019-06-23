package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.QueryParams;
import entity.SortedClusters;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.IOUtility;
import utility.io.TimeUtility;

public class AlgTest {
	
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
	public static void testAlgEuc(String algName, QueryParams qParams) throws Exception{
		TimeUtility.reset();
		
		SortedClusters sClusters = null;
		String resPath = null;
		
		if(algName.equals("base")) {
			System.out.println("----------------------- AlgEucDisBase ----------------------");
			sClusters = AlgTest.eucBase.excuteQuery(qParams);
			resPath = Global.outPath + "result_ecu_base.txt";
		} else if(algName.equals("fast")) {
			System.out.println("----------------------- AlgEucDisFastRange ----------------------");
			sClusters = AlgTest.eucFast.excuteQuery(qParams);
			resPath = Global.outPath + "result_ecu_fast.txt";
		} else if(algName.equals("AlgEucDisBaseOptics")) {
			System.out.println("----------------------- AlgEucDisBaseOptics ----------------------");
			sClusters = AlgTest.eucBaseOptics.excuteQuery(qParams, Global.pathOrderObjects + "_AlgEucDisBaseOptics");
			resPath = Global.outPath + "result_ecu_base_optics.txt";
		} else if(algName.equals("AlgEucDisBaseOpticsWu")) {
			System.out.println("----------------------- AlgEucDisBaseOpticsWu ----------------------");
			sClusters = AlgTest.eucBaseOpticsWu.excuteQuery(qParams, Global.pathOrderObjects + "_AlgEucDisBaseOpticsWu");
//			sClusters = AlgTest.eucBaseOpticsWu.excuteQuery(qParams, null);
			resPath = Global.outPath + "result_ecu_base_optics_wu.txt";
		} else if(algName.equals("AlgEucDisAdvancedOptics")) {
			System.out.println("----------------------- AlgEucDisAdvancedOptics ----------------------");
			sClusters = AlgTest.eucAdvancedOptics.excuteQuery(qParams, Global.pathOrderObjects + "_AlgEucDisAdvancedOptics");
			resPath = Global.outPath + "result_ecu_advanced_optics.txt";
		} else if(algName.equals("AlgEucDisAdvancedOpticsWu")) {
			System.out.println("----------------------- AlgEucDisAdvancedOpticsWu ----------------------");
			sClusters = AlgTest.eucAdvancedOpticsWu.excuteQuery(qParams, Global.pathOrderObjects + "_AlgEucDisAdvancedOpticsWu");
			resPath = Global.outPath + "result_ecu_advanced_optics_wu.txt";
		} else if(algName.equals("AlgEucDisAdvancedOpticsWu2")) {
			System.out.println("----------------------- AlgEucDisAdvancedOpticsWu2 ----------------------");
			sClusters = AlgTest.eucAdvancedOpticsWu2.excuteQuery(qParams, Global.pathOrderObjects + "_AlgEucDisAdvancedOpticsWu2");
			resPath = Global.outPath + "result_ecu_advanced_optics_wu2.txt";
		}
		
		System.out.println("用时：" + TimeUtility.getGlobalSpendMilTime() + "ms");
		
//		System.out.println(sClusters);
		
		if(null != sClusters) {
			IOUtility.writeSortedClusters(resPath, qParams, sClusters);
			System.out.println("共簇：" + sClusters.getClusters().size());
		}
	}
	
	/**
	 * get QueryParams
	 * @return
	 * @throws Exception
	 */
	public static QueryParams getQParams() throws Exception{
		String[] allTxt = FileLoader.loadText(Global.pathIdText);
		Point[] allCoord = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
		
//		int id = new Random().nextInt(Global.numNode);
		
		// #QueryParams [location=[0.07741439372142855, 0.7209741612375], sWords=[4628], k=10, epsilon=1.0E-4, minpts=5]
//		QueryParams qParams = new QueryParams(allCoord[id], allTxt[id], 3, 3, 0.005, 2);
		
		/* fast range */
//		#QueryParams [location=[0.07129198357142852, 0.25200787187500007], sWords=[w, pet], k=5, epsilon=0.08, minpts=2]
		QueryParams qParams = new QueryParams();
//		double[] loca = {0.07129198357142852, 0.25200787187500007};
		double[] loca = {0.7, 0.7};
		qParams.location = new Point(loca);
		List<String> words = new ArrayList<>();
//		words.add("w");
//		words.add("pet");
		
		/* base */
//		QueryParams qParams = new QueryParams();
//		double[] loca = {0.7, 0.7};
//		qParams.location = new Point(loca);
//		List<String> words = new ArrayList<>();
////		words.add("91");
////		words.add("f");
//		words.add("b");
//		words.add("c");
//		qParams.sWords = words;
//		qParams.k = 3;
//		qParams.epsilon = 0.2;
//		qParams.minpts = 1;
		
//		words.add("Coffee".toLowerCase());
//		words.add("Tea".toLowerCase());
//		words.add("Breakfast".toLowerCase());
//		words.add("Sandwiches".toLowerCase());
//		words.add("Good".toLowerCase());
//		words.add("Bars".toLowerCase());
		words.add("university".toLowerCase());
		
		
		qParams.sWords = words;
		qParams.k = 5000000;
		qParams.epsilon = 0.001;	//  用于AlgEucDisBase
		qParams.minpts = 5;
		qParams.xi = 0.01;		// 用于AlgEucDisBaseOptics
		
		return qParams;
	}
	
	public static void main(String[] args) throws Exception{
		AlgTest.eucBase = new AlgEucDisBase();
		AlgTest.eucFast = new AlgEucDisFastRange();
		
		AlgTest.eucBaseOptics = new AlgEucDisBaseOptics();
		AlgTest.eucBaseOpticsWu = new AlgEucDisBaseOpticsWu();
		
		AlgTest.eucAdvancedOptics = new AlgEucDisAdvancedOptics();
		AlgTest.eucAdvancedOpticsWu = new AlgEucDisAdvancedOpticsWu();
//		AlgTest.eucAdvancedOpticsWu2 = new AlgEucDisAdvancedOpticsWu2();
		
		QueryParams qParams = AlgTest.getQParams();
		
		AlgTest.testAlgEuc("base", qParams);
		AlgTest.testAlgEuc("fast", qParams);
		
//		AlgTest.testAlgEuc("AlgEucDisBaseOpticsWu", qParams);
//		AlgTest.testAlgEuc("AlgEucDisAdvancedOpticsWu", qParams);
		
		
//		AlgTest.testAlgEuc("AlgEucDisAdvancedOpticsWu2", qParams);
		
//		AlgTest.testAlgEuc("AlgEucDisBaseOptics", qParams);
//		AlgTest.testAlgEuc("AlgEucDisAdvancedOptics", qParams);
		
		
	}

}
