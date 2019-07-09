package precomputation.dataset.meetup;

import java.io.BufferedReader;
import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import precomputation.dataset.file.FileLoader;
import precomputation.dataset.file.OrginalFileWriter;
import utility.Global;
import utility.MLog;
import utility.io.IOUtility;
import utility.io.TimeUtility;

public class AdjustOrginalData {
	public final static String pathOrginalData = Global.datasetPath + "orginal_data" + File.separator;
	
	/**
	 * 切分行
	 * @param line
	 * @return
	 */
	public static String[] splitLine(String line) {
		int index1 = line.indexOf(',');
		String[] arr = new String[3];
		arr[0] = line.substring(0, index1);
		
		int index2 = line.indexOf(',', index1 + 1);
		arr[1] = line.substring(index1 + 1, index2);
		
		arr[2] = line.substring(index2 + 1);
		
		return arr;
	}
	
	public static Boolean isNumic(String str) {
		Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	}
	
	/**
	 * 写文件
	 * @param path
	 * @throws Exception
	 */
	public static void writeIdNameAndIdTextFiles(String path) throws Exception {
		long startTime = System.currentTimeMillis();
		MLog.log("start writeIdNameAndIdTextFiles . . .");
		
		OrginalFileWriter idNameWriter = new OrginalFileWriter(Global.pathOrgId2Name);
		OrginalFileWriter idTextWriter = new OrginalFileWriter(Global.pathOrgId2Text);
		idNameWriter.writeLine("               ");
		idTextWriter.writeLine("               ");
		
		BufferedReader br = IOUtility.getBR(path);
		String line = null;
		int counter = 0;
		while(null != (line = br.readLine())) {
			String[] arr = splitLine(line);
			idNameWriter.write(counter, arr[1]);
			idTextWriter.write(counter, arr[2]);
			counter++;
		}
		br.close();
		idNameWriter.close();
		idTextWriter.close();
		
		line = Global.delimiterPound + String.valueOf(counter);
		IOUtility.setFirstLine(Global.pathOrgId2Name, line);
		IOUtility.setFirstLine(Global.pathOrgId2Text, line);
		
		MLog.log("spend time: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	public static void writeIdCoordFile(String path) throws Exception {
		long startTime = System.currentTimeMillis();
		MLog.log("start writeIdCoordFile . . .");
		
		OrginalFileWriter idCoordWriter = new OrginalFileWriter(Global.pathOrgId2Coord);
		idCoordWriter.writeLine("               ");
		String line = null;
		int counter = 0;
		BufferedReader br = IOUtility.getBR(path);
		while(null != (line = br.readLine())) {
			String[] arr = splitLine(line);
			
			if(isNumic(arr[0])) {
				arr[2] = arr[2].replaceAll(",", Global.delimiterSpace);
				arr[2] = arr[2].substring(0, arr[2].length() - 1);
				arr = arr[2].split(Global.delimiterSpace);
				idCoordWriter.write(counter, arr[1] + Global.delimiterSpace + arr[0]);
				counter++;
			}
		}
		idCoordWriter.close();
		br.close();
		
		line = Global.delimiterPound + String.valueOf(counter);
		IOUtility.setFirstLine(Global.pathOrgId2Coord, line);
		
		MLog.log("spend time: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	public static void main(String[] args) throws Exception {
//		String line = "100000,?stanbul-Adventures-Biking-Camping-Climbing-Hiking,This is a group for anyone interested in hiking, rock climbing, camping, kayaking, biking, etc. All skills levels are welcome. I started this group because to meet other outdoor enthusiasts. Looking forward to exploring the outdoors with everybody.";
//		String[] arr = splitLine(line);
//		for(int i=0; i<arr.length; i++)
//			System.out.println(arr[i]);
		
		
		String path = pathOrginalData + "act.txt";
		writeIdNameAndIdTextFiles(path);
		
		path = pathOrginalData + "latlon2.txt";
		writeIdCoordFile(path);
		
		
		System.out.println(pathOrginalData);
	}
}





















