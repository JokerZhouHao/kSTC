package utility.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class LuceneUtility {
	public static List<String> getTerms(String str) throws Exception{
		List<String> terms = new ArrayList();
		Analyzer sAnalyzer = new StandardAnalyzer();
		TokenStream ts = sAnalyzer.tokenStream("tt", str);
		CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
		ts.reset();
		int i = 0;
		while(ts.incrementToken()) {
			terms.add(termAttr.toString());
		}
		if(terms.isEmpty())	return null;
		sAnalyzer.close();
		return terms;
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println(LuceneUtility.getTerms("ERC talk talked c ddd erc good goods thing things do doing eee"));
		System.out.println(LuceneUtility.getTerms("AT"));
	}
	
}
