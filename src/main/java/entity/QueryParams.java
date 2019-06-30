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
	public double alpha = 0.5;
	public double steepDegree = 0.1;
	public double steepOppositeDegree = 0.9;
	public int h = 6;
	public int zorderWidth = 1000;
	public int zorderHeight = 1000;
	
	public int opticMinpts = 1;
	public double opticEpsilon = 0.001;
	
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
	
	private final static String head = "rFanout  alpha  steepD  h   opMpts  opEps    ns   " + 
			"t   k     nw  mpts   eps       xi       maxPNeiByte   ";
	private final static String formatStr = "%-9s%-7s%-8s%-4s%-8s%-9s%-5s" +
			   "%-4s%-6s%-4s%-7s%-10s%-9s%-14s";
	
	public QueryParams() {}
	
	public QueryParams(double epsilonOrXi, int minpts) {
		this.epsilon = epsilonOrXi;
		this.xi = epsilonOrXi;
		this.minpts = minpts;
	}
	
	public QueryParams(int rtreeFanout, double alpha, double steepDegree, int h, int opticMinpts, double opticEpsilon,
			int numSample, int type,
			int k, int numWord, int minpts, double epsilon, double xi, int maxPidNeighborsBytes) {
		super();
		this.rtreeFanout = rtreeFanout;
		this.alpha = alpha;
		this.steepDegree = steepDegree;
		this.steepOppositeDegree = 1 - steepDegree;
		this.h = h;
		this.zorderWidth = (int)Math.pow(2, h);
		this.zorderHeight = this.zorderWidth;
		this.opticMinpts = opticMinpts;
		this.opticEpsilon = opticEpsilon;
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
			System.out.println(String.format(formatStr, qp.rtreeFanout, qp.alpha, qp.steepDegree,
					qp.h, qp.opticMinpts, qp.opticEpsilon, qp.numSample,
					qp.type, qp.k, qp.numWord, qp.minpts,
					qp.epsilon, qp.xi, qp.maxPidNeighborsBytes));
		}
		System.out.println();
	}
	
	public static void display(QueryParams qp) {
		System.out.println("Global.rtreeFanout: " + Global.rtreeFanout + "          # 只能通过改config.props文件，来选择rtree");
		System.out.println(head);
		System.out.println(String.format(formatStr, qp.rtreeFanout, qp.alpha, qp.steepDegree,
				qp.h, qp.opticMinpts, qp.opticEpsilon, qp.numSample,
				qp.type, qp.k, qp.numWord, qp.minpts,
				qp.epsilon, qp.xi, qp.maxPidNeighborsBytes));
		System.out.println();
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
			qp = new QueryParams(Integer.parseInt(arr[0]), Double.parseDouble(arr[1]), Double.parseDouble(arr[2]), 
					Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), Double.parseDouble(arr[5]),
					Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), 
					Integer.parseInt(arr[8]), Integer.parseInt(arr[9]), Integer.parseInt(arr[10]), 
					Double.parseDouble(arr[11]), Double.parseDouble(arr[12]),
					Integer.parseInt(arr[13]));
			qps.add(qp);
		}
		br.close();
		if(qps.isEmpty())	return null;
		else return qps;
	}
	
	public static String generateTestQuery(int rtreeFanout, double alpha, double steepDegree, int h, 
			int opticMinpts, double  opticEpsilon,
			int numSample, int type,
			int k, int numWord, int minpts, double epsilon, double xi, int maxPidNeighborsBytes) {
		return String.format("%s %s %s %s %s %s %s %s %s %s %s %s %s %s", rtreeFanout, alpha, steepDegree, h, opticMinpts, opticEpsilon, 
							numSample, type, k, numWord, minpts, epsilon, xi, maxPidNeighborsBytes);
	}
	
	@Override
	public String toString() {
		return String.format("rFanout=%s.alpha=%s.steepD=%s.h=%s.om=%s.oe=%s.ns=%s.t=%s.k=%s.nw=%s.mpts=%s.eps=%s.xi=%s.maxPNeiByte=%s",
				rtreeFanout, alpha, steepDegree,
				h, opticMinpts, opticEpsilon,
				numSample,
				type, k, numWord, minpts,
				epsilon, xi, maxPidNeighborsBytes);
	}
	
	
	public static void main(String[] args) throws Exception{
		List<QueryParams> qps = new ArrayList<>();
		QueryParams qp = new QueryParams(100, 0.3, 0.2, 2, 3, 0.0001, 200, -1, 5, 3, 20, 0.1, 0.01, 50000000);
//		qps.add(qp);
//		qps.add(qp);
//		QueryParams.displays(qps);
//		System.out.println();
//		QueryParams.display(qp);
//		System.out.println();
		
		String path = Global.pathResult + "qptest.txt";
		qps = QueryParams.load(path);
		QueryParams.displays(qps);
		System.out.println();
//		
		System.out.println(QueryParams.resFileName(qps.get(0)));
		System.out.println(qps.get(0));
		
	}
	
}