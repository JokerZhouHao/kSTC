package precomputation.dataset.file;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entity.MCoord;
import entity.Rectangle;
import utility.Global;
import utility.io.IOUtility;
import utility.io.LuceneUtility;
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
		String[] names = FileLoader.loadNames(Global.pathOrgId2Name);
		
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
		double[][] coords = FileLoader.loadCoords(Global.pathOrgId2Coord);
		
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
		String[] texts = FileLoader.loadText(Global.pathOrgId2Text);
		
		OrginalFileWriter ofw = new OrginalFileWriter(path);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(int i=0; i<ids.size(); i++) {
			ofw.write(i, texts[ids.get(i)]);
		}
		ofw.close();
	}
	
	public static void buildSubTextFile(String path, List<Integer> ids, String texts[]) throws Exception{
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
	 * 通过id构建原始文件
	 * @param ids
	 * @throws Exception
	 */
	public static void buildingAllFilesByIds(List<Integer> ids) throws Exception {
		String pName = Global.pathIdName;
		SubsetBuilder.buildingSubNameFile(pName, ids);
		
		String pCoords = Global.pathIdCoord;
		SubsetBuilder.buildingSubCoordFile(pCoords, ids);
		
		String pText = Global.pathIdText;
		SubsetBuilder.buildSubTextFile(pText, ids);
	}
	
	/**
	 * building subset by rectangle
	 * @param rec
	 * @throws Exception
	 */
	public static void  buildingSubset(Rectangle rec) throws Exception{
		TimeUtility.init();
		System.out.println("> start building subset in " + rec.toString());
		double[][] coords = FileLoader.loadCoords(Global.pathOrgId2Coord);
		
		List<Integer> ids = new ArrayList<>();
		int i = 0;
		for(double[] da : coords) {
			if(rec.contain(da[0], da[1]))	ids.add(i);
			i++;
		}
		
		buildingAllFilesByIds(ids);
		
		System.out.println("> Over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	/**
	 * 构建没有重复坐标对象的数据集
	 * @throws Exception
	 */
	public static void buildingNoRepeatCoordSubset() throws Exception {
		TimeUtility.init();
		System.out.println("> start buildingNoRepeatCoordSubset . . . ");
		
		double[][] coords = FileLoader.loadCoords(Global.pathOrgId2Coord);
		
		List<Integer> ids = new ArrayList<>();
		Set<MCoord> rec = new HashSet<>();
		int i = 0;
		for(double[] da : coords) {
			MCoord crd = new MCoord(da);
			if(!rec.contains(crd)) {
				rec.add(crd);
				ids.add(i);
			}
			i++;
		}
		
		System.out.println("NumOrginal: " + coords.length);
		System.out.println("NumNoRepeatCoord: " + ids.size());
		
		String pName = Global.pathOrgId2Name + ".NoRepeatCoord";
		SubsetBuilder.buildingSubNameFile(pName, ids);
		
		String pCoords = Global.pathOrgId2Coord + ".NoRepeatCoord";
		SubsetBuilder.buildingSubCoordFile(pCoords, ids);
		
		String pText = Global.pathOrgId2Text + ".NoRepeatCoord";
		SubsetBuilder.buildSubTextFile(pText, ids);
		
		System.out.println("> Over, spend time : " + TimeUtility.getGlobalSpendTime());
		
	}
	
	/**
	 * 构建不含有某些词的数据集
	 * @throws Exception
	 */
	public static void buildingFilterTermsSubset(List<String> termFilter, String suffix) throws Exception {
		TimeUtility.init();
		System.out.println("> start buildingFilterTermsSubset . . . ");
		
		String[] txts = FileLoader.loadText(Global.pathOrgId2Text);
		List<Integer> ids = new ArrayList<>();
		String txt = null;
		for(int id = 0; id < txts.length; id++) {
			txt = txts[id];
			for(String st : termFilter) {
				txt = txt.replaceAll(st, "");
			}
			if(LuceneUtility.getTerms(txt) != null) {
				txts[id] = txt;
				ids.add(id);
			} else txts[id] = null;
		}
		
		System.out.println("NumOrginal: " + txts.length);
		System.out.println("NumNoRepeatCoord: " + ids.size());
		
		String pName = Global.pathOrgId2Name + suffix;
		SubsetBuilder.buildingSubNameFile(pName, ids);
		
		String pCoords = Global.pathOrgId2Coord  + suffix;
		SubsetBuilder.buildingSubCoordFile(pCoords, ids);
		
		String pText = Global.pathOrgId2Text + suffix;
		SubsetBuilder.buildSubTextFile(pText, ids, txts);
		
		System.out.println("> Over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	
	public static void main(String[] args) throws Exception{
		TimeUtility.init();
//		SubsetBuilder.buildingSubset(new Rectangle(-112.41,33.46, -111.90,33.68));
		SubsetBuilder.buildingNoRepeatCoordSubset();
	}
}
