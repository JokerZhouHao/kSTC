package entity;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * SGPL cell
 * @author ZhouHao
 * @since 2018年11月23日
 */
public class Cell {
	private Integer id = Integer.MIN_VALUE;
	private Set<Integer> pids = null;
	
	private Cell() {
		pids = new HashSet<>();
	}
	
	public Cell(int id) {
		this(id, new HashSet<>());
	}
	
	public Cell(int id, Set<Integer> pids) {
		this.id = id;
		this.pids = pids;
	}
	
	public Cell(byte[] bytes) {
		loadFromBytes(bytes);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<Integer> getPids() {
		return pids;
	}

	public void setPids(Set<Integer> pids) {
		this.pids = pids;
	}
	
	public void addPid(int pid) {
		this.pids.add(pid);
	}
	
	public Set<Integer> merge(Cell cell){
		if(id == cell.id)	pids.addAll(cell.pids);
		return pids;
	}
	
	public int getBytesNum() {
		return 4 * (1 + 1 + pids.size());
	}
	
	public byte[] toBytes() {
		int num = this.getBytesNum();
		ByteBuffer bb = ByteBuffer.allocate(num);
		bb.putInt(id);
		bb.putInt(pids.size());
		for(int ii : pids) {
			bb.putInt(ii);
		}
		return bb.array();
	}
	
	private void loadFromBytes(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		id = bb.getInt();
		int size = bb.getInt();
		for(int i=0; i<size; i++)	pids.add(bb.getInt());
	}
	
	public static Cell loadFromBytes(ByteBuffer bb) {
		Cell cell = new Cell();
		cell.id = bb.getInt();
		int size = bb.getInt();
		for(int i=0; i<size; i++)	cell.pids.add(bb.getInt());
		return cell;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (id != other.id)
			return false;
		if (pids == null) {
			if (other.pids != null)
				return false;
		} else if (!pids.equals(other.pids))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cell [id=" + id + ", pids=" + pids + "]";
	}
	
	public static void main(String[] args) {
		int id = 1;
		Set<Integer> pids = new HashSet<>();
		pids.add(12);
		pids.add(32);
		
		Cell c1 = new Cell(id, pids);
		System.out.println(c1);
		
		Cell c2 = new Cell(c1.toBytes());
		System.out.println(c2);
	}
}
