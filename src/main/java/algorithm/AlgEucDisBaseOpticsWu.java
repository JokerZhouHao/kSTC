package algorithm;

import java.util.List;
import java.util.Map;

import entity.Node;
import entity.QueryParams;
import entity.SortedClusters;

public class AlgEucDisBaseOpticsWu extends AlgEucDisBaseOptics{
	public AlgEucDisBaseOpticsWu(QueryParams qp) throws Exception{
		super(qp);
	}
	
	
	
	@Override
	public SortedClusters excuteQuery(QueryParams qParams, String pathOrderedFile,
		List<Node> nodes, List<Node> sortedNodes) throws Exception {
		// TODO Auto-generated method stub
		return super.excuteQueryByWu(qParams, pathOrderedFile, nodes, sortedNodes);
	}
}
