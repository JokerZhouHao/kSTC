package test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class LuceneAnalyzeTest {
	public static void main(String[] args) throws Exception{
		Analyzer analyzer = new StandardAnalyzer();
		TokenStream ts = analyzer.tokenStream("tt", "Business,Accepts,Credit,Cards,lot,street,Business,Parking,Restaurants,Good,For,Groups,Restaurants,Price,Range2,Wheelchair,Accessible,Restaurants,Take,Out,Caters,Good,For,Kids,Restaurants,Attire,Dogs,Allowed,Outdoor,Seating,Chicken Wings, Burgers, Caterers, Street Vendors, Barbeque, Food Trucks, Food, Restaurants, Event Planning & ServicesHenderson17:0-23:0,Friday,17:0-23:0,Sunday,17:0-23:0,Saturday,CK'S BBQ & Catering890024.5NV");
		CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
		ts.reset();
		int i = 0;
		while(ts.incrementToken()) {
			System.out.println((++i) + " : " + termAttr);
		}
		analyzer.close();
	}
}
