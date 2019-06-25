package index;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

import entity.Cell;
import entity.CellCollection;
import utility.Global;
import utility.indexlucene.AbstractLuceneIndex;

public class Term2CellColIndex extends AbstractLuceneIndex{
	private static final String fieldTerm = "term";
	private static final String fieldCellCol = "cellCol";
	private  QueryParser queryParser = null;
	
	public Term2CellColIndex(String indexPath) throws Exception{
		super(indexPath);
		queryParser = new QueryParser(fieldTerm, analyzer);
	}
	
	public void addDoc(String term, CellCollection cellCol) throws Exception{
		Document doc = new Document();
		doc.add(new StringField(fieldTerm, term, Store.NO));
		doc.add(new StoredField(fieldCellCol, new BytesRef(cellCol.toBytes())));
		indexWriter.addDocument(doc);
	}
	
	public CellCollection searchTerm(String term) throws Exception{
		TopDocs results = indexSearcher.search(queryParser.parse(term), 1);
		ScoreDoc[] hits = results.scoreDocs;
		
		if(0 == hits.length)	return null;
		return new CellCollection(indexSearcher.doc(hits[0].doc).getBinaryValue(fieldCellCol).bytes);
	}
	
	public static void main(String[] args) throws Exception{
		int id = 1;
		Set<Integer> pids = new HashSet<>();
		pids.add(12);
		pids.add(32);
		Cell c1 = new Cell(id, pids);
		
		id = 1;
		pids = new HashSet<>();
		pids.add(22);
		pids.add(23);
		Cell c2 = new Cell(id, pids);
		
		CellCollection cc = new CellCollection();
		cc.add(c1);
		cc.add(c2);
		System.out.println(cc);
		
		CellCollection cc1 = new CellCollection(cc.toBytes());
		System.out.println(cc1);
		
		Term2CellColIndex t2c = new Term2CellColIndex(Global.pathTestIndex);
		t2c.openIndexWriter();
		t2c.addDoc("zhou", cc);
		t2c.addDoc("hao", cc1);;
		t2c.close();
		
		t2c.openIndexReader();
		System.out.println(t2c.searchTerm("hao"));
		t2c.close();
	}
}
