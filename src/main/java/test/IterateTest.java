package test;

import java.util.Iterator;
import java.util.function.Consumer;

public class IterateTest<T> implements Iterable<T>{
	
	private Integer[] elementData = {0, 1, 2, 3, 4};
	private int cur = 0;
	
	@Override
	public Iterator iterator() {
		return new Iter();
	}
	
	class Iter implements Iterator<Integer>{

		@Override
		public boolean hasNext() {
			if(cur < elementData.length)	return true;
			return false;
		}

		@Override
		public Integer next() {
			if(hasNext())	return elementData[cur++];
			else return null;
		}

	}
	
	public static void main(String[] args) {
		IterateTest<Integer> it = new IterateTest();
		for(int i : it) {
			System.out.println(i);
		}
	}
}
