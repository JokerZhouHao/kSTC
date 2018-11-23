package entity;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CellCollection {
	private Map<Integer, Cell> id2Cell = new HashMap<>();
	
	public CellCollection() {}
	
	public CellCollection(byte[] bytes) {
		loadFromBytes(bytes);
	}
	
	public void add(Cell cell) {
		Cell tc = id2Cell.get(cell.getId());
		if(null != tc) tc.merge(cell);
		else id2Cell.put(cell.getId(), cell);
	}
	
	public Cell get(int id) {
		return id2Cell.get(id);
	}
	
	public Boolean contain(int id) {
		return id2Cell.containsKey(id);
	}
	
	public Map<Integer, Cell> getId2Cell() {
		return id2Cell;
	}

	public void setId2Cell(Map<Integer, Cell> id2Cell) {
		this.id2Cell = id2Cell;
	}

	public int getBytesNum() {
		int num = 0;
		num += 4;
		for(Entry<Integer, Cell> en : id2Cell.entrySet()) {
			num += en.getValue().getBytesNum();
		}
		return num;
	}
	
	public byte[] toBytes() {
		ByteBuffer bb = ByteBuffer.allocate(getBytesNum());
		bb.putInt(id2Cell.size());
		for(Entry<Integer, Cell> en : id2Cell.entrySet()) {
			bb.put(en.getValue().toBytes());
		}
		return bb.array();
	}
	
	private void loadFromBytes(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		Cell cell = null;
		int size = bb.getInt();
		for(int i=0; i<size; i++) {
			cell = Cell.loadFromBytes(bb);
			id2Cell.put(cell.getId(), cell);
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("CellCollection > \n");
		for(Entry<Integer, Cell> en : id2Cell.entrySet()) {
			sb.append(en.getValue().toString());
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
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
	}
}
