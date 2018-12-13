package precomputation.dataset.file;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import entity.Node;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.IOUtility;
import utility.io.IterableBufferReader;

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
		String[] allTerms = FileLoader.loadAllTerms(Global.pathWidTerms);
		
		
		ObjectOutputStream oos = IOUtility.getOOS(Global.pathTestFile);
		double []cods = {1.0, 2.0};
		Node nd1 = new Node(1, new Point(cods), 1.0, 1.0);
		Node nd2 = new Node(2, new Point(cods), 2.0, 2.0);
		Node nd3 = new Node(2, new Point(cods), 2.0, 2.0);
		oos.writeObject(nd1);
		oos.writeObject(nd2);
		oos.writeObject(nd3);
		oos.close();
		List<Node> nds = loadOrderedFile(Global.pathTestFile);
		System.out.println(nds);
		
//		double db = -115.283122245;
//		float ft = -115.283122245f;
//		System.out.println(ft);
	}
}
