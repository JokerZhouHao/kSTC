package io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;


public class ZipBase64Reader {
	
	private BufferedReader bufferedReader = null;
	private ZipArchiveInputStream zais = null;
	private ZipEntry curZipEntry = null;
	private String entryName = null;
	private String filePath = null;
	private ZipFile zipFile = null;
	
	public ZipBase64Reader() {}
	
	public ZipBase64Reader(String filePath) {
		this.filePath = filePath;
		init();
	}
	
	public ZipBase64Reader(String filePath, String entryName) {
		this.filePath = filePath;
		this.entryName = entryName;
		init();
	}
	
	public void init() {
		try {
			if(null != entryName) {
				zipFile = new ZipFile(new File(filePath));
				Enumeration<ZipArchiveEntry> enu = zipFile.getEntries();
				ZipArchiveEntry zae = null;
				boolean sign = false;
				while(enu.hasMoreElements()) {
					zae = enu.nextElement();
//					System.out.println(zae.getName() + "   " + entryName);
					if(zae.getName().contains(entryName)) {
						sign = true;
						break;
					}
				}
				if(sign) {
					bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(zipFile.getInputStream(zae))));
				} else {
					System.out.println("压缩包" + filePath + "不包含文件" + entryName);
				}
				return;
			}
			zais = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(new File(filePath))));;
			curZipEntry = zais.getNextZipEntry();
			bufferedReader = new BufferedReader(new InputStreamReader(zais));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getCurZipEntryName() {
		return curZipEntry.getName();
	}
	
	public BufferedReader getCurBufferedReader() {
		return bufferedReader;
	}
	
	public ZipEntry changeToNextZipEntry() {
		try {
			curZipEntry = zais.getNextZipEntry();
			if(null==curZipEntry)	return null;
			bufferedReader = new BufferedReader(new InputStreamReader(zais));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curZipEntry;
	}
	
	public String readLine() {
		try {
			return bufferedReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Boolean close() {
		try {
			if(null != bufferedReader)	bufferedReader.close();
			if(null != zais)	zais.close();
			if(null != zipFile)	zipFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public static void main(String[] args) throws Exception{
		
		String path = "E:\\mask\\kSTC\\DataSet\\yelp_dataset\\yelp_dataset.tar.gz";
		ZipBase64Reader rs = new ZipBase64Reader(path, "yelp_academic_dataset_business.json");
		System.out.println(rs.readLine());
//		System.out.println(rs.readLine());
//		System.out.println(rs.readLine());
//		System.out.println(rs.readLine());
//		System.out.println(rs.readLine());
//		System.out.println(rs.readLine());
		rs.close();
		
	}
}
