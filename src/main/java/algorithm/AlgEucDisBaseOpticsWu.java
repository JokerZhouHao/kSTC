package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import entity.Node;
import entity.QueryParams;
import entity.SortedClusters;

public class AlgEucDisBaseOpticsWu extends AlgEucDisBaseOptics{
	public AlgEucDisBaseOpticsWu(QueryParams qp) throws Exception{
		super(qp);
	}
	
	public SortedClusters excuteQueryByWu(QueryParams qParams, String pathOrderedFile,
			List<Node> nodes, List<Node> sortedNodes) throws Exception {
		return super.excuteQueryByWu(qParams, pathOrderedFile, nodes, sortedNodes);
	}
}
