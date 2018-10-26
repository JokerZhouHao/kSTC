package utility.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 
 * @author ZhouHao
 * provide some function to IO
 * 2018/10/24
 */
public class IOUtility {
	
	// Buffer
	public static BufferedReader getBR(String fp) throws Exception{
		return new BufferedReader(new FileReader(fp));
	}
	
	public static IterableBufferReader<String> getIBW(String fp) throws Exception{
		return new IterableBufferReader(new FileReader(fp));
	}
	
	public static BufferedWriter getBW(String fp) throws Exception{
		return new BufferedWriter(new FileWriter(fp));
	}
	
	public static BufferedWriter getBW(String fp, Boolean appand) throws Exception{
		return new BufferedWriter(new FileWriter(fp, appand));
	}
	
	// data
	public static DataInputStream getDis(String fp) throws Exception{
		return new DataInputStream(new BufferedInputStream(new FileInputStream(fp)));
	}
	
	public static DataOutputStream getDos(String fp) throws Exception{
		return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fp)));
	}
	
	// GZIP
	public static DataInputStream getDGZis(String fp) throws Exception{
		return new DataInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(fp))));
	}
	
	public static DataOutputStream getDGZos(String fp) throws Exception{
		return new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(fp))));
	}
	
	// ZIP64Reader
	public static ZipBase64Reader getZipBase64Reader(String fp) throws Exception{
		return new ZipBase64Reader(fp);
	}
	
	public static ZipBase64Reader getZipBase64Reader(String fp, String entryName) throws Exception{
		return new ZipBase64Reader(fp, entryName);
	}
	
	public static boolean exists(String path) {
		return new File(path).exists();
	}
	
	public static boolean existsOrThrowsException(String path) throws Exception{
		if(!exists(path)) {
			throw new Exception("path : " + path + " no exists");
		} else return true;
	}
}
