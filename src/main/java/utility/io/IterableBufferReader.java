package utility.io;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Iterator;

import utility.Global;

/**
 * 
 * @author Monica
 * iterable to output line of file
 * @param <T>
 */
public class IterableBufferReader<T> extends BufferedReader implements Iterable<T> {
	
	public IterableBufferReader(Reader in) {
		super(in);
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iter<T>();
	}
	
	class Iter<T> implements Iterator<T>{
		
		T line = null;
		
		@Override
		public boolean hasNext() {
			try {
				line = (T)readLine();
				if(null == line)	close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
			
			if(null != line)	return true;
			return false;
		}

		@Override
		public T next() {
			return line;
		}
	}
	
	public static void main(String[] args) throws Exception{
		for(String st : IOUtility.getIBW(Global.inputPath + "yelp_academic_dataset_business.json")) {
			System.out.println(st);
		}
	}
	
}
