package index;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.util.BytesRef;

import entity.Node;
import entity.NodeCollection;
import entity.QueryParams;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.StringTools;
import utility.indexlucene.AbstractLuceneIndex;

public class CellidPidWordsIndex extends AbstractLuceneIndex{
	private static final String fieldId = "cellidpid";
	private static final String fieldWords = "words";
	private static QueryParser queryAndWordsParser = null;
	
	public CellidPidWordsIndex(String indexPath) throws Exception{
		super(indexPath);
		queryAndWordsParser = new QueryParser(fieldWords, analyzer);
		queryAndWordsParser.setDefaultOperator(queryAndWordsParser.AND_OPERATOR);
	}
	
	@Override
	public void openIndexReader() throws Exception {
		// TODO Auto-generated method stub
		super.openIndexReader();
		this.indexSearcher.setSimilarity(new BM25Similarity());
	}
	
	public void addWordsDoc(int cellid, int pid, String words) throws Exception{
		Document doc = new Document();
		ByteBuffer bb = ByteBuffer.allocate(4 * 2);
		bb.putInt(cellid);
		bb.putInt(pid);
		doc.add(new StoredField(fieldId, new BytesRef(bb.array())));
		doc.add(new TextField(fieldWords, words, Store.NO));
		indexWriter.addDocument(doc);
	}
	
	public Map<Integer, List<Node>> searchWords(QueryParams queryParams, Point[] allLocations) throws Exception {
		Query query = null;
		query = queryAndWordsParser.parse(StringTools.collection2Str(queryParams.sWords));
		TopDocs results = indexSearcher.search(query, Integer.MAX_VALUE);
		ScoreDoc[] hits = results.scoreDocs;
		
		if(0 == hits.length)	return null;
		double dis = Double.MAX_VALUE;
		Point pot = null;
		Map<Integer, List<Node>> cellid2Node = new HashMap<>();
		List<Node> nList = null;
		ByteBuffer bb = null;
		Document doc = null;
		int cellid, pid = 0;
		for(int i=0; i<hits.length; i++) {
			doc = indexSearcher.doc(hits[i].doc);
			bb = ByteBuffer.wrap(doc.getBinaryValue(fieldId).bytes);
			cellid = bb.getInt();
			pid = bb.getInt();
			if(null == (nList = cellid2Node.get(cellid))) {
				nList = new ArrayList<>();
				cellid2Node.put(cellid, nList);
			}
			pot = allLocations[pid];
			dis = queryParams.location.getMinimumDistance(allLocations[pid]);
			nList.add(new Node(pid, pot, dis, 1 - hits[i].score/queryParams.sWords.size()));
//			System.out.println(String.valueOf(id) + " " + hits[i].score/queryParams.sWords.size());
//			resSet.add(Integer.parseInt(doc.get(fieldId)));
		}
		return cellid2Node;
	}
	
	public static void testSearchWords() throws Exception{
		Point[] allLocations = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
		CellidPidWordsIndex index = new CellidPidWordsIndex(Global.pathCellidPidWordsIndex);
		
		index.openIndexReader();
		
		List<String> sWords = new ArrayList<>();
		sWords.add("c");
		sWords.add("e");
//		sWords.add("d");
		QueryParams qParams = new QueryParams();
		double[] pots = {0.1, 0.2};
		qParams.location = new Point(pots);
		qParams.sWords = sWords;
		System.out.println(index.searchWords(qParams, allLocations));
		
		index.close();
	}
	
	
	
	public static void main(String[] args) throws Exception{
		CellidPidWordsIndex.testSearchWords();
	}
}
