package precomputation.dataset.placedump20110628;

import java.util.Map.Entry;
import java.util.zip.ZipEntry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.GzReader;
import io.ZipGzReader;
import precomputation.dataset.file.OrginalFileWriter;
import utility.Global;
import utility.MLog;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * 读取文件places_dump_20110628.zip中的数据，然后取出相应字段，生成文件
 * id_coord_longtitude_latitude.txt，id_name.txt，id_text.txt
 * @author ZhouHao
 * @since 2019年7月4日
 */
public class Zip2StringFile {
	private ZipGzReader zgReader = null;
	private int indexCurEntry = 1;
	private ZipEntry curEntry = null;
	private GzReader gReader = null;
	
	public Zip2StringFile(String path) throws Exception {
		zgReader = new ZipGzReader(path);
	}
	
	/**
	 * readLine
	 * @return
	 * @throws Exception
	 */
	public String readLine() throws Exception {
		String line = null;
		while(curEntry == null || gReader == null || (line = gReader.readLine()) == null) {
			curEntry = zgReader.changeToNextZipEntry();
			if(curEntry == null)	return null;
			if(!curEntry.getName().endsWith("gz"))	continue;
			MLog.log((indexCurEntry++) + ": " + curEntry.getName());
			gReader = new GzReader(zgReader.inputStream());
			line = gReader.readLine();
			break;
		}
		return line;
	}
	
	public void close() throws Exception {
		zgReader.close();
	}
	
	public String getCity(JSONObject jo) {
		return ((JSONObject)jo.get("properties")).getString("city");
	}
	
	public String getCountry(JSONObject jo) {
		return ((JSONObject)jo.get("properties")).getString("country");
	}
	
	public String getTags(JSONObject jo) {
		StringBuffer sb = new StringBuffer();
		JSONObject jo1 = (JSONObject)jo.get("properties");
		JSONArray jArr = (JSONArray)jo1.get("tags");
		if(null == jArr)	return null;
		for(Object obj : jArr) {
			sb.append(obj.toString());
			sb.append(' ');
		}
		return sb.toString();
	}
	
	public String getClassifiers(JSONObject jo) {
		StringBuffer sb = new StringBuffer();
		JSONArray arr = (JSONArray)((JSONObject)jo.get("properties")).get("classifiers");
		if(null == arr || arr.isEmpty())	return null;
		
		jo = arr.getJSONObject(0);
		for(Entry<String, Object> en : jo.entrySet()) {
			sb.append(en.getValue());
			sb.append(' ');
		}
		return sb.toString();
	}
	
	public String getText(JSONObject jo) {
		StringBuffer sb = new StringBuffer();
		String str = null;
		str = getCity(jo);
		if(null != str)	{
			sb.append(str);
			sb.append(' ');
		}
		
		str = getCountry(jo);
		if(null != str)	{
			sb.append(str);
			sb.append(' ');
		}
		
		str = getTags(jo);
		if(null != str)	{
			sb.append(str);
			sb.append(' ');
		}
		
		str = getClassifiers(jo);
		if(null != str)	{
			sb.append(str);
			sb.append(' ');
		}
		
		return sb.toString();
	}
	
	public String getId(JSONObject jo) {
		return jo.getString("id");
	}
	
	public String getLonLat(JSONObject jo) {
		StringBuffer sb = new StringBuffer();
		JSONArray arr = jo.getJSONObject("geometry").getJSONArray("coordinates");
		sb.append(arr.get(0).toString());
		sb.append(' ');
		sb.append(arr.get(1).toString());
		
		return sb.toString();
	}
	
	/**
	 * transToFile
	 * @throws Exception
	 */
	public void transToFile() throws Exception {
		Global.displayInputOutputPath();
		
		long startTime = System.currentTimeMillis();
		MLog.log("start transfer json file to three files . . .");
		
		OrginalFileWriter idNameWriter = new OrginalFileWriter(Global.pathOrgId2Name);
		OrginalFileWriter idCoordWriter = new OrginalFileWriter(Global.pathOrgId2Coord);
		OrginalFileWriter idTextWriter = new OrginalFileWriter(Global.pathOrgId2Text);
		idNameWriter.writeLine("               ");
		idCoordWriter.writeLine("               ");
		idTextWriter.writeLine("               ");
		
		String line = null;
		int counter = 0;
		while(null != (line = readLine())) {
			JSONObject jo = JSON.parseObject(line);
			idNameWriter.write(counter, getId(jo));
			idCoordWriter.write(counter, getLonLat(jo));
			idTextWriter.write(counter, getText(jo).replaceAll("\n", " "));
			counter++;
		}
		idNameWriter.close();
		idCoordWriter.close();
		idTextWriter.close();
		
		line = Global.delimiterPound + String.valueOf(counter);
		IOUtility.setFirstLine(Global.pathOrgId2Name, line);
		IOUtility.setFirstLine(Global.pathOrgId2Coord, line);
		IOUtility.setFirstLine(Global.pathOrgId2Text, line);
		
		
//		line = readLine();
//		JSONObject jo = JSON.parseObject(line);
//		System.out.println(getCity(jo));
//		System.out.println(getCountry(jo));
//		System.out.println(getTags(jo));
//		System.out.println(getClassifiers(jo));
//		System.out.println(getText(jo));
//		System.out.println(getId(jo));
//		System.out.println(getLonLat(jo));
		
		
		MLog.log("spend time: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	
	public static void main(String[] args) throws Exception {
		String path = "D:\\kSTC\\Dataset\\places_dump_20110628\\places_dump_20110628.zip";
		Zip2StringFile tofile = new Zip2StringFile(path);
		tofile.transToFile();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
