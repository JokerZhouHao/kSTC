package index.optic;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.util.BytesRef;

import entity.Node;
import entity.NodeCollection;
import entity.QueryParams;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.StringTools;
import utility.indexlucene.AbstractLuceneIndex;

/**
 * <words, <<pid + neighbors>, <pid + neighbors>, <pid + neighbors>>索引
 * @author ZhouHao
 * @since 2019年4月27日
 */
public class Pid2Text2NeighborIndex extends AbstractLuceneIndex{
	private static final String fieldTerms = "terms";
	private static final String  fieldPidNeighbors = "pidNeighbors";
	private static QueryParser queryAndWordsParser = null;
	private static QueryParser queryOrWordsParser = null;
	public static final int signRtreeNode = Integer.MIN_VALUE; 
	
	public Pid2Text2NeighborIndex(String indexPath) throws Exception{
		super(indexPath);
		queryAndWordsParser = new QueryParser(fieldTerms, analyzer);
		queryAndWordsParser.setDefaultOperator(queryAndWordsParser.AND_OPERATOR);
		
		queryOrWordsParser = new QueryParser(fieldTerms, analyzer);
		queryOrWordsParser.setDefaultOperator(queryOrWordsParser.OR_OPERATOR);
	}
	
	@Override
	public void openIndexReader() throws Exception {
		// TODO Auto-generated method stub
		super.openIndexReader();
		this.indexSearcher.setSimilarity(new BM25Similarity());
//		this.indexSearcher.setSimilarity(new ClassicSimilarity());
	}
	
	public void addDoc(String text, int pid, List<Node> neighbors) throws Exception{
		Document doc = new Document();
		doc.add(new TextField(fieldTerms, text, Store.NO));
		doc.add(new StoredField(fieldPidNeighbors, pidNeighborsToBytes(pid, neighbors)));
		indexWriter.addDocument(doc);
	}
	
	public void searchWords(QueryParams queryParams, Point[] allLocations, List<Node> nodeCol,
							Map<Integer, List<NeighborsNode>> pidNeighbors ) throws Exception {
		TopDocs results = indexSearcher.search(queryOrWordsParser.parse(StringTools.collection2Str(queryParams.sWords)), 
											   Integer.MAX_VALUE);
		ScoreDoc[] hits = results.scoreDocs;
		if(0 == hits.length)	return;
		
		ByteBuffer bb = null;
		Document doc = null;
		int pid = 0, num = 0, j;
		List<NeighborsNode> neis = null;
		double maxScore = hits[0].score;
		for(int i=0; i<hits.length; i++) {
			doc = indexSearcher.doc(hits[i].doc);
			bb = ByteBuffer.wrap(doc.getBinaryValue(fieldPidNeighbors).bytes);
			pid = bb.getInt();
			num = bb.getInt();
			neis = new ArrayList<>();
			for(j=0; j<num; j++) {
				neis.add(new NeighborsNode(bb.getInt(), bb.getDouble()));
			}
			
			nodeCol.add(new Node(pid, allLocations[pid],
					queryParams.location.getMinimumDistance(allLocations[pid]),
					1 - hits[i].score / maxScore));
			pidNeighbors.put(pid, neis);
		}
	}
	
	public byte[] pidNeighborsToBytes(int pid, List<Node> neighbors) throws Exception{
		int numByte = 4 * (1 + 1 + neighbors.size() * 3);
		ByteBuffer bb = ByteBuffer.allocate(numByte);
		bb.putInt(pid);
		bb.putInt(neighbors.size());
		for(Node nd : neighbors) {
			bb.putInt(nd.id);
			bb.putDouble(nd.disToCenter);
		}
		return bb.array();
	}
	
	public static void main(String[] args) throws Exception{
		
	}
}
