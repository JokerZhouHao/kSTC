package io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public class TarReader {
	private String path = null;
	private String entryName = null;
	public final static String encode = "UTF8";
	
	private BufferedReader reader = null;
	
	public TarReader(String path) {
		this.path = path;
	}
	
	public TarReader(String path, String entryName) throws Exception {
		this.path = path;
		this.entryName = entryName;
	}
	
	private void init() throws Exception {
//		GZIPInputStream gis = new GZIPInputStream(new FileInputStream(path));
		FileInputStream gis = new FileInputStream(path);
		ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream("tar", gis);
		TarArchiveEntry arch = null;
		while(null != (arch = (TarArchiveEntry)ais.getNextEntry())) {
			String name = arch.getName();
			if(name.contains(entryName))	break;
		}
		if(arch == null) {
			gis.close();
			throw new Exception("未找到entry " + entryName);
		}
		reader = new BufferedReader(new InputStreamReader(gis, encode));
	}
	
	public String readLine() throws Exception{
		if(null == reader)	init();
		return reader.readLine();
	}
	
	public void close() throws Exception {
		if(null != reader)	reader.close();
	}
	
	public void displayAllEntryName() throws Exception{
		FileInputStream gis = new FileInputStream(path);
		ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream("tar", gis);
		TarArchiveEntry arch = null;
		while(null != (arch = (TarArchiveEntry)ais.getNextEntry())) {
			System.out.println(arch.getName());
		}
		gis.close();
	}
	
	public static void displayAllEntryName(String path) throws Exception {
		new TarReader(path).displayAllEntryName();
	}
	
	public static void showNLine(String path, String entryName, int numLine) throws Exception {
		System.out.println("**********************  " + entryName + "  ***********************");
		TarReader reader = new TarReader(path, entryName);
		for(int i=0; i < numLine; i++) {
			System.out.println(reader.readLine());
		}
		reader.close();
		System.out.println();
	}
	
	
	public static void main(String[] args) throws Exception {
		/**********   E:\mask\kSTC\DataSet\yelp_dataset\yelp_dataset.tar  ******/
		String path = "E:\\mask\\kSTC\\DataSet\\yelp_dataset\\yelp_dataset.tar";
		String entryBusiness = "yelp_academic_dataset_business.json";
		String entryCheckin = "yelp_academic_dataset_checkin.json";
		String entryPhoto = "yelp_academic_dataset_photo.json";
		String entryReview = "yelp_academic_dataset_review.json";
		String entryTip = "yelp_academic_dataset_tip.json";
		String entryUser = "yelp_academic_dataset_user.json";
		
		showNLine(path, entryBusiness, 1);
		showNLine(path, entryCheckin, 1);
		showNLine(path, entryPhoto, 1);
		showNLine(path, entryReview, 1);
		showNLine(path, entryTip, 1);
		showNLine(path, entryUser, 1);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
