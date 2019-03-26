package precomputation.dataset.file;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import entity.Rectangle;
import utility.Global;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * builder subset
 * @author ZhouHao
 * @since 2018年11月7日
 */
public class SubsetBuilder {
	
	/**
	 * build sub name file
	 * @param path
	 * @param ids
	 * @throws Exception
	 */
	public static void buildingSubNameFile(String path, List<Integer> ids) throws Exception{
		String[] names = FileLoader.loadNames(Global.pathIdName);
		
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
		double[][] coords = FileLoader.loadCoords(Global.pathIdCoord);
		
		OrginalFileWriter ofw = new OrginalFileWriter(path);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(int i=0; i<ids.size(); i++) {
			ofw.writeCoord(i, String.valueOf(coords[ids.get(i)][0]), String.valueOf(coords[ids.get(i)][1]));
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
		String[] texts = FileLoader.loadText(Global.pathIdText);
		
		OrginalFileWriter ofw = new OrginalFileWriter(path);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(int i=0; i<ids.size(); i++) {
			ofw.write(i, texts[ids.get(i)]);
		}
		ofw.close();
	}
	
	/**
	 * build sub id wids file
	 * @param path
	 * @param ids
	 * @throws Exception
	 */
	public static void buildSubIdWidsFile(String path, List<Integer> ids) throws Exception{
		Set<Integer>[] allWids = FileLoader.loadIdWids(Global.pathIdWids);
		
		OrginalFileWriter ofw = new OrginalFileWriter(path);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(int i=0; i<ids.size(); i++) {
			ofw.writeWids(i, allWids[ids.get(i)]);
		}
		ofw.close();
	}
	
	/**
	 * building subset by rectangle
	 * @param rec
	 * @throws Exception
	 */
	public static void  buildingSubset(Rectangle rec) throws Exception{
		System.out.println("> start building subset in " + rec.toString());
		double[][] coords = FileLoader.loadCoords(Global.pathIdCoord);
		
		List<Integer> ids = new ArrayList<>();
		int i = 0;
		for(double[] da : coords) {
			if(rec.contain(da[0], da[1]))	ids.add(i);
			i++;
		}
		
		String pName = Global.pathIdName + rec.toString();
		SubsetBuilder.buildingSubNameFile(pName, ids);
		
		String pCoords = Global.pathIdCoord + rec.toString();
		SubsetBuilder.buildingSubCoordFile(pCoords, ids);
		
		String pText = Global.pathIdText + rec.toString();
		SubsetBuilder.buildSubTextFile(pText, ids);
		
//		String pIdWids = Global.pathIdWids + rec.toString();
//		SubsetBuilder.buildSubIdWidsFile(pIdWids, ids);
		
		System.out.println("> Over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	public static void main(String[] args) throws Exception{
		TimeUtility.init();
		SubsetBuilder.buildingSubset(new Rectangle(-112.41,33.46, -111.90,33.68));
	}
}
