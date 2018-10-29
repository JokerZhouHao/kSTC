package utility.indexlucene;

import java.io.File;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 
 * @author ZhouHao
 * provide base index functions
 * 2018/10/24
 */
public abstract  class AbstractLuceneIndex {
	protected IndexWriter indexWriter = null;
	protected Analyzer analyzer = null;
	protected IndexReader indexReader = null;
	protected IndexSearcher indexSearcher = null;
	protected String indexPath = null;
	
	public AbstractLuceneIndex() {}
	
	public AbstractLuceneIndex(String indexPath) {
		super();
		this.indexPath = indexPath;
	}
	
	public void setIndexPath(String indexPath) throws Exception{
		this.indexPath = indexPath;
		if(null != indexWriter) {
			indexWriter.close();
			openIndexWriter();
		}
		
		if(null != indexReader) {
			indexReader.close();
			openIndexReader();
		}
		
	}
	
	// openIndexWriter
	protected void openIndexWriter() throws Exception{
		analyzer = new StandardAnalyzer();
		if(!new File(indexPath).exists()) {
			new File(indexPath).mkdir();
		}
		Directory indexDir = FSDirectory.open(Paths.get(indexPath));
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		indexWriter = new IndexWriter(indexDir, iwc);
	}
	
	// openIndexReader
	public void openIndexReader() throws Exception{
		indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		indexSearcher = new IndexSearcher(indexReader);
	}
	
	// close
	public void close() throws Exception{
		if(indexReader!=null) {
			indexReader.close();
		}
		if(indexWriter != null) {
			indexWriter.close();
		}
	}
}
