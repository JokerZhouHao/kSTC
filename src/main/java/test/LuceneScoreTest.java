package test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import index.CellidPidWordsIndex;
import index.optic.Term2PidNeighborsIndex;
import spatialindex.spatialindex.Point;
import utility.Global;

public class LuceneScoreTest {
	/**
	 * 测试基本评分方式
	 * @throws Exception
	 */
	public static void testBaseScore() throws Exception {
		Directory indexDir = FSDirectory.open(Paths.get(Global.pathTestIndex));
		IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
		iwc.setOpenMode(OpenMode.CREATE);
		
		IndexWriter writer = new IndexWriter(indexDir, iwc);
		
		Document doc1 = new Document();
		Document doc2 = new Document();
		Document doc3 = new Document();
		
		
		TextField f1 = new TextField("bookname","ab", Field.Store.YES);
		TextField f2 = new TextField("bookname","ab bc", Field.Store.YES);
		TextField f3 = new TextField("bookname","ab bc cd", Field.Store.YES);
		
		doc1.add(f1);
		doc2.add(f2);
		doc3.add(f3);
		
		writer.addDocument(doc1);
		writer.addDocument(doc2);
		writer.addDocument(doc3);
		
		writer.close();
		
		QueryParser queryAndWordsParser = new QueryParser("bookname", new StandardAnalyzer());
		queryAndWordsParser.setDefaultOperator(queryAndWordsParser.AND_OPERATOR);
		
		IndexReader indexReader = DirectoryReader.open(indexDir);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
//		indexSearcher.setSimilarity(new ClassicSimilarity());
		indexSearcher.setSimilarity(new BM25Similarity());
//		indexSearcher.setSimilarity(new BooleanSimilarity()); BM25Similarity MultiSimilarity ClassicSimilarity
		
//		TermQuery q = new TermQuery(new Term("bookname", "ab bc"));
		Query q = queryAndWordsParser.parse("ab");
		TopDocs hits = indexSearcher.search(q, Integer.MAX_VALUE);
		for(int i=0; i<hits.scoreDocs.length;i++){
			System.out.println(hits.scoreDocs[i]);
//			System.out.println(indexSearcher.explain(q, hits.id(i)));//
		}
		System.out.println();
		
		q = queryAndWordsParser.parse("bc");
		hits = indexSearcher.search(q, Integer.MAX_VALUE);
		for(int i=0; i<hits.scoreDocs.length;i++){
			System.out.println(hits.scoreDocs[i]);
		}
		System.out.println();
		
		q = queryAndWordsParser.parse("ab bc");
		hits = indexSearcher.search(q, Integer.MAX_VALUE);
		for(int i=0; i<hits.scoreDocs.length;i++){
			System.out.println(hits.scoreDocs[i]);
		}
		System.out.println();
		
		indexReader.close();
	}
	
	/**
	 * 测试doc长度是否影响评分
	 * @throws Exception
	 */
	public static void testDocLenInfluenceScore() throws Exception{
		Point[] allLocations = Global.allLocations;
		
		String path1 = Global.getPathCellidRtreeidOrPidWordsIndex(50, 6);
		String path2 = Global.getPathCellidRtreeidOrPidWordsIndex(50, 10);
		
		CellidPidWordsIndex index1 = new CellidPidWordsIndex(path1);
		index1.openIndexReader();
		CellidPidWordsIndex index2 = new CellidPidWordsIndex(path2);
		index2.openIndexReader();
		
		List<String> words = new ArrayList<>();
//		caters business
//		bars tuesday
		words.add("bars");
		words.add("tuesday");
		
		Map<Integer, Double> res1 = index1.searchWordsReScore(words, allLocations);
		Map<Integer, Double> res2 = index2.searchWordsReScore(words, allLocations);
		
		if(res1.size() == res2.size()) {
			for(Entry<Integer, Double> en : res1.entrySet()) {
				if(!en.getValue().equals(res2.get(en.getKey()))) {
					System.out.println(en.getKey() + " " + en.getValue() + "  -  " +
							en.getKey() + " " + res2.get(en.getKey()));
				}
//				if(res2.containsKey(en.getKey()) && res2.get(en.getKey()) == en.getValue())	continue;
//				else {
//					System.out.println(en.getKey() + " " + en.getValue() + "  -  " +
//										en.getKey() + " " + res2.get(en.getKey()));
//				}
			}
		} else {
			System.out.println(res1.size() + "   " + res2.size());
		}
		
		index1.close();
		index2.close();
	}
	
	public static void main(String[] args) throws Exception {
		testDocLenInfluenceScore();
	}

}
