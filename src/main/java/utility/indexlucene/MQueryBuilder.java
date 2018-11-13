package utility.indexlucene;

import java.util.List;

import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause.Occur;

public class MQueryBuilder {
	public static BooleanQuery buildWidsQuery(String fieldName, List<Integer> wids) {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		for(int wid : wids) {
			builder = builder.add(new BooleanClause(IntPoint.newExactQuery(fieldName, wid), Occur.MUST));
		}
		return builder.build();
	}
}
