package entity;

import java.util.ArrayList;
import java.util.List;

import spatialindex.spatialindex.Point;

public class QueryParams {
	public Point location = null;
	public List<String> sWords = new ArrayList<>();
	public int k = 1;
	public double epsilon = 0;
	public int minpts = 0;
	
	public QueryParams() {}

	public QueryParams(Point location, List<String> sWords, int k, double epsilon, int minpts) {
		super();
		this.location = location;
		this.sWords = sWords;
		this.k = k;
		this.epsilon = epsilon;
		this.minpts = minpts;
	}
	
}
