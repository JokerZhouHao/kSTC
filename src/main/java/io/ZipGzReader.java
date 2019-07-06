package io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;

/**
 * 用于读取文件places_dump_20110628.zip里面的所有gz文件
 * 
 * @author ZhouHao
 * @since 2019年7月4日
 */
public class ZipGzReader {
	private BufferedReader bufferedReader = null;
	private ZipArchiveInputStream zais = null;
	private ZipEntry curZipEntry = null;
	private String entryName = null;
	private String filePath = null;
	private ZipFile zipFile = null;
	
	public ZipGzReader(String filePath) throws Exception{
		this.filePath = filePath;
		zais = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(new File(filePath))));
	}
	
	public ZipEntry changeToNextZipEntry() throws Exception{
		curZipEntry = zais.getNextZipEntry();
		if(null==curZipEntry)	return null;
		return curZipEntry;
	}
	
	public void displayAllEntrys() throws Exception {
		while(null != (curZipEntry = zais.getNextZipEntry())) {
			System.out.println(curZipEntry.getName());
		}
	}
	
	public void close() throws Exception {
		zais.close();
	}
	
	public InputStream inputStream() {
		return zais;
	}
	
	public static void main(String[] args) throws Exception {
		String path = "D:\\kSTC\\Dataset\\places_dump_20110628\\places_dump_20110628.zip";
		ZipGzReader reader = new ZipGzReader(path);
//		reader.displayAllEntrys();
		int num = 0;
		ZipEntry curEntry = null;
		while(null != (curEntry = reader.changeToNextZipEntry())){
			if(!curEntry.getName().endsWith("gz"))	continue;
			System.out.println(curEntry.getName());
			GzReader gReader = new GzReader(reader.inputStream());
			System.out.println(gReader.readLine() + "\n");
			num++;
		}
		System.out.println(num);
		
		reader.close();
	}
}






















