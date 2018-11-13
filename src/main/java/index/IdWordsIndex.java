package index;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.CharStream;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.queryparser.surround.query.FieldsQuery;
import org.apache.lucene.queryparser.xml.builders.TermQueryBuilder;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.QueryBuilder;

import utility.Global;
import utility.StringTools;
import utility.indexlucene.AbstractLuceneIndex;
import utility.indexlucene.MQueryBuilder;

public class IdWordsIndex extends AbstractLuceneIndex {
	
	private static String fieldId = "id";
	private static String fieldWids = "wids";
	private static String fieldWords = "words";
	private static QueryParser queryAndWordsParser = null;
	
	
	public IdWordsIndex(String indexPath) throws Exception{
		super(indexPath);
		queryAndWordsParser = new QueryParser(fieldWords, analyzer);
		queryAndWordsParser.setDefaultOperator(queryAndWordsParser.AND_OPERATOR);
	}
	
	public void addDoc(int id, Set<Integer> wids) throws Exception{
		Document doc = new Document();
		doc.add(new StoredField(fieldId, id));
		for(int wid : wids) {
			doc.add(new IntPoint(fieldWids, wid));
		}
		indexWriter.addDocument(doc);
	}
	
	public Set<Integer> searchWords(List<String> words) throws Exception {
		Query query = null;
		query = queryAndWordsParser.parse(StringTools.strList2Str(words));
		
		TopDocs results = indexSearcher.search(query, Integer.MAX_VALUE);
		ScoreDoc[] hits = results.scoreDocs;
		
		if(0 == hits.length)	return null;
		Set<Integer> resSet = new HashSet<>();
		Document doc = null;
		for(int i=0; i<hits.length; i++) {
			doc = indexSearcher.doc(hits[i].doc);
			resSet.add(Integer.parseInt(doc.get(fieldId)));
		}
		return resSet;
	}
	
	public Set<Integer> searchWids(List<Integer> wids) throws Exception {
		BooleanQuery bQuery = MQueryBuilder.buildWidsQuery(fieldWids, wids);
		TopDocs results = indexSearcher.search(bQuery, Integer.MAX_VALUE);
		ScoreDoc[] hits = results.scoreDocs;
		
		if(0 == hits.length)	return null;
		Set<Integer> resSet = new HashSet<>();
		Document doc = null;
		for(int i=0; i<hits.length; i++) {
			doc = indexSearcher.doc(hits[i].doc);
			resSet.add(Integer.parseInt(doc.get(fieldId)));
		}
		return resSet;
	}
	
	public static void main(String[] args) throws Exception{
		IdWordsIndex index = new IdWordsIndex(Global.pathPidAndRtreeIdWordsIndex);
//		index.openIndexWriter();
//		
//		int id = 0;
//		Set<Integer> wids = new HashSet<>();
//		
//		id = 0;
//		wids.add(2);
//		wids.add(4);
//		wids.add(7);
//		index.addDoc(id, wids);
//		wids.clear();
//		
//		id = 1;
//		wids.add(1);
//		wids.add(4);
//		wids.add(7);
//		index.addDoc(id, wids);
//		wids.clear();
//		
//		id = 2;
//		wids.add(1);
//		wids.add(6);
//		wids.add(7);
//		index.addDoc(id, wids);
//		wids.clear();
		
		index.openIndexReader();
		
		List<Integer> sWids = new ArrayList<>();
		sWids.add(663);
		sWids.add(155);
		sWids.add(924);
		sWids.add(38);
		System.out.println(index.searchWids(sWids));
		
		index.close();
		
	}
}
