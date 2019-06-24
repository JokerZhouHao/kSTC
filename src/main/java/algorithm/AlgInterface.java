package algorithm;

import entity.QueryParams;
import entity.SortedClusters;

public interface AlgInterface {
	public SortedClusters excuteQuery(QueryParams qParams) throws Exception;
	public void free() throws Exception;
}
