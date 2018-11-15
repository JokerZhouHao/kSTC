package test;

import java.nio.file.Paths;

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

import utility.Global;

public class LuceneScoreTest {
	public static void main(String[] args) throws Exception {
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

}
