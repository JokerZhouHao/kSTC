package precomputation.dataset.file;

import java.io.BufferedWriter;
import java.util.Set;

import utility.Global;
import utility.io.IOUtility;

/**
 * output id_name.txt, id_coord_latlon.txt, id_text.txt
 * @author ZhouHao
 * @since 2018年10月30日
 */
public class OrginalFileWriter {
	private BufferedWriter bw = null;
	
	public OrginalFileWriter(String path) throws Exception{
		bw = IOUtility.getBW(path);
	}
	
	public void writeLine(String str) throws Exception{
		this.bw.write(str);
		this.bw.write('\n');
	}
	
	public void write(int id, String str) throws Exception{
		this.bw.write(String.valueOf(id));
		this.bw.write(Global.delimiterLevel1);
		this.bw.write(str);
		this.bw.write('\n');
	}
	
	public void writeCoord(int id, String lon, String lat) throws Exception{
		this.bw.write(String.valueOf(id));
		this.bw.write(Global.delimiterLevel1);
		this.bw.write(lon);
		this.bw.write(Global.delimiterSpace);
		this.bw.write(lat);
		this.bw.write('\n');
	}
	
	public void writeWids(int id, Set<Integer> wids) throws Exception{
		this.bw.write(String.valueOf(id));
		this.bw.write(Global.delimiterLevel1);
		Boolean sign = Boolean.TRUE;
		for(Integer ig : wids) {
			if(sign) sign = Boolean.FALSE;
			else this.bw.write(Global.delimiterLevel2);
			this.bw.write(String.valueOf(ig));
		}
		this.bw.write('\n');
	}
	
	public void close() throws Exception{
		this.bw.close();
	}
}
