package precomputation.dataset.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import entity.Node;
import entity.SGPLInfo;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.IOUtility;
import utility.io.IterableBufferReader;
import utility.io.LuceneUtility;

/**
 * load id_text.txt，id_name.txt，id_coord_latlon.txt three files
 * @author ZhouHao
 * @since 2018年11月6日
 */
public class FileLoader {
	public static String[] loadNames(String fp) throws Exception{
		IterableBufferReader<String> ibr = IOUtility.getIBW(fp);
		String[] names = null;
		String[] arr = null;
		for(String line : ibr) {
			if(line.startsWith(Global.delimiterPound)) {
				names = new String[Integer.parseInt(line.split(Global.delimiterPound)[1].trim())];
			} else {
				arr = line.split(Global.delimiterLevel1);
				names[Integer.parseInt(arr[0])] = arr[1];
			}
		}
		return names;
	}
	
	public static Set<String> loadSetNames(String fp) throws Exception{
		IterableBufferReader<String> ibr = IOUtility.getIBW(fp);
		Set<String> names = new HashSet<>();
		String[] arr = null;
		for(String line : ibr) {
			if(line.startsWith(Global.delimiterPound)) {
			} else {
				arr = line.split(Global.delimiterLevel1);
				names.add(arr[1].trim());
			}
		}
		return names;
	}
	
	/**
	 * notice : record sort is longitude, latitude
	 * @param fp
	 * @return
	 * @throws Exception
	 */
	public static double[][] loadCoords(String fp) throws Exception{
		IterableBufferReader<String> ibr = IOUtility.getIBW(fp);
		double[][] coords = null;
		String[] arr = null;
		int id = 0;
		for(String line : ibr) {
			if(line.startsWith(Global.delimiterPound)) {
				coords = new double[Integer.parseInt(line.split(Global.delimiterPound)[1].trim())][2];
			} else {
				arr = line.split(Global.delimiterLevel1);
				id = Integer.parseInt(arr[0]);
				arr = arr[1].split(Global.delimiterSpace);
				coords[id] = new double[2];
				coords[id][0] = Double.parseDouble(arr[0]);
				coords[id][1] = Double.parseDouble(arr[1]);
			}
		}
		return coords;
	}
	
	/**
	 * loadLocations
	 * @param fp
	 * @return
	 * @throws Exception
	 */
	public static Point[] loadPoints(String fp) throws Exception{
		IterableBufferReader<String> ibr = IOUtility.getIBW(fp);
		Point[] points = null;
		String[] arr = null;
		double[] location = null;
		int id = 0;
		for(String line : ibr) {
			if(line.startsWith(Global.delimiterPound)) {
				points = new Point[Integer.parseInt(line.split(Global.delimiterPound)[1].trim())];
			} else {
				arr = line.split(Global.delimiterLevel1);
				id = Integer.parseInt(arr[0]);
				arr = arr[1].split(Global.delimiterSpace);
				
				location = new double[2];
				location[0] = Double.parseDouble(arr[0]);
				location[1] = Double.parseDouble(arr[1]);
				
				points[id] = new Point(location);
			}
		}
		return points;
	}
	
	public static String[] loadText(String fp) throws Exception{
		IterableBufferReader<String> ibr = IOUtility.getIBW(fp);
		String[] texts = null;
		String[] arr = null;
		int start = 0;
		int id = 0;
		for(String line : ibr) {
			if(line.startsWith(Global.delimiterPound)) {
				texts = new String[Integer.parseInt(line.split(Global.delimiterPound)[1].trim())];
			} else {
				start = line.indexOf(Global.delimiterLevel1);
				id = Integer.parseInt(line.substring(0, start));
				start += Global.delimiterLevel1.length();
				texts[id] = line.substring(start);
			}
		}
		return texts;
	}
	
	public static Set<Integer>[] loadIdWids(String fp) throws Exception{
		BufferedReader br = IOUtility.getBR(fp);
		String line = br.readLine();
		int numId = Integer.parseInt(line.split(Global.delimiterPound)[1]);
		
		Set<Integer>[] allWids = new Set[numId];
		String[] arr = null;
		int id;
		Set<Integer> wids = null;
		
		while(null != (line = br.readLine())) {
			arr = line.split(Global.delimiterLevel1);
			allWids[Integer.parseInt(arr[0])] = wids = new HashSet<>();
			arr = arr[1].split(Global.delimiterLevel2);
			for(String st : arr) {
				wids.add(Integer.parseInt(st));
			}
		}
		br.close();
		return allWids;
	}
	
	public static String[] loadWords(String fp) throws Exception{
		BufferedReader br = IOUtility.getBR(fp);
		String line = br.readLine();
		int numId = Integer.parseInt(line.split(Global.delimiterPound)[1].trim());
		
		String[] words = new String[numId];
		int wid = 0;
		String[] arr = null;
		while(null != (line = br.readLine())) {
			arr = line.split(Global.delimiterLevel1);
			wid = Integer.parseInt(arr[0]);
			words[wid] = arr[1];
		}
		br.close();
		
		return words;
	}
	
	public static List<String>[] loadTerms(String fp) throws Exception{
		BufferedReader br = IOUtility.getBR(fp);
		String line = br.readLine();
		int numId = Integer.parseInt(line.split(Global.delimiterPound)[1].trim());
		
		List<String>[] allTerms = new List[numId];
		int pid = 0;
		String[] arr = null;
		while(null != (line = br.readLine())) {
			arr = line.split(Global.delimiterLevel1);
			pid = Integer.parseInt(arr[0]);
			allTerms[pid] = new ArrayList<>();
			arr = arr[1].split(Global.delimiterSpace);
			for(String st : arr) {
				if(!st.equals(""))	allTerms[pid].add(st);
			}
		}
		br.close();
		
		return allTerms;
	}
	
	public static String[] loadAllTerms(String fp) throws Exception{
		BufferedReader br = IOUtility.getBR(fp);
		String line = br.readLine();
		int numId = Integer.parseInt(line.split(Global.delimiterPound)[1].trim());
		
		String[] allTerms = new String[numId];
		int pid = 0;
		String[] arr = null;
		int wid = 0;
		while(null != (line = br.readLine())) {
			arr = line.split(Global.delimiterLevel1);
			allTerms[wid++] = arr[1];
		}
		br.close();
		return allTerms;
	}
	
	public static List<Node> loadOrderedFile(String fp) throws Exception{
		ObjectInputStream ois = IOUtility.getOIS(fp);
		List<Node> nodes = new ArrayList<>();
		Node tNd = null;
		try {
			while(Boolean.TRUE)
				nodes.add((Node)ois.readObject());
		} catch (EOFException e) {
			// TODO: handle exception
		}
		ois.close();
		if(nodes.isEmpty())	return null;
		else return nodes;
	}
	
	public static Map<String, Integer> loadPidNgbLens(String fp) throws Exception{
		BufferedReader br = IOUtility.getBR(fp);
		String line = br.readLine();
		Map<String, Integer> lens = new HashMap<>();
		String[] arr = null;
		while(null != (line = br.readLine())) {
			arr = line.split(Global.delimiterLevel1);
			if(lens.containsKey(arr[0].trim())) {
				System.out.println(line);
				System.exit(0);
			}
			lens.put(arr[0].trim(), Integer.parseInt(arr[1]));
		}
		br.close();
		return lens;
	}
	
	// 读取term_2_pid_neighbors_len.txt,opticMinpts=1,opticEpsilon=0.1, maxPidNeighborsBytes=50000000
	public static Map<String, Integer> loadPNgbLen(String path) throws Exception{
		BufferedReader br = IOUtility.getBR(path);
		Map<String, Integer> term2Len = new HashMap<>();
		String line = null;
		while(null != (line =br.readLine())) {
			if(line.startsWith("#"))	continue;
			String[] arr = line.split(Global.delimiterLevel1);
			term2Len.put(arr[0], Integer.parseInt(arr[1]));
		}
		br.close();
		return term2Len;
	}
	
	// 查看Ngb大于一定长度的词
	public static void showTerm2Len(String path, Integer minLen) throws Exception{
		Map<String, Integer> term2Len = FileLoader.loadPNgbLen(path);
		int num = 0;
		for(Entry<String, Integer> en : term2Len.entrySet()) {
			if(minLen != Integer.MAX_VALUE) {
				if(en.getValue() > minLen) {
					num++;
					System.out.println(en.getKey() + Global.delimiterLevel1 + en.getValue());
				}
			} else {
				if(en.getValue() == Integer.MAX_VALUE) {
					num++;
					System.out.println(en.getKey() + Global.delimiterLevel1 + en.getValue());
				}
			}
		}
		System.out.println("数量 : " + num);
	}
	
	/**
	 * 输出词频文件
	 * @param texts
	 * @param path
	 * @throws Exception
	 */
	public static void writeWidFrequency(List<String> texts, String path) throws Exception{
		List<String> terms = null;
		Map<String, Integer> term2Fre = new HashMap<>();
		Integer ti = null;
		for(String txt : texts) {
			terms = LuceneUtility.getTerms(txt);
			for(String tm : terms) {
				if(null != (ti = term2Fre.get(tm))) {
					term2Fre.put(tm, ti + 1);
				} else term2Fre.put(tm, 1);
			}
		}
		Map<Integer, List<String>> fre2Term = new TreeMap<>();
		for(Entry<String, Integer> en : term2Fre.entrySet()) {
			if(null == (terms = fre2Term.get(en.getValue()))){
				terms = new ArrayList<>();
				fre2Term.put(en.getValue(), terms);
			}
			terms.add(en.getKey());
		}
		
		BufferedWriter bw = IOUtility.getBW(path);
		bw.write("#" + String.valueOf(term2Fre.size()) + '\n');
		for(Entry<Integer, List<String>> en : fre2Term.entrySet()) {
			terms = en.getValue();
			for(String tm : terms) {
				bw.write(String.valueOf(en.getKey()));
				bw.write(Global.delimiterLevel1);
				bw.write(tm);
				bw.write('\n');
			}
		}
		bw.close();
		System.out.println("> over");
	}
	
	/**
	 * 输出cell2pids文件
	 * @param pathCoord
	 */
	public static void writeCellId2Pids(String pathCoord, String pathCellid2Pids) throws Exception{
		System.out.println("> start write " + pathCellid2Pids);
		SGPLInfo sgpl = Global.sgplInfo;
		double[][] coords = loadCoords(pathCoord);
		Map<Integer, List<Integer>> cellId2Pids = new TreeMap<>();
		List<Integer> pids = null;
		int cellId = 0;
		for(int pid=0; pid < coords.length; pid++) {
			cellId = sgpl.getZOrderId(coords[pid]);
			if(null == (pids = cellId2Pids.get(cellId))) {
				pids = new ArrayList<>();
				cellId2Pids.put(cellId, pids);
			}
			pids.add(pid);
		}
		
		BufferedWriter bw = IOUtility.getBW(pathCellid2Pids);
		bw.write("#" + String.valueOf(cellId2Pids.size()) + "\n");
		for(Entry<Integer, List<Integer>> en : cellId2Pids.entrySet()) {
			bw.write(String.valueOf(en.getKey()));
			bw.write(Global.delimiterLevel1);
			for(int i=0; i<en.getValue().size() - 1; i++)
				bw.write(String.valueOf(en.getValue().get(i)) + Global.delimiterLevel2);
			bw.write(String.valueOf(en.getValue().get(en.getValue().size() - 1)));
			bw.write('\n');
		}
		bw.close();
		System.out.println("> over");
	}
	
	public static Map<Integer, List<Integer>> loadCellid2Pids(String path) throws Exception{
		BufferedReader br = IOUtility.getBR(path);
		Map<Integer, List<Integer>> c2p = new HashMap<>();
		String line = null;
		String[] arr = null;
		int cellid = 0;
		List<Integer> pids = null;
		while(null != (line = br.readLine())) {
			if(!line.startsWith(Global.delimiterPound)) {
				arr = line.split(Global.delimiterLevel1);
				cellid = Integer.parseInt(arr[0]);
				arr = arr[1].split(Global.delimiterLevel2);
				pids = new ArrayList<>();
				for(String st : arr) {
					pids.add(Integer.parseInt(st));
				}
				c2p.put(cellid, pids);
			}
		}
		br.close();
		return c2p;
	}
	
	public static void main(String[] args) throws Exception{
//		String str = "#123";
//		System.out.println(str.split("#")[1]);
//		FileLoader.loadNames(Global.pathIdName);
//		FileLoader.loadCoords(Global.pathIdCoord);
//		FileLoader.loadPoints(Global.pathIdCoord);
//		FileLoader.loadText(Global.pathIdText);
//		FileLoader.loadIdWids(Global.pathIdWids);
//		FileLoader.loadWords(Global.pathWidWord);
//		FileLoader.loadTerms(Global.pathIdTerms);
//		String[] allTerms = FileLoader.loadAllTerms(Global.pathWidTerms);
//		Map<String, Integer> lens = FileLoader.loadPidNgbLens(Global.pathPidNeighborLen);
//		
//		
//		ObjectOutputStream oos = IOUtility.getOOS(Global.pathTestFile);
//		double []cods = {1.0, 2.0};
//		Node nd1 = new Node(1, new Point(cods), 1.0, 1.0);
//		Node nd2 = new Node(2, new Point(cods), 2.0, 2.0);
//		Node nd3 = new Node(2, new Point(cods), 2.0, 2.0);
//		oos.writeObject(nd1);
//		oos.writeObject(nd2);
//		oos.writeObject(nd3);
//		oos.close();
//		List<Node> nds = loadOrderedFile(Global.pathTestFile);
//		System.out.println(nds);
		
//		double db = -115.283122245;
//		float ft = -115.283122245f;
//		System.out.println(ft);
		
		writeCellId2Pids(Global.pathIdCoord + Global.signNormalized, Global.pathCell2Pids);
		loadCellid2Pids(Global.pathCell2Pids);
		
	}
}
