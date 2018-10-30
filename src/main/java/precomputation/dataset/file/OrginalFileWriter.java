package precomputation.dataset.file;

import java.io.BufferedWriter;

import utility.Global;
import utility.io.IOUtility;

/**
 * output id_name.txt, id_coord_latlon.txt, id_text.txt
 * @author ZhouHao
 * @since 2018年10月30日
 */
public class OrginalFileWriter {
	private BufferedWriter bw = null;
	
	public OrginalFileWriter(FileName fileName) throws Exception{
		switch (fileName) {
		case ID_NAME:
			bw = IOUtility.getBW(Global.inputPath + "id_name.txt");
			break;
		case ID_COORD_LATLON:
			bw = IOUtility.getBW(Global.inputPath + "id_coord_latlon.txt");
			break;
		case ID_TEXT:
			bw = IOUtility.getBW(Global.inputPath + "id_text.txt");
			break;
		default:
			break;
		}
	}
	
	public void write(String str) throws Exception{
		this.bw.write(str);
		this.bw.write('\n');
	}
	
	public void write(int id, String str) throws Exception{
		this.bw.write(String.valueOf(id));
		this.bw.write(Global.delimiterLevel1);
		this.bw.write(str);
		this.bw.write('\n');
	}
	
	public void writeCoord(int id, String lat, String lon) throws Exception{
		this.bw.write(String.valueOf(id));
		this.bw.write(lat);
		this.bw.write(Global.delimiterSpace);
		this.bw.write(lon);
		this.bw.write('\n');
	}
	
	public void close() throws Exception{
		this.bw.close();
	}
}
