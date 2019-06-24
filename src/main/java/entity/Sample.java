package entity;

import java.util.ArrayList;
import java.util.List;

import spatialindex.spatialindex.Point;
import utility.Global;

public class Sample {
	public String line = null;
	public int id = 0;
	public int idOrg = 0;
	public Point coords = null;
	public List<String> sWords = null;
	
	public Sample(String line) throws Exception{
		this.line = line;
		format(line);
	}
	
	private void format(String line) {
		String[] arr = line.split(Global.delimiterLevel1);
		id = Integer.parseInt(arr[0]);
		idOrg = Integer.parseInt(arr[1]);
		
		arr = arr[2].split(Global.delimiterSpace);
		double[] cs = new double[2];
		cs[0] = Double.parseDouble(arr[0]);
		cs[1] = Double.parseDouble(arr[1]);
		coords = new Point(cs);
		
		sWords = new ArrayList<>();
		for(int i = 2; i < arr.length; i++) {
			if(arr[i].trim().isEmpty())	continue;
			sWords.add(arr[i]);
		}
	}
	
	public String toString() {
		return line;
	}
}