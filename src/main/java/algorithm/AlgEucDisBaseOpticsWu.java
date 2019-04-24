package algorithm;

import java.util.List;
import java.util.Map;

import entity.Node;
import entity.QueryParams;
import entity.SortedClusters;

public class AlgEucDisBaseOpticsWu extends AlgEucDisBaseOptics{
	public AlgEucDisBaseOpticsWu() throws Exception{
		super();
	}
	
	@Override
	public SortedClusters excuteQuery(QueryParams qParams, String pathOrderedFile,
			Map<Integer, List<Node>> cellid2Nodes, List<Node> sortedNodes) throws Exception {
		// TODO Auto-generated method stub
		return super.excuteQueryByWu(qParams, pathOrderedFile, cellid2Nodes, sortedNodes);
	}
}
