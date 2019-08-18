package precomputation.dataset.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import utility.Global;
import utility.MLog;
import utility.RandomNumGenerator;
import utility.io.TimeUtility;

/**
 * 用于创建不同规模大小数据集
 * @author ZhouHao
 * @since 2019年7月18日
 */
public class DiffScaleBuilder {
	private static String[] names = null;
	private static double[][] coords = null;
	private static double[][] coordsNormal = null;
	private static String[] texts = null;
	
	/**
	 * build sub name file
	 * @param path
	 * @param ids
	 * @throws Exception
	 */
	public static void buildingSubNameFile(String path, List<Integer> ids) throws Exception{
		if(names==null)	names = FileLoader.loadNames(Global.pathIdName);
		
		OrginalFileWriter ofw = new OrginalFileWriter(path);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(int i=0; i<ids.size(); i++) {
			ofw.write(i, names[ids.get(i)]);
		}
		ofw.close();
	}
	
	/**
	 * build sub coord file
	 * @param path
	 * @param ids
	 * @throws Exception
	 */
	public static void buildingSubCoordFile(String path, List<Integer> ids) throws Exception{
		if(coords==null)	 coords = FileLoader.loadCoords(Global.pathIdCoord);
		
		OrginalFileWriter ofw = new OrginalFileWriter(path);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(int i=0; i<ids.size(); i++) {
			ofw.writeCoord(i, String.valueOf(coords[ids.get(i)][0]), String.valueOf(coords[ids.get(i)][1]));
		}
		ofw.close();
	}
	
	
	public static void buildingSubNormalCoordFile(String path, List<Integer> ids) throws Exception{
		if(coordsNormal==null)	 coordsNormal = FileLoader.loadCoords(Global.pathIdNormCoord);
		
		OrginalFileWriter ofw = new OrginalFileWriter(path);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(int i=0; i<ids.size(); i++) {
			ofw.writeCoord(i, String.valueOf(coordsNormal[ids.get(i)][0]), String.valueOf(coordsNormal[ids.get(i)][1]));
		}
		ofw.close();
	}
	
	
	/**
	 * build sub text file
	 * @param path
	 * @param ids
	 * @throws Exception
	 */
	public static void buildSubTextFile(String path, List<Integer> ids) throws Exception{
		if(texts==null) texts = FileLoader.loadText(Global.pathIdText);
		
		OrginalFileWriter ofw = new OrginalFileWriter(path);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(int i=0; i<ids.size(); i++) {
			ofw.write(i, texts[ids.get(i)]);
		}
		ofw.close();
	}
	
	/**
	 * buildingAllFilesByIds
	 * @param ids
	 * @param suffix
	 * @throws Exception
	 */
	public static void buildingAllFilesByIds(List<Integer> ids, String suffix) throws Exception {
		if(suffix != null)	suffix = "." + suffix;
		else suffix = ".suffix";
		String pName = Global.pathIdName + suffix;
		DiffScaleBuilder.buildingSubNameFile(pName, ids);
		
		String pCoords = Global.pathIdCoord + suffix;
		DiffScaleBuilder.buildingSubCoordFile(pCoords, ids);
		
		String pCoordsNormal = Global.pathIdNormCoord + suffix;
		DiffScaleBuilder.buildingSubNormalCoordFile(pCoordsNormal, ids);
		
		String pText = Global.pathIdText + suffix;
		DiffScaleBuilder.buildSubTextFile(pText, ids);
	}
	
	
	/**
	 * 通过id构建原始文件
	 * @param ids
	 * @throws Exception
	 */
	public static void buildingAllFilesByIds(List<Integer> ids) throws Exception {
		buildingAllFilesByIds(ids, null);
	}
	
	public static void buildDiffScales(List<Integer> scales) throws Exception {
		Global.displayInputOutputPath();
		
		MLog.log("创建尺寸" + scales + "的子集  . . . ");
		Collections.sort(scales);
		names = FileLoader.loadNames(Global.pathIdName);
		List<Integer> allIds = new ArrayList<>();
		for(int i=0; i<names.length; i++)	allIds.add(i);
		List<Integer> ids = new ArrayList<>();
		
		long startTime = System.currentTimeMillis();
		int num = 0;
		for(int i=0; i<scales.size(); i++) {
			int scale = scales.get(i);
			MLog.log("创建" + scale + "规模子集  . . . ");
			long sTime = System.currentTimeMillis();
			while(num != scale) {
				int index = RandomNumGenerator.getRandomInt(allIds.size() - 1);
				ids.add(allIds.get(index));
				allIds.remove(index);
				num++;
			}
			MLog.log("number choose object: " + (new HashSet<>(ids).size()));
			buildingAllFilesByIds(ids, String.valueOf(scale));
			MLog.log("Over创建" + scale + "规模子集, 用时: " +   TimeUtility.getSpendTimeStr(sTime, System.currentTimeMillis()));
		}
		MLog.log("Over, 总用时: " + TimeUtility.getSpendTimeStr(startTime, System.currentTimeMillis()));
	}
	
	public static void main(String[] args) throws Exception {
		List<Integer> scales = new ArrayList<>();
		scales.add(500000);
		scales.add(1000000);
		scales.add(1500000);
		buildDiffScales(scales);
	}
	
}
