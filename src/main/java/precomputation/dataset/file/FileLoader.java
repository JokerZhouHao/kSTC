package precomputation.dataset.file;

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
	
	public static void main(String[] args) throws Exception{
//		String str = "#123";
//		System.out.println(str.split("#")[1]);
//		FileLoader.loadNames(Global.pathIdName);
//		FileLoader.loadCoords(Global.pathIdCoord);
//		FileLoader.loadText(Global.pathIdText);
//		FileLoader.loadIdWids(Global.pathIdWids);
		FileLoader.loadWords(Global.pathWidWord);
		
//		double db = -115.283122245;
//		float ft = -115.283122245f;
//		System.out.println(ft);
	}
}
