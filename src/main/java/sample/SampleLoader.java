package sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import entity.QueryParams;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.IOUtility;

/**
 * 样本读取器
 * @author ZhouHao
 * @since 2019年4月23日
 */
public class SampleLoader {
	/**
	 * 查询样本文件格式如下：
	 * #格式说明部分
	 * ##样本数
	 * 样本编号(重0开始): k;经度 纬度;minpts;epsilon;xi;词1#词2#词3#词4
	 */
	public static final String delimiter1 = ": ";
	public static final String delimiter2 = ";";
	public static final String delimiter3 = "#";
	
	private static final String annotation = "#样本编号(从1开始): k;经度 纬度;minpts;epsilon;xi;词1#词2#词3#词4";
	
	public static List<QueryParams> load(String path, int k, int minpts, double epsilon, double xi) throws Exception{
		List<QueryParams> samples = null;
		BufferedReader br = IOUtility.getBR(path);
		String line = null;
		String[] arr = null;
		while(null != (line = br.readLine())) {
			if(line.startsWith("#"))	continue;
			else {
				QueryParams qp = new QueryParams();
				line = line.split(delimiter1)[1];
				arr = line.split(delimiter2);
				
				if(k == Integer.MAX_VALUE)	qp.k = Integer.parseInt(arr[0]);
				else qp.k = k;
				
				double[] loca = {0.7, 0.7};
				loca[0] = Double.parseDouble(arr[1].split(" ")[0]);
				loca[1] = Double.parseDouble(arr[1].split(" ")[1]);
				qp.location = new Point(loca);
				
				if(minpts == Integer.MAX_VALUE)	qp.minpts = Integer.parseInt(arr[2]);
				else qp.minpts = minpts;
				
				if(epsilon == Double.MAX_VALUE) qp.epsilon = Double.parseDouble(arr[3]);
				else qp.epsilon = epsilon;
				
				if(xi == Double.MAX_VALUE) qp.xi = Double.parseDouble(arr[4]);
				else qp.xi = xi;
				
				arr = arr[5].split(delimiter3);
				List<String> sWords = new ArrayList<>();
				for(String st : arr)	sWords.add(st.trim());
				qp.sWords = sWords;
				
				if(null == samples) samples = new ArrayList<>();
				
				samples.add(qp);
			}
		}
		br.close();
		return samples;
	}
	
	public static List<QueryParams> load(String path) throws Exception{
		return load(path, Integer.MAX_VALUE, Integer.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
	}
	
	public static String toSample(QueryParams qp) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(qp.k));
		sb.append(delimiter2);
		
		sb.append(String.valueOf(qp.location.getCoord(0)));
		sb.append(' ');
		sb.append(String.valueOf(qp.location.getCoord(1)));
		sb.append(delimiter2);
		
		sb.append(String.valueOf(qp.minpts));
		sb.append(delimiter2);
		
		sb.append(String.valueOf(qp.epsilon));
		sb.append(delimiter2);
		
		sb.append(String.valueOf(qp.xi));
		sb.append(delimiter2);
		
		for(int i=0; i<qp.sWords.size() - 1; i++) {
			sb.append(qp.sWords.get(i));
			sb.append(delimiter3);
		}
		if(qp.sWords.size() > 0)
			sb.append(qp.sWords.get(qp.sWords.size() - 1));
		
		return sb.toString();
	}
	
	public static void write(String path, List<QueryParams> qps) throws Exception{
		BufferedWriter bw = IOUtility.getBW(path);
		bw.write(annotation + "\n##");
		bw.write(String.valueOf(qps.size()) + "\n");
		for(int i=0; i<qps.size(); i++) {
			bw.write(String.valueOf(i+1));
			bw.write(delimiter1);
			bw.write(toSample(qps.get(i)));
			bw.write('\n');
		}
		bw.close();
	}
	
	public static void main(String[] args) throws Exception{
		List<QueryParams> qps = SampleLoader.load(Global.samplePath + "test.txt");
		System.out.println(qps);
		
		write(Global.samplePath + "w_test.txt", qps);
	}
}

















