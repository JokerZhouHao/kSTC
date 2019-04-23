package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import spatialindex.spatialindex.Point;
import utility.io.LuceneUtility;

public class QueryParams {
	public Point location = null;
	public List<String> sWords = new ArrayList<>();
	public int k = 1;
	public double epsilon = 0;
	public double xi = 0;
	public int minpts = 0;
	
	public QueryParams() {}

	public QueryParams(Point location, List<String> sWords, int k, double epsilonOrXi, int minpts) {
		super();
		this.location = location;
		this.sWords = sWords;
		this.k = k;
		this.epsilon = epsilonOrXi;
		this.xi = epsilonOrXi;
		this.minpts = minpts;
	}
	
	public QueryParams(Point location, String words, int numWord, int k, double epsilonOrXi, int minpts) throws Exception{
		super();
		this.location = location;
		this.sWords = this.getWords(words, numWord);
		this.k = k;
		this.epsilon = epsilonOrXi;
		this.xi = epsilonOrXi;
		this.minpts = minpts;
	}
	
	public QueryParams(double epsilonOrXi, int minpts) {
		this.epsilon = epsilonOrXi;
		this.xi = epsilonOrXi;
		this.minpts = minpts;
	}
	
	public List<String> getWords(String str, int numWord) throws Exception{
		List<String> terms = LuceneUtility.getTerms(str);
//		int num = new Random().nextInt(numWord<terms.size()?numWord:terms.size());
//		if(num==0)	num = new Random().nextInt(numWord<terms.size()?numWord:terms.size());
//		if(num==0)	num = new Random().nextInt(numWord<terms.size()?numWord:terms.size());
		
		int num = numWord<terms.size()?numWord:terms.size();
		
		List<String> words = new ArrayList<>();
		for(int i=terms.size()%num; i < terms.size(); i+=terms.size()/num) {
			words.add(terms.get(i));
		}
		return words;
	}
	
	@Override
	public String toString() {
		return "QueryParams [location=[" + String.valueOf(location.getCoord(0)) + ", " + String.valueOf(location.getCoord(1)) + "], sWords=" + sWords + ", k=" + k + ", epsilonOrXi=" + epsilon
				+ ", minpts=" + minpts + "]";
	}
	
}