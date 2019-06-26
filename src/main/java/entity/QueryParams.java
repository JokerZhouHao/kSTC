package entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algorithm.AlgEucDisAdvancedOpticsWu;
import algorithm.AlgEucDisBase;
import algorithm.AlgEucDisBaseOpticsWu;
import algorithm.AlgEucDisFastRange;
import services.RunTimeRecordor;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.IOUtility;
import utility.io.LuceneUtility;

public class QueryParams {
	
	public int rtreeFanout = 50;
	public double steepDegree = 0.1;
	public double steepOppositeDegree = 0.9;
	public int zorderWidth = 1000;
	public int zorderHeight = 1000;
	
	public int numSample = 0;
	
	/**
	 * 1	AlgEucDisBase
	 * 2	AlgEucDisFastRange
	 * 11	AlgEucDisBaseOpticsWu
	 * 12	AlgEucDisAdvancedOpticsWu
	 */
	public int type = 0;
	
	public int k = 0;
	public int numWord = 0;
	
	public Point location = null;
	public int minpts = 0;
	public double epsilon = 0;
	public double xi = 0;
	public List<String> sWords = new ArrayList<>();
	
	public SGPLInfo sgplInfo = null;
	
	public int maxPidNeighborsBytes = 0;
	
	public RunTimeRecordor runTimeRec = new RunTimeRecordor();
	
	private final static String head = "rFanout steepD steepOD zw    zh    ns   " + 
			"t  k     nw  mpts   eps       xi       maxPNeiByte   ";
	private final static String formatStr = "%-8d%-7s%-8s%-6d%-6d%-5d" +
			   "%-3d%-6d%-4d%-7d%-10s%-9s%-14d";
	
	public QueryParams() {}
	
	public QueryParams(double epsilonOrXi, int minpts) {
		this.epsilon = epsilonOrXi;
		this.xi = epsilonOrXi;
		this.minpts = minpts;
	}
	
	public QueryParams(int rtreeFanout, double steepDegree, int zorderWidth, int zorderHeight, 
			int numSample, int type,
			int k, int numWord, int minpts, double epsilon, double xi, int maxPidNeighborsBytes) {
		super();
		this.rtreeFanout = rtreeFanout;
		this.steepDegree = steepDegree;
		this.steepOppositeDegree = 1 - steepDegree;
		this.zorderWidth = zorderWidth;
		this.zorderHeight = zorderHeight;
		this.numSample = numSample;
		this.type = type;
		this.k = k;
		this.numWord = numWord;
		this.minpts = minpts;
		this.epsilon = epsilon;
		this.xi = xi;
		this.maxPidNeighborsBytes = maxPidNeighborsBytes;
		
		double i = 1.0;
		sgplInfo = new SGPLInfo(zorderWidth, zorderHeight, 0, i/zorderWidth, 
							0, i/zorderHeight, zorderWidth * zorderHeight);
	}
	
	
	public AlgType algType() {
		switch (type) {
			case 1:
				return AlgType.AlgEucDisBase;
			case 2:
				return AlgType.AlgEucDisFastRange;
			case 11:
				return AlgType.AlgEucDisBaseOpticsWu;
			case 12:
				return AlgType.AlgEucDisAdvancedOpticsWu;
			default:
				break;
		}
		return AlgType.AlgEucDisBase;
	}
	
	
	public void setCoordAndSWords(Point location, List<String> sWords) {
		this.location = location;
		this.sWords = sWords;
	}
	
	public static void displays(List<QueryParams> qps) {
		System.out.println("Global.rtreeFanout: " + Global.rtreeFanout + "          # 只能通过改config.props文件，来选择rtree");
		System.out.println(head);
		for(QueryParams qp : qps) {
			System.out.println(String.format(formatStr, qp.rtreeFanout, qp.steepDegree,
					qp.steepOppositeDegree, qp.zorderWidth, qp.zorderHeight, qp.numSample,
					qp.type, qp.k, qp.numWord, qp.minpts,
					qp.epsilon, qp.xi, qp.maxPidNeighborsBytes));
		}
	}
	
	public static void display(QueryParams qp) {
		System.out.println("Global.rtreeFanout: " + Global.rtreeFanout + "          # 只能通过改config.props文件，来选择rtree");
		System.out.println(head);
		System.out.println(String.format(formatStr, qp.rtreeFanout, qp.steepDegree,
				qp.steepOppositeDegree, qp.zorderWidth, qp.zorderHeight, qp.numSample,
				qp.type, qp.k, qp.numWord, qp.minpts,
				qp.epsilon, qp.xi, qp.maxPidNeighborsBytes));
	}
	
	public static String resFileName(QueryParams qp) {
		return qp.toString() + ".csv";
	}
	
	public static List<QueryParams> load(String path) throws Exception {
		List<QueryParams> qps = new ArrayList<>();
		QueryParams qp = null;
		BufferedReader br = IOUtility.getBR(path);
		String line = null;
		String arr[] = null;
		while(null != (line = br.readLine()) && !line.startsWith("--")) {
			if(line.startsWith("#") || line.trim().equals(""))	continue;
			arr = line.split(Global.delimiterSpace);
			qp = new QueryParams(Integer.parseInt(arr[0]), Double.parseDouble(arr[1]), Integer.parseInt(arr[2]), 
					Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), Integer.parseInt(arr[5]), 
					Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Integer.parseInt(arr[8]), 
					Double.parseDouble(arr[9]), Double.parseDouble(arr[10]),
					Integer.parseInt(arr[11]));
			qps.add(qp);
		}
		br.close();
		if(qps.isEmpty())	return null;
		else return qps;
	}
	
	public static String generateTestQuery(int rtreeFanout, double steepDegree, int zorderWidth, int zorderHeight, int numSample, int type,
			int k, int numWord, int minpts, double epsilon, double xi, int maxPidNeighborsBytes) {
		return String.format("%s %s %s %s %s %s %s %s %s %s %s %s", rtreeFanout, steepDegree, zorderWidth,
							zorderHeight, numSample, type, k, numWord, minpts, epsilon, xi, maxPidNeighborsBytes);
	}
	
	@Override
	public String toString() {
		return String.format("rFanout=%s.steepD=%s.steepOD=%s.zw=%s.zh=%s.ns=%s.t=%s.k=%s.nw=%s.mpts=%s.eps=%s.xi=%s.maxPNeiByte=%s",
				rtreeFanout, steepDegree,
				steepOppositeDegree, zorderWidth, zorderHeight, numSample,
				type, k, numWord, minpts,
				epsilon, xi, maxPidNeighborsBytes);
	}
	
	
	public static void main(String[] args) throws Exception{
		List<QueryParams> qps = new ArrayList<>();
		QueryParams qp = new QueryParams(100, 0.3, 20, 30, 200, -1, 5, 3, 20, 0.1, 0.01, 50000000);
		qps.add(qp);
		qps.add(qp);
		QueryParams.displays(qps);
		System.out.println();
		QueryParams.display(qp);
		System.out.println();
		
		String path = Global.pathResult + "qptest.txt";
		qps = QueryParams.load(path);
		QueryParams.displays(qps);
		System.out.println();
		
		System.out.println(QueryParams.resFileName(qp));
		System.out.println(qp);
		
	}
	
}