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
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.util.BytesRef;

import entity.Node;
import entity.NodeCollection;
import entity.QueryParams;
import entity.fastrange.Cellid2Nodes;
import precomputation.dataset.file.FileLoader;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.MLog;
import utility.StringTools;
import utility.indexlucene.AbstractLuceneIndex;

public class CellidPidWordsIndex extends AbstractLuceneIndex{
	private static final String fieldId = "cellidpid";
	private static final String fieldWords = "words";
	private QueryParser queryAndWordsParser = null;
	private QueryParser queryOrWordsParser = null;
	public static final int signRtreeNode = Integer.MIN_VALUE; 
	
	public CellidPidWordsIndex(String indexPath) throws Exception{
		super(indexPath);
		queryAndWordsParser = new QueryParser(fieldWords, analyzer);
		queryAndWordsParser.setDefaultOperator(queryAndWordsParser.AND_OPERATOR);
		
		queryOrWordsParser = new QueryParser(fieldWords, analyzer);
		queryOrWordsParser.setDefaultOperator(queryOrWordsParser.OR_OPERATOR);
	}
	
	@Override
	public void openIndexReader() throws Exception {
		// TODO Auto-generated method stub
		super.openIndexReader();
		this.indexSearcher.setSimilarity(new BM25Similarity());
//		this.indexSearcher.setSimilarity(new ClassicSimilarity());
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
	
	public Map<Integer, List<Node>> searchWords(List<String> words, Point[] allLocations) throws Exception {
		Query query = null;
		synchronized (queryAndWordsParser) {
			query = queryAndWordsParser.parse(StringTools.collection2Str(words));
		}
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
		double maxScore = hits[0].score;
		for(int i=0; i<hits.length; i++) {
			doc = indexSearcher.doc(hits[i].doc);
			bb = ByteBuffer.wrap(doc.getBinaryValue(fieldId).bytes);
			cellid = bb.getInt();
			if(cellid == signRtreeNode)	continue;
			pid = bb.getInt();
			if(null == (nList = cellid2Node.get(cellid))) {
				nList = new ArrayList<>();
				cellid2Node.put(cellid, nList);
			}
			pot = allLocations[pid];
			nList.add(new Node(pid, pot, 0, 1 - hits[i].score/maxScore));
//			System.out.println(String.valueOf(id) + " " + hits[i].score/queryParams.sWords.size());
//			resSet.add(Integer.parseInt(doc.get(fieldId)));
		}
		if(cellid2Node.isEmpty())	return null;
		return cellid2Node;
		
	}
	
	public Map<Integer, Double> searchWordsReScore(List<String> words, Point[] allLocations) throws Exception {
		Query query = null;
		query = queryAndWordsParser.parse(StringTools.collection2Str(words));
		
		TopDocs results = indexSearcher.search(query, Integer.MAX_VALUE);
		ScoreDoc[] hits = results.scoreDocs;
		
		if(0 == hits.length)	return null;
		ByteBuffer bb = null;
		Document doc = null;
		int cellid, pid = 0;
		Map<Integer, Double> pid2score = new HashMap<>();
		for(int i=0; i<hits.length; i++) {
			doc = indexSearcher.doc(hits[i].doc);
			bb = ByteBuffer.wrap(doc.getBinaryValue(fieldId).bytes);
			cellid = bb.getInt();
			if(cellid == signRtreeNode)	continue;
			pid = bb.getInt();
			pid2score.put(pid, (double)hits[i].score);
		}
		if(pid2score.isEmpty())	return null;
		return pid2score;
	}
	
	
	public Cellid2Nodes searchWordsReCellid2Nodes(QueryParams queryParams, Point[] allLocations) throws Exception {
		Query query = null;
		
		if(queryParams.sWords.isEmpty()) MLog.log("queryParams.sWords.isEmpty");
		
		query = queryAndWordsParser.parse(StringTools.collection2Str(queryParams.sWords));
//		query = queryOrWordsParser.parse(StringTools.collection2Str(queryParams.sWords));
		
		TopDocs results = indexSearcher.search(query, Integer.MAX_VALUE);
		ScoreDoc[] hits = results.scoreDocs;
		
		if(0 == hits.length)	return null;
		double dis = Double.MAX_VALUE;
		Point pot = null;
		ByteBuffer bb = null;
		Document doc = null;
		int cellid, pid = 0;
		double maxScore = hits[0].score;
		Cellid2Nodes cid2Nds = new Cellid2Nodes();
		for(int i=0; i<hits.length; i++) {
			doc = indexSearcher.doc(hits[i].doc);
			bb = ByteBuffer.wrap(doc.getBinaryValue(fieldId).bytes);
			cellid = bb.getInt();
			if(cellid == signRtreeNode)	continue;
			pid = bb.getInt();
			pot = allLocations[pid];
			dis = queryParams.location.getMinimumDistance(allLocations[pid]);
			cid2Nds.add(new Node(pid, pot, dis, 1 - hits[i].score/maxScore, cellid));
//			System.out.println(String.valueOf(id) + " " + hits[i].score/queryParams.sWords.size());
//			resSet.add(Integer.parseInt(doc.get(fieldId)));
		}
		if(cid2Nds.isEmpty())	return null;
		return cid2Nds;
	}
	
	public NodeCollection searchWordsReNodeCol(QueryParams queryParams, Point[] allLocations) throws Exception {
		Query query = null;
		query = queryAndWordsParser.parse(StringTools.collection2Str(queryParams.sWords));
//		query = queryOrWordsParser.parse(StringTools.collection2Str(queryParams.sWords));
		
		TopDocs results = indexSearcher.search(query, Integer.MAX_VALUE);
		ScoreDoc[] hits = results.scoreDocs;
		
		if(0 == hits.length)	return null;
		double dis = Double.MAX_VALUE;
		NodeCollection nodeCol = new NodeCollection();
		Point pot = null;
		ByteBuffer bb = null;
		Document doc = null;
		int pid = 0;
		int cellId = 0;
		double maxScore = hits[0].score;
		for(int i=0; i<hits.length; i++) {
			doc = indexSearcher.doc(hits[i].doc);
			bb = ByteBuffer.wrap(doc.getBinaryValue(fieldId).bytes);
			cellId = bb.getInt();
			pid = bb.getInt();
			if(pid >= 0) {
				pot = allLocations[pid];
				dis = queryParams.location.getMinimumDistance(allLocations[pid]);
			}
			else {
				pot = null;
				dis = Double.MAX_VALUE;
			}
			nodeCol.add(new Node(pid, pot, dis, 1 - hits[i].score/maxScore, cellId));
//			System.out.println(String.valueOf(id) + " " + hits[i].score/queryParams.sWords.size());
//			resSet.add(Integer.parseInt(doc.get(fieldId)));
		}
		return nodeCol;
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
//		System.out.println(index.searchWords(qParams, allLocations));
		
		index.close();
	}
	
	
	
	public static void main(String[] args) throws Exception{
		CellidPidWordsIndex.testSearchWords();
	}
}
