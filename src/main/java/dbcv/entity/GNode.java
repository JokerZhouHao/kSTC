package dbcv.entity;


/**
 * 表示Mutual Reach dis Graph中的点
 * @author ZhouHao
 * @since 2019年7月6日
 */
public class GNode extends CNode implements Comparable{
	public Boolean hasAccessed = Boolean.FALSE;
	public double weight = 0.0; 	// 表示以该点为出度边的权重
	public GNode next = null;
	
	public GNode(CNode nd) {
		this(nd, 0.0);
	}
	
	public GNode(CNode nd, double weight) {
		this.coords = nd.coords;
		this.id = nd.id;
		this.orgId = nd.orgId;
		this.coreDis = nd.coreDis;
		this.weight = weight;
	}
	
	@Override
	public int compareTo(Object o) {
		GNode nd = (GNode)o;
		if(weight > nd.weight)	return 1;
		else if(weight == nd.weight)	return 0;
		return -1;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GNode other = (GNode) obj;
		if(id == other.id)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
}
