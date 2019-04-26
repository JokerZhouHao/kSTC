package utility.io;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utility.Global;

public class FileLoader {
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
			if(en.getValue() == Integer.MAX_VALUE) {	// 这是超过了阈值
//				num++;
//				System.out.println(en.getKey());
			} else if(en.getValue() > minLen) {
				num++;
				System.out.println(en.getKey() + Global.delimiterLevel1 + en.getValue());
			}
		}
		System.out.println("数量 : " + num);
	}
	
	
	
	public static void main(String[] args) throws Exception{
		String path = Global.inputPath + "term_2_pid_neighbors_len.txt,opticMinpts=1,opticEpsilon=0.1, maxPidNeighborsBytes=50000000";
		FileLoader.showTerm2Len(path, 2000);
	}
}
