package precomputation.dataset.file;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import entity.Rectangle;
import utility.Global;

/**
 * builder subset
 * @author ZhouHao
 * @since 2018年11月7日
 */
public class SubsetBuilder {
	public static void  buildingSubset(Rectangle rec) throws Exception{
		System.out.println("> start building subset in " + rec.toString());
		String[] names = FileLoader.loadNames(Global.pathIdName);
		double[][] coords = FileLoader.loadCoords(Global.pathIdCoord);
		String[] texts = FileLoader.loadText(Global.pathIdText);
		
		List<Integer> ids = new ArrayList<>();
		int i = 0;
		for(double[] da : coords) {
			if(rec.contain(da[0], da[1]))	ids.add(i);
			i++;
		}
		
		String pName = Global.pathIdName + rec.toString();
		String pCoords = Global.pathIdCoord + rec.toString();
		String pText = Global.pathIdText + rec.toString();
		
		OrginalFileWriter ofw = new OrginalFileWriter(pName);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(i=0; i<ids.size(); i++) {
			ofw.write(i, names[ids.get(i)]);
		}
		ofw.close();
		
		ofw = new OrginalFileWriter(pCoords);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(i=0; i<ids.size(); i++) {
			ofw.writeCoord(i, String.valueOf(coords[ids.get(i)][0]), String.valueOf(coords[ids.get(i)][1]));
		}
		ofw.close();
		
		ofw = new OrginalFileWriter(pText);
		ofw.writeLine(Global.delimiterPound + String.valueOf(ids.size()));
		for(i=0; i<ids.size(); i++) {
			ofw.write(i, texts[ids.get(i)]);
		}
		ofw.close();
		System.out.println(">Over");
	}
	
	public static void main(String[] args) throws Exception{
		SubsetBuilder.buildingSubset(new Rectangle(-125, 28, 15, 60));
	}
}
