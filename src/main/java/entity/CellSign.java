package entity;

public class CellSign {
	private int id = 0;
	private Boolean sign = Boolean.FALSE;
	public CellSign(int id, Boolean sign) {
		super();
		this.id = id;
		this.sign = sign;
	}
	public int getId() {
		return id;
	}
	public Boolean getSign() {
		return sign;
	}
	@Override
	public String toString() {
		return "CellSign [id=" + id + ", sign=" + sign + "]";
	}
}
