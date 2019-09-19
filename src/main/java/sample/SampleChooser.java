package sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import entity.Term2Fre;
import entity.fastrange.Cellid2Nodes;
import index.CellidPidWordsIndex;
import entity.ListHash;
import entity.QueryParams;
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
	
	public static Map<String, Integer> ngbLens = null;
	
	private static RandomNumGenerator genSumFre = null;
	private static List<String> terms = null;
	private static List<Integer> fres = null;
	
	private static HashSet<String> filter = null;
	private static Map<String, Term2Fre> term2Fres = null;
	private static CellidPidWordsIndex cellidWIndex = null;
	
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
		
		// 用于清除有相同查询词的sample
		HashSet<ListHash<String>> recTerms = new HashSet<>();
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
			int i = 0;
			for(i=0; i<sampWords.length; i++) {
				int j = RandomNumGenerator.getRandomInt(0, words.size() - 1);
				sampWords[i] = words.get(j);
				if(ngbLens.get(sampWords[i]) == Integer.MAX_VALUE) {
					MLog.log(sampWords[i] + " is too long ");
					break;
				}
				words.remove(j);
			}
			if(i < sampWords.length)	continue;
			ListHash<String> lh = new ListHash<>(Arrays.asList(sampWords));
			if(recTerms.contains(lh))	continue;
			else recTerms.add(lh);
			
			bw.write(String.valueOf(num) + Global.delimiterLevel1);
			bw.write(String.valueOf(id) + Global.delimiterLevel1);
			bw.write(String.valueOf(sampCoords[0]) + Global.delimiterSpace + String.valueOf(sampCoords[1]));
			for(i=0; i<sampWords.length; i++) {
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
	 * 以词频作为概率选词
	 * @param numSample
	 * @param numWord
	 * @throws Exception
	 */
	public static void chooseSampleByFre(int numSample, int numWord) throws Exception {
		String pathSample = getPath(numSample, numWord);
		MLog.log("开始选取样本" + pathSample);
		long startTime = System.currentTimeMillis();
		
		if(coords == null)	coords = FileLoader.loadCoords(Global.pathIdNormCoord);
		if(genId == null)	genId = new RandomNumGenerator(0, coords.length);
		if(genSumFre == null) {
			// 读取term2Fre
			HashSet<String> filter = new HashSet<>();
			for(Entry<String, Integer> en : ngbLens.entrySet()) {
				if(en.getValue() == Integer.MAX_VALUE) {
					filter.add(en.getKey());
				}
			}
			List<Term2Fre> term2Fre = FileLoader.loadTerm2Fre(Global.pathTermFrequency, filter);
			
			// 构建选词list
			terms = new ArrayList<>();
			fres = new ArrayList<>();
			int count = 0;
			for(Term2Fre fre : term2Fre) {
				terms.add(fre.term);
				count += fre.frequency;
				fres.add(count);
			}
			genSumFre = new RandomNumGenerator(1, count);
		}
		
		// 用于清除有相同查询词的sample
		HashSet<ListHash<String>> recTerms = new HashSet<>();
		double[] sampCoords = new  double[2];
		Set<String> sampWords = new HashSet<>();
		int num = 1;
		
		BufferedWriter bw = IOUtility.getBW(pathSample);
		bw.write(head + "\n");
		while(num <= numSample) {
			int id = genId.getRandomInt();
			if(null == coords[id])	continue;
			sampCoords[0] = RandomNumGenerator.getRandomCoordDouble(coords[id][0]);
			sampCoords[1] = RandomNumGenerator.getRandomCoordDouble(coords[id][1]);
			
			// 将词频当做概率选词
			sampWords.clear();
			while(sampWords.size() != numWord) {
				int index = Collections.binarySearch(fres, genSumFre.getRandomInt());
				if(index >= 0)	sampWords.add(terms.get(index));
				else sampWords.add(terms.get(-index - 1));
			}
			ListHash<String> lh = new ListHash<>(sampWords);
			if(recTerms.contains(lh))	continue;
			else recTerms.add(lh);
			
			bw.write(String.valueOf(num) + Global.delimiterLevel1);
			bw.write(String.valueOf(id) + Global.delimiterLevel1);
			bw.write(String.valueOf(sampCoords[0]) + Global.delimiterSpace + String.valueOf(sampCoords[1]));
			for(String tm : sampWords) {
				bw.write(Global.delimiterSpace + tm);
			}
			bw.write("\n");
//			MLog.log("已生成" + num + "个查询");
			num++;
		}
		bw.close();
		MLog.log("over, 用时: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	/**
	 * 在object中，通过词频选词
	 * @param numSample
	 * @param numWord
	 * @throws Exception
	 */
	public static void chooseSampleByFreInObject(int numSample, int numWord, int minFre) throws Exception {
		String pathSample = getPath(numSample, numWord);
		MLog.log("开始选取样本" + pathSample);
		long startTime = System.currentTimeMillis();
		
		if(coords == null)	coords = FileLoader.loadCoords(Global.pathIdNormCoord);
		if(txts == null)	txts = FileLoader.loadText(Global.pathIdText);
		ArrayList<double[]> coordsList = new ArrayList<>(Arrays.asList(coords));
		ArrayList<String> txtsList = new ArrayList<>(Arrays.asList(txts));
		
		if(genId == null)	genId = new RandomNumGenerator(0, coordsList.size());
		if(filter == null) {
			filter = new HashSet<>();
			for(Entry<String, Integer> en : ngbLens.entrySet()) {
				if(en.getValue() == Integer.MAX_VALUE) {
					filter.add(en.getKey());
				}
			}
			List<Term2Fre> term2Fre = FileLoader.loadTerm2Fre(Global.pathTermFrequency, null);
			term2Fres = new HashMap<String, Term2Fre>();
			for(Term2Fre tf : term2Fre) {
				term2Fres.put(tf.term, tf);
			}
		}
		
		// 用于清除有相同查询词的sample
		HashSet<ListHash<String>> recTerms = new HashSet<>();
		HashSet<Integer> recIds = new HashSet<>();
		QueryParams qParams = new QueryParams();
		qParams.location = new Point(coords[0]);
		
		double[] sampCoords = new  double[2];
		Set<String> sampWords = null;
		int num = 1;
		
		BufferedWriter bw = IOUtility.getBW(pathSample);
		bw.write(head + "\n");
		while(num <= numSample) {
			int id = genId.getRandomInt();
			
			if(null == coordsList.get(id) || null == txtsList.get(id))	continue;
			sampCoords[0] = RandomNumGenerator.getRandomCoordDouble(coordsList.get(id)[0]);
			sampCoords[1] = RandomNumGenerator.getRandomCoordDouble(coordsList.get(id)[1]);
			
			List<String> words = LuceneUtility.getTerms(txtsList.get(id));
			if(words == null || words.isEmpty())	continue;	
			sampWords = chooseTermByFre(new HashSet<>(words), numWord, minFre);
			if(sampWords == null || sampWords.size() < numWord)	continue;
			
			if(numWord == 4) {
				coordsList.remove(id);
				txtsList.remove(id);
				genId = new RandomNumGenerator(0, coordsList.size());
			}
			
			ListHash<String> lh = new ListHash<>(sampWords);
			// 是否已添加
			if(recTerms.contains(lh))	continue;
			else recTerms.add(lh);
			
			// 该词组合出现频率是否大于minFre
			qParams.sWords = new ArrayList<>(sampWords);
			Cellid2Nodes cid2nds = cellidWIndex.searchWordsReCellid2Nodes(qParams, Global.allLocations);
			if(cid2nds.numNode() < minFre)	continue;
			
			
			bw.write(String.valueOf(num) + Global.delimiterLevel1);
			bw.write(String.valueOf(id) + Global.delimiterLevel1);
			bw.write(String.valueOf(sampCoords[0]) + Global.delimiterSpace + String.valueOf(sampCoords[1]));
			for(String tm : sampWords) {
				bw.write(Global.delimiterSpace + tm);
			}
			bw.write("\n");
			bw.flush();
//			MLog.log("已生成" + num + "个查询");
			num++;
			if(numWord == 4) {
				MLog.log(String.valueOf(num) + " - " + String.valueOf(txtsList.size()));
			}
		}
		bw.close();
		MLog.log("over, 用时: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	private static Set<String> chooseTermByFre(Set<String> terms, int numWord, int minFre) {
		List<Term2Fre> availTF = new ArrayList<>();
		boolean isAllTooLong = true;
		for(String t : terms) {
//			if(!filter.contains(t) && term2Fres.get(t).frequency >= minFre)	availTF.add(term2Fres.get(t));
//			availTF.add(term2Fres.get(t));
			
			
		}
		if(availTF.size() < numWord)	return null;
		Collections.sort(availTF);
		
		Set<String> choosedTerms = new HashSet<>();
		List<String> tms = new ArrayList();
		List<Integer> fres = new ArrayList<>();
		while(choosedTerms.size() < numWord) {
			tms.clear();
			fres.clear();
			int count = 0;
			for(Term2Fre fre : availTF) {
				tms.add(fre.term);
				count += fre.frequency;
				fres.add(count);
			}
			RandomNumGenerator genSumFre = new RandomNumGenerator(1, count);
			int index = Collections.binarySearch(fres, genSumFre.getRandomInt());
			if(index < 0)	index = -index - 1;
			choosedTerms.add(tms.get(index));
			availTF.remove(index);
		}
		return choosedTerms;
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
		
		String pathNgbLen = Global.outPath + "term_2_pid_neighbors_len.txt,opticMinpts=4,opticEpsilon=2.0E-4,maxPidNeighborsBytes=2147483631";
		SampleChooser.ngbLens = FileLoader.loadPidNgbLens(pathNgbLen);
		
		String path =  Global.getPathCellidRtreeidOrPidWordsIndex(300, 12);
		cellidWIndex = new CellidPidWordsIndex(path);
		cellidWIndex.openIndexReader();
		
		List<Integer> nws = new ArrayList<>();
//		nws.add(1);
//		nws.add(2);
//		nws.add(3);
		nws.add(4);
//		nws.add(5);
		for(int nw : nws) {
//			SampleChooser.chooseSample(500, nw);
//			SampleChooser.chooseSampleByFre(500, nw);
			SampleChooser.chooseSampleByFreInObject(50, nw, 100000);
		}
		
//		List<Sample> sams = load(3, 3);
//		System.out.println(sams);
		
		cellidWIndex.close();
	}
}
