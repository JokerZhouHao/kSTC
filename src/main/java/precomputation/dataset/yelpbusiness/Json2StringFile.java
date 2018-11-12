package precomputation.dataset.yelpbusiness;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import precomputation.dataset.file.OrginalFileWriter;
import utility.Global;
import utility.StringTools;
import utility.io.IOUtility;
import utility.io.IterableBufferReader;
import utility.io.TimeUtility;

/**
 * transfer json string to the file the format is us need
 * @author ZhouHao
 * @since 2018年10月29日
 */
public class Json2StringFile {
	
	private String pathJson = null;
	private int numLine = 0;
	
	// attributions
	private Map<String, String> attrNames = new HashMap<>();
	private List<String> recordAttrs = null;
	
	public Json2StringFile(String pathJson) {
		this.pathJson = pathJson;
		recordAttrs = new ArrayList<>();
		recordAttrs.add("address");
		recordAttrs.add("attributes");
		recordAttrs.add("categories");
		recordAttrs.add("city");
		recordAttrs.add("hours");
		recordAttrs.add("name");
		recordAttrs.add("neighborhood");
		recordAttrs.add("postal_code");
		recordAttrs.add("stars");
		recordAttrs.add("state");
	}
	
	/**
	 * get line num
	 * @return
	 * @throws Exception
	 */
	public int getNumLine() throws Exception{
		if(numLine == 0) {
			IterableBufferReader<String> ibr = IOUtility.getIBW(pathJson);
			for(String st : ibr) {
				numLine++;
			}
		}
		return numLine;
	}
	
	/**
	 * getBussinessId
	 * @param jo
	 * @return
	 */
	public String getBussinessId(JSONObject jo) {
		return jo.getString("business_id");
	}
	
	/**
	 * getLatitude
	 * @param jo
	 * @return
	 */
	public String getLatitude(JSONObject jo) {
		return jo.getString("latitude");
	}
	
	/**
	 * getLongtitude
	 * @param jo
	 * @return
	 */
	public String getLongtitude(JSONObject jo) {
		return jo.getString("longitude");
	}
	
	/**
	 * transform special string to json string
	 * eg: {'garage': False, 'street': True, 'validated': False, 'lot': True, 'valet': False} to json form
	 * @param str
	 * @return
	 */
	private String trans2Json(String str) {
		StringBuffer sb = new StringBuffer();
		char c;
		for(int i=0; i<str.length(); i++) {
			c = str.charAt(i);
			if(c != '\'') {
				if(c == ',' || c=='}') {
					sb.append('"');
					sb.append(c);
				} else {
					sb.append(c);
					if(i > 0 && c == ' ' && str.charAt(i-1) == ':') {
						sb.append('"');	
					}
				}
			} else sb.append(c);
		}
		
		return sb.toString();
	}
	
	/**
	 * get the string to attribute name is attrName 
	 * @param jo
	 * @param attrName
	 * @return
	 */
	public String getAttrStr(JSONObject jo, String attrName) {
		StringBuffer sb = new StringBuffer();
		String tempSt = null;
		tempSt = jo.getString(attrName);
		if(tempSt == null)	return "";
		else if(tempSt.equals("") || tempSt.charAt(0) != '{') {
			return tempSt;
		} else if (tempSt.charAt(tempSt.length()-1) == '}' && tempSt.charAt(tempSt.length()-2) != '"') {
			tempSt = trans2Json(tempSt);
		}
		
		JSONObject attrJson = JSON.parseObject(tempSt);
		
		String value = null;
		for(Map.Entry<String, Object> en : attrJson.entrySet()) {
			value = (String)en.getValue();
			if(!value.equals("False") && !value.equals("none") && !value.equals("no")) {
				boolean shouldRecord = Boolean.FALSE;
				if(value.charAt(value.length() - 1) == '}') {
					if(value.charAt(value.length() - 2) != '"') {
						value = trans2Json(value);
					}
					JSONObject attrSpecial = JSON.parseObject(value);
					for(Map.Entry<String, Object> en1 : attrSpecial.entrySet()) {
						tempSt = (String)en1.getValue();
						if(!tempSt.equals("False") && !tempSt.equals("none") && !tempSt.equals("no")) {
							if(null == (tempSt = attrNames.get(en1.getKey()))) {
								tempSt = StringTools.splitUpperString(en1.getKey());
								attrNames.put(en1.getKey(), tempSt);
							}
							sb.append(tempSt);
							shouldRecord = true;
						}
					}
				} else {
					if(((String)en.getValue()).contains("-")) {	// 时间字符串
						sb.append(en.getValue());
						sb.append(',');
					}
					shouldRecord = Boolean.TRUE;
				}
				if(shouldRecord) {
					if(null == (tempSt = attrNames.get(en.getKey()))) {
						tempSt = StringTools.splitUpperString(en.getKey());
						attrNames.put(en.getKey(), tempSt);
					}
					sb.append(tempSt);
				}
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * get id's text information
	 * @param jo
	 * @return
	 */
	public String getText(JSONObject jo) {
		StringBuffer sb = new StringBuffer();
		for(String attrName : recordAttrs) {
			sb.append(getAttrStr(jo, attrName));
		}
		return sb.toString();
	}
	
	/**
	 * transfer json file to files
	 * @throws Exception
	 */
	public void transToFile() throws Exception{
		System.out.println("> start transfer json file to three files . . . ");
		
		getNumLine();
		
		OrginalFileWriter idNameWriter = new OrginalFileWriter(Global.pathIdName);
		OrginalFileWriter idCoordWriter = new OrginalFileWriter(Global.pathIdCoord);
		OrginalFileWriter idTextWriter = new OrginalFileWriter(Global.pathIdText);
		idNameWriter.writeLine(Global.delimiterPound + String.valueOf(numLine));
		idCoordWriter.writeLine(Global.delimiterPound + String.valueOf(numLine));
		idTextWriter.writeLine(Global.delimiterPound + String.valueOf(numLine));
		
		JSONObject jo = null;
		int counter = 0;
		String lat=null, lon = null;
		IterableBufferReader<String> ibr = IOUtility.getIBW(this.pathJson);
		for(String line : ibr) {
			jo = JSON.parseObject(line);
			lat = this.getLatitude(jo);
			lon = this.getLongtitude(jo);
			if(lat != null && lon != null) {
				idNameWriter.write(counter, getBussinessId(jo));
				idCoordWriter.writeCoord(counter, getLongtitude(jo), getLatitude(jo));
				idTextWriter.write(counter, getText(jo));
				counter++;
			}
		}
		idNameWriter.close();
		idCoordWriter.close();
		idTextWriter.close();
		
		System.out.println("> Over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	public static void main(String[] args) throws Exception{
		String path = Global.inputPath + "yelp_academic_dataset_business.json";
//		String path = Global.inputPath + "sample.json";
//		System.out.println(new Json2StringFile(path).getNumLine());
//		IterableBufferReader<String> ibr = IOUtility.getIBW(path);
//		JSONObject jo = JSON.parseObject(ibr.readLine());
//		System.out.println(new Json2StringFile(path).getBussinessId(jo));
//		System.out.println(new Json2StringFile(path).getAttrStr(jo, "attributes"));
//		System.out.println(new Json2StringFile(path).getAttrStr(jo, "address"));
//		System.out.println(new Json2StringFile(path).getAttrStr(jo, "hours"));
//		System.out.println(new Json2StringFile(path).getAttrStr(jo, "name"));
//		System.out.println(new Json2StringFile(path).getAttrStr(jo, "postal_code"));
//		ibr.close();
		
		Json2StringFile jsf = new Json2StringFile(path);
		jsf.transToFile();
	}
}





















