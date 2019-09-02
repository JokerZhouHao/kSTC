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
	private QueryParser queryParser = null;
	
	public Term2PidNeighborsIndex(String indexPath) throws Exception{
		super(indexPath);
		queryParser = new QueryParser(fieldTerm, analyzer);
	}
	
	public byte[] pidNeighborsToBytes(Map<Integer, List<Node>> pidNeighbors, int numByte) throws Exception{
		ByteBuffer bb = ByteBuffer.allocate(numByte);
		bb.putInt(pidNeighbors.size());
		for(Entry<Integer, List<Node>> en : pidNeighbors.entrySet()) {
			bb.putInt(en.getKey());
			bb.putInt(en.getValue().size());
			for(Node inn : en.getValue()) {
				bb.putInt(inn.id);
//				bb.putDouble(inn.disToCenter);
				bb.putFloat((float)inn.disToCenter);
			}
		}
		return bb.array();
	}
	
	public byte[] pidNeighborsToBytes(List<Node> ngbs, int numByte) throws Exception{
		ByteBuffer bb = ByteBuffer.allocate(numByte);
		bb.putInt(ngbs.size());
		for(Node nd : ngbs) {
			bb.putInt(nd.id);
			bb.putFloat((float)nd.coreDistance);
			bb.putFloat((float)nd.reachabilityDistance);
			if(nd.coreDistance == Node.UNDEFINED)	continue;
			bb.putInt(nd.neighbors.size());
			for(Node ngb : nd.neighbors) {
				bb.putInt(ngb.id);
				bb.putFloat((float)ngb.disToCenter);
			}
		}
		return bb.array();
	}
	
	
	public byte[] pidNeighborsToBytes(Entry<Integer, List<Node>> pidNeighbors) throws Exception{
		int numByte = 4 * (1 + 1 + pidNeighbors.getValue().size() * 3);
		ByteBuffer bb = ByteBuffer.allocate(numByte);
		bb.putInt(pidNeighbors.getKey());
		List<Node> nds = pidNeighbors.getValue();
		bb.putInt(nds.size());
		for(Node nd : nds) {
			bb.putInt(nd.id);
			bb.putDouble(nd.disToCenter);
		}
		return bb.array();
	}
	
	public byte[] pidNeighborsToBytes(Map<Integer, List<Node>> pidNeighbors) throws Exception{
		int numInt = 0;
		numInt++; // pidNeighbors.size
		for(Entry<Integer, List<Node>> en : pidNeighbors.entrySet()) {
			numInt += 2; // en.key + en.value.size
			numInt += en.getValue().size() * 3; // node's id + disToCenter
			if(numInt > Global.maxPidNeighbors4Bytes) {
				return null;
//				throw new Exception("pidNeighbors转化为byte后的数量超出了int最大值");
			}
		}
		return pidNeighborsToBytes(pidNeighbors, numInt * 4);
	}
	
	private Map<Integer, List<NeighborsNode>> bytesToPidNeighbors(byte[] bytes) {
		int numInt = 0;
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		Map<Integer, List<NeighborsNode>> map = new HashMap<>();
		numInt = bb.getInt();
		int pid = 0;
		List<NeighborsNode> neighbors = null;
		int numNeighbor = 0;
		for(int i=0; i<numInt; i++) {
			pid = bb.getInt();
			numNeighbor = bb.getInt();
			neighbors = new ArrayList<>();
			for(int j=0; j<numNeighbor; j++) {
//				neighbors.add(new NeighborsNode(bb.getInt(), bb.getDouble()));
				neighbors.add(new NeighborsNode(bb.getInt(), bb.getFloat()));
			}
			map.put(pid, neighbors);
		}
		return map;
	}
	
	private Map<Node, List<NeighborsNode>> bytesToNodeNeighbors(byte[] bytes) {
		int numInt = 0;
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		Map<Node, List<NeighborsNode>> map = new HashMap<>();
		numInt = bb.getInt();
		List<NeighborsNode> neighbors = null;
		for(int i=0; i<numInt; i++) {
			Node nd = new Node();
			nd.id = bb.getInt();
			nd.coreDistance = bb.getFloat();
			nd.reachabilityDistance = bb.getFloat();
			neighbors = null;
			if(nd.coreDistance != Node.UNDEFINED) {
				neighbors = new ArrayList<>();
				int numNgb = bb.getInt();
				while(numNgb > 0) {
					neighbors.add(new NeighborsNode(bb.getInt(), bb.getFloat()));
					numNgb--;
				}
			}
			map.put(nd, neighbors);
		}
		return map;
	}
	
	private List<Node> bytesToNodeList(byte[] bytes) {
		int numInt = 0;
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		numInt = bb.getInt();
		List<Node> orderNodes = new ArrayList<>();
		for(int i=0; i<numInt; i++) {
			Node nd = new Node();
			nd.id = bb.getInt();
			nd.coreDistance = bb.getFloat();
			nd.reachabilityDistance = bb.getFloat();
			orderNodes.add(nd);
			
			if(nd.coreDistance != Node.UNDEFINED) {
				int numNgb = bb.getInt();
				while(numNgb > 0) {
					bb.getInt();
					bb.getFloat();
					numNgb--;
				}
			}
		}
		return orderNodes;
	}
	
	
	
	public void addDoc(String term, byte[] pidNeighbors) throws Exception{
		Document doc = new Document();
		doc.add(new StringField(fieldTerm, term, Store.NO));
		doc.add(new StoredField(fieldPidNeighbors, new BytesRef(pidNeighbors)));
		indexWriter.addDocument(doc);
	}
	
	public void addDoc(String term, Map<Integer, List<Node>> pidNeighbors) throws Exception{
		this.addDoc(term, pidNeighborsToBytes(pidNeighbors));
	}
	
	public Map<Integer, List<NeighborsNode>> searchTerm(String term, QueryParams qp) throws Exception{
		TopDocs results = indexSearcher.search(queryParser.parse(term), 1);
		ScoreDoc[] hits = results.scoreDocs;
		if(0 == hits.length)	return null;
		
		qp.runTimeRec.setFrontTime();
		byte[] bs = indexSearcher.doc(hits[0].doc).getBinaryValue(fieldPidNeighbors).bytes;
		qp.runTimeRec.numByteOfTermPNgb += bs.length;
		qp.runTimeRec.timeReadTermPNgb += qp.runTimeRec.getTimeSpan();
		
		return bytesToPidNeighbors(bs);
	}
	
	public Map<Node, List<NeighborsNode>> searchTermReNode2Ngbs(String term, QueryParams qp) throws Exception{
		TopDocs results = indexSearcher.search(queryParser.parse(term), 1);
		ScoreDoc[] hits = results.scoreDocs;
		if(0 == hits.length)	return null;
		
		qp.runTimeRec.setFrontTime();
		byte[] bs = indexSearcher.doc(hits[0].doc).getBinaryValue(fieldPidNeighbors).bytes;
		qp.runTimeRec.numByteOfTermPNgb += bs.length;
		qp.runTimeRec.timeReadTermPNgb += qp.runTimeRec.getTimeSpan();
		
		return bytesToNodeNeighbors(bs);
	}
	
	public List<Node> searchTermReNodes(String term, QueryParams qp) throws Exception{
		TopDocs results = indexSearcher.search(queryParser.parse(term), 1);
		ScoreDoc[] hits = results.scoreDocs;
		if(0 == hits.length)	return null;
		
		qp.runTimeRec.setFrontTime();
		byte[] bs = indexSearcher.doc(hits[0].doc).getBinaryValue(fieldPidNeighbors).bytes;
		qp.runTimeRec.numByteOfTermPNgb += bs.length;
		qp.runTimeRec.timeReadTermPNgb += qp.runTimeRec.getTimeSpan();
		
		return bytesToNodeList(bs);
	}
	
	public Map<Integer, List<NeighborsNode>> searchTerm1(String term) throws Exception{
		TopDocs results = indexSearcher.search(queryParser.parse(term), Integer.MAX_VALUE);
		ScoreDoc[] hits = results.scoreDocs;
		if(0 == hits.length)	return null;
		
		Map<Integer, List<NeighborsNode>> pidNeis = new HashMap<>();
		int pid = 0;
		List<NeighborsNode> neis = null;
		ByteBuffer bb = null;
		int i, j, size;
		for(i=0; i<hits.length; i++) {
			bb = ByteBuffer.wrap(indexSearcher.doc(hits[i].doc).getBinaryValue(fieldPidNeighbors).bytes);
			pid = bb.getInt();
			size = bb.getInt();
			neis = new ArrayList<>();
			for(j=0; j<size; j++) {
				neis.add(new NeighborsNode(bb.getInt(), bb.getDouble()));
			}
			pidNeis.put(pid, neis);
		}
		
		return pidNeis;
	}
	
	public static void main(String[] args) throws Exception{
		Term2PidNeighborsIndex index = new Term2PidNeighborsIndex(Global.pathTerm2PidNeighborsIndex);
		index.openIndexReader();
		Map<Integer, List<NeighborsNode>> pid2Ngb = index.searchTerm("pet", null);
		System.out.println(pid2Ngb);
		
//		index.openIndexWriter();
//		String term = "ab";
//		Map<Integer, List<NeighborsNode>> pidNeighbors = new HashMap<>();
//		List<NeighborsNode> neighbors = new ArrayList<>();
//		neighbors.add(new NeighborsNode(1, 1));
//		neighbors.add(new NeighborsNode(2, 2));
//		pidNeighbors.put(1, neighbors);
//		index.addDoc(term, pidNeighbors);
//		
//		term = "abc";
//		pidNeighbors = new HashMap<>();
//		neighbors = new ArrayList<>();
//		neighbors.add(new NeighborsNode(2, 2));
//		neighbors.add(new NeighborsNode(3, 3));
//		pidNeighbors.put(2, neighbors);
//		index.addDoc(term, pidNeighbors);
//		index.close();
//		
//		index.openIndexReader();
//		pidNeighbors = index.searchTerm("ab");
//		pidNeighbors = index.searchTerm("abc");
//		index.close();
		
	}
}
