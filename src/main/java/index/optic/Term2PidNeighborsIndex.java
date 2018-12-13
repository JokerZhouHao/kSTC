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

public class Term2PidNeighborsIndex extends AbstractLuceneIndex{
	private static final String fieldTerm = "term";
	private static final String fieldPidNeighbors = "pidneighbors";
	private static QueryParser queryParser = null;
	
	public Term2PidNeighborsIndex(String indexPath) throws Exception{
		super(indexPath);
		queryParser = new QueryParser(fieldTerm, analyzer);
	}
	
	private byte[] pidNeighborsToBytes(Map<Integer, List<Integer>> pidNeighbors) {
		int numInt = 0;
		numInt++;
		for(Entry<Integer, List<Integer>> en : pidNeighbors.entrySet()) {
			numInt += 2;
			numInt += en.getValue().size();
		}
		ByteBuffer bb = ByteBuffer.allocate(numInt * 4);
		bb.putInt(pidNeighbors.size());
		for(Entry<Integer, List<Integer>> en : pidNeighbors.entrySet()) {
			bb.putInt(en.getKey());
			bb.putInt(en.getValue().size());
			for(Integer inn : en.getValue()) {
				bb.putInt(inn);
			}
		}
		return bb.array();
	}
	
	private Map<Integer, List<Integer>> bytesToPidNeighbors(byte[] bytes) {
		int numInt = 0;
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		Map<Integer, List<Integer>> map = new HashMap<>();
		numInt = bb.getInt();
		int pid = 0;
		List<Integer> neighbors = null;
		int numNeighbor = 0;
		for(int i=0; i<numInt; i++) {
			pid = bb.getInt();
			numNeighbor = bb.getInt();
			neighbors = new ArrayList<>();
			for(int j=0; j<numNeighbor; j++) {
				neighbors.add(bb.getInt());
			}
			map.put(pid, neighbors);
		}
		return map;
	}
	
	public void addDoc(String term, Map<Integer, List<Integer>> pidNeighbors) throws Exception{
		Document doc = new Document();
		doc.add(new StringField(fieldTerm, term, Store.NO));
		doc.add(new StoredField(fieldPidNeighbors, new BytesRef(pidNeighborsToBytes(pidNeighbors))));
		indexWriter.addDocument(doc);
	}
	
	public Map<Integer, List<Integer>> searchTerm(String term) throws Exception{
		TopDocs results = indexSearcher.search(queryParser.parse(term), 1);
		ScoreDoc[] hits = results.scoreDocs;
		if(0 == hits.length)	return null;
		return bytesToPidNeighbors(indexSearcher.doc(hits[0].doc).getBinaryValue(fieldPidNeighbors).bytes);
	}
	
	public static void main(String[] args) throws Exception{
		Term2PidNeighborsIndex index = new Term2PidNeighborsIndex(Global.pathTestIndex);
		
		index.openIndexWriter();
		String term = "ab";
		Map<Integer, List<Integer>> pidNeighbors = new HashMap<>();
		List<Integer> neighbors = new ArrayList<>();
		neighbors.add(1);
		neighbors.add(2);
		pidNeighbors.put(1, neighbors);
		index.addDoc(term, pidNeighbors);
		
		term = "abc";
		pidNeighbors = new HashMap<>();
		neighbors = new ArrayList<>();
		neighbors.add(2);
		neighbors.add(3);
		pidNeighbors.put(2, neighbors);
		index.addDoc(term, pidNeighbors);
		index.close();
		
		index.openIndexReader();
		pidNeighbors = index.searchTerm("ab");
		pidNeighbors = index.searchTerm("abc");
		index.close();
		
	}
}
