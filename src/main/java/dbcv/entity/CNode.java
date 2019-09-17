package dbcv.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spatialindex.spatialindex.Point;
import utility.Global;

public class CNode {
	public Point[] coords = null;
	public Integer id = -1;
	public int orgId = -1;
	protected double coreDis = 0.0;
	
	private Boolean signHasCalCoreDis = Boolean.FALSE;
	
	public CNode() {}
	
	public CNode(Point[] coords, int id, int orgId) {
		super();
		this.coords = coords;
		this.id = id;
		this.orgId = orgId;
	}
	
	public CNode copy() {
		return new CNode(coords, id, orgId);
	}
	
	public double minDistance(CNode node) {
		return coords[orgId].getMinimumDistance(coords[node.orgId]);
	}
	
	public double calCoreDist(double d, List<Double> distances, List<CNode>... ndss) {
		signHasCalCoreDis = Boolean.TRUE;
		coreDis = 0.0;
		for(List<CNode> nds : ndss) {
			for(CNode nd : nds) {
				// 过滤掉距离为0的
				double dis = minDistance(nd);
//				if(dis != 0.0)	distances.add(dis);
				distances.add(dis);
			}
		}
		if(distances.isEmpty())	return 0.0;
		Collections.sort(distances);
		
		for(int i = 0; i < distances.size(); i++) {
			if(Global.isZero(distances.get(i)))	continue;
			coreDis += Math.pow(1 / distances.get(i), d);
		}
		if(coreDis == 0.0)	return 0.0;
		coreDis = Math.pow(coreDis / (distances.size() - 1), -1 / d);
		return coreDis;
	}
	
	public double coreDist() throws Exception{
		if(!signHasCalCoreDis)	throw new Exception("必须至少调用一次calCoreDist方法后，才能调用coreDist方法");
		return coreDis;
	}
	
	@Override
	public String toString() {
		return "CNode [id=" + id + ", orgId=" + orgId + ", coreDis=" + coreDis + "]";
	}

	public static void main(String[] args) throws Exception{
		Point[] coords = {
				new Point(new double[]{0, 0}),
				new Point(new double[]{3, 4}),
				new Point(new double[]{6, 8})
		};
		
		List<CNode> nds = new ArrayList<>();
		nds.add(new CNode(coords, 0, 0));
		nds.add(new CNode(coords, 1, 1));
		nds.add(new CNode(coords, 2, 2));
//		System.out.println(nds.get(0).calCoreDist(2, nds));
		System.out.println(nds.get(0).coreDist());
	}
}
























