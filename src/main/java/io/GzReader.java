package io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class GzReader {
	private GZIPInputStream gis = null;
	private BufferedReader br = null;
	
	public GzReader(String path) throws Exception{
		gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(path)));
		br = new BufferedReader(new InputStreamReader(gis));
	}
	
	public String readLine() throws Exception{
		return br.readLine();
	}
	
	public void close() throws Exception {
		gis.close();
	}
	
	public static void main(String[] args) throws Exception {
		String path = "D:\\kSTC\\Dataset\\places_dump_20110628\\places_dump_20110628\\places_dump_AE.geojson.gz";
		GzReader reader = new GzReader(path);
		System.out.println(reader.readLine());
		
	}
}
