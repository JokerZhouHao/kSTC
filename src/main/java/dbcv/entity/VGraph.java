package dbcv.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import spatialindex.spatialindex.Point;
import utility.MLog;

class Edge {
	public int beg = 0;
	public int en = 0;
	public double weight = 0.0;
}

/**
 * Mutual Reach Dist Graph
 * @author ZhouHao
 * @since 2019年7月6日
 */
public class VGraph {
	public Point coords[] = null;
	public List<CNode> cnds = null;
	public GNode[] edges = null;
	private double maxWeightInMST = 0.0; // 在最小生成树中的权重最大边的权重
	private Map<Integer, Integer> nd2index = null;	// 记录GNode在图中的序号
	private int numNode = 0;
	
	
	public VGraph(Point[] coords, List<CNode> cnds) throws Exception{
		this.coords = coords;
		this.cnds = cnds;
//		this.edges = new GNode[this.cnds.size()];
//		buildGraph();
		numNode = cnds.size();
	}
	
	private double weight(CNode nd1, CNode nd2) throws Exception{
		return Math.max(nd1.coreDist(), Math.max(nd2.coreDist(), nd1.minDistance(nd2)));
	}
	
	private double weight(int i1, int i2) throws Exception{
		return weight(cnds.get(i1), cnds.get(i2));
	}
	
	private void buildGraph() throws Exception{
		GNode gnd = null;
		nd2index = new HashMap<>();
		for(int i=0; i<cnds.size(); i++) {
			edges[i] = gnd = new GNode(cnds.get(i));
			nd2index.put(cnds.get(i).id, i);
			for(int j=0; j<cnds.size(); j++) {
				if(i == j)	continue;
				gnd.next = new GNode(cnds.get(j), weight(cnds.get(i), cnds.get(j)));
				gnd = gnd.next;
			}
		}
	}
	
	public double maxWeightInMST() throws Exception {
		if(maxWeightInMST != 0.0)	return maxWeightInMST;
		maxWeightInMST = 0.0;
		
//		if(numNode == 14 || numNode == 9498 || numNode == 24079 || numNode == 14) {
//			MLog.log("node num: " + numNode);
//		}
		
		
		Edge[] tree = new Edge[numNode - 1];
		for(int i=0; i<tree.length; i++)	tree[i] = new Edge();
		
		
//		if(numNode == 14 || numNode == 9498 || numNode == 24079 || numNode == 14) {
//			MLog.log("start init ");
//		}
		
		
		// 初始选择顶点v0
		for(int v = 1; v <= numNode - 1; v++) {
			tree[v-1].beg = 0;
			tree[v-1].en = v;
			tree[v-1].weight = weight(tree[v-1].beg, tree[v-1].en);
		}
		
		
//		if(numNode == 14 || numNode == 9498 || numNode == 24079 || numNode == 14) {
//			MLog.log("end init ");
//		}
		
		
		// 依次求最小边
		Edge x = null;
		int j, k, s, v;
		double min = 0.0, tWeight = 0.0;
		for(k=0; k <= numNode - 2; k++) {
			min = tree[k].weight;
			s = k;
			for(j = k + 1; j <= numNode - 2; j++) {
				if(tree[j].weight < min) {
					min = tree[j].weight;
					s = j;
				}
			}
			
			maxWeightInMST = maxWeightInMST >= min ? maxWeightInMST : min;
			
			
//			if(numNode == 14 || numNode == 9498 || numNode == 24079 || numNode == 14) {
//				MLog.log("ndHasAdd size: " + (k + 2));
//			}
			
			
			
			v = tree[s].en;
			
			x = tree[s];	tree[s] = tree[k];	tree[k] = x;
			
			for(j = k + 1; j <= numNode - 2; j++) {
				tWeight = weight(v, tree[j].en);
				if(tWeight < tree[j].weight) {
					tree[j].weight = tWeight;
					tree[j].beg = v;
				}
			}
		}
		
		return maxWeightInMST;
	}
	
	public double maxWeightInMST1() {
		if(maxWeightInMST != 0.0)	return maxWeightInMST;
		maxWeightInMST = 0.0;
		PriorityQueue<GNode> minHeap = new PriorityQueue<>();
		Set<GNode> ndHasAdd = new HashSet<>();
		ndHasAdd.add(edges[0]);
		int index = 0;
		
		
		long startTime = System.currentTimeMillis();
		if(edges.length == 14 || edges.length==9498 || edges.length==24079 || edges.length==14) {
			MLog.log("edge num: " + edges.length);
		}
		
		while(ndHasAdd.size() != edges.length) {
			
			
			if(edges.length == 14 || edges.length==9498 || edges.length==24079 || edges.length==14) {
				MLog.log("ndHasAdd size: " + ndHasAdd.size());
			}
			
			
			GNode p = edges[index].next;
			while(p != null) {
				if(!ndHasAdd.contains(p)) {
					minHeap.add(p);
				}
				p = p.next;
			}
			while(true) {
				p = minHeap.poll();
				if(!ndHasAdd.contains(p)) {
					maxWeightInMST = maxWeightInMST >= p.weight ? maxWeightInMST : p.weight; 
					ndHasAdd.add(p);
					index = nd2index.get(p.id);
					break;
				} 
			}
		}
		return maxWeightInMST;
	}
}



















