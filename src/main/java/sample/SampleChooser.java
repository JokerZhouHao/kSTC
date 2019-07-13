package sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import entity.Sample;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.MLog;
import utility.RandomNumGenerator;
import utility.io.IOUtility;
import utility.io.LuceneUtility;
import utility.io.TimeUtility;

/**
 * 测试样本选择器
 * @author ZhouHao
 * @since 2019年6月24日
 */
public class SampleChooser {
	private static final String head = "#id: orgId: lon lat word word word . . .";
	
	private static double[][] coords = null;
	private static String[] txts = null;
	private static RandomNumGenerator genId = null;
	private static String pathFormat = "testSample.ns=%s.nw=%s";
	
	/**
	 * 获得sample path
	 * @param numSample
	 * @param numWord
	 * @return
	 */
	public static String getPath(int numSample, int numWord) {
		return Global.pathSample + String.format(pathFormat, numSample, numWord);
	}
	
	/**
	 * 选择样本
	 * @param numSample
	 * @param numWord
	 * @throws Exception
	 */
	public static void chooseSample(int numSample, int numWord) throws Exception {
		String pathSample = getPath(numSample, numWord);
		MLog.log("开始选取样本" + pathSample);
		long startTime = System.currentTimeMillis();
		
		if(coords == null)	coords = FileLoader.loadCoords(Global.pathIdNormCoord);
		if(txts == null)	txts = FileLoader.loadText(Global.pathIdText);
		if(genId == null)	genId = new RandomNumGenerator(0, coords.length);
		
		double[] sampCoords = new  double[2];
		String[] sampWords = new String[numWord];
		int num = 1;
		
		BufferedWriter bw = IOUtility.getBW(pathSample);
		bw.write(head + "\n");
		while(num <= numSample) {
			int id = genId.getRandomInt();
			if(null == coords[id] || null == txts[id])	continue;
			sampCoords[0] = RandomNumGenerator.getRandomCoordDouble(coords[id][0]);
			sampCoords[1] = RandomNumGenerator.getRandomCoordDouble(coords[id][1]);
			
			List<String> words = LuceneUtility.getTerms(txts[id]);
			if(words == null || words.isEmpty())	continue;	
			words = new ArrayList<>(new HashSet<>(words));	// 去重
			if(words.size() < sampWords.length)	continue;
			for(int i=0; i<sampWords.length; i++) {
				int j = RandomNumGenerator.getRandomInt(0, words.size() - 1);
				sampWords[i] = words.get(j);
				words.remove(j);
			}
			
			bw.write(String.valueOf(num) + Global.delimiterLevel1);
			bw.write(String.valueOf(id) + Global.delimiterLevel1);
			bw.write(String.valueOf(sampCoords[0]) + Global.delimiterSpace + String.valueOf(sampCoords[1]));
			for(int i=0; i<sampWords.length; i++) {
				bw.write(Global.delimiterSpace + sampWords[i]);
			}
			bw.write("\n");
//			MLog.log("已生成" + num + "个查询");
			num++;
		}
		bw.close();
		MLog.log("over, 用时: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	/**
	 * 读取sample文件
	 * @param numSample
	 * @param numWord
	 * @return
	 * @throws Exception
	 */
	public static List<Sample>	load(int numSample, int numWord) throws Exception {
		List<Sample> sams = new ArrayList<>();
		BufferedReader br = IOUtility.getBR(getPath(500, numWord));
		String line = null;
		while(null != (line = br.readLine()) && numSample > 0) {
			if(line.startsWith("#") || line.trim().isEmpty())	continue;
			sams.add(new Sample(line));
			numSample--;
		}
		br.close();
		return sams;
	}
	
	public static void main(String[] args) throws Exception {
		Global.displayInputOutputPath();
		
		List<Integer> nws = new ArrayList<>();
		nws.add(1);
		nws.add(2);
		nws.add(3);
		nws.add(4);
		nws.add(5);
		for(int nw : nws) {
			SampleChooser.chooseSample(500, nw);
		}
		
//		List<Sample> sams = load(3, 3);
//		System.out.println(sams);
	}
}
