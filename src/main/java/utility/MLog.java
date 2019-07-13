package utility;

public class MLog {
	public static synchronized void log(String info) {
		System.out.println("[" + TimeUtility.getTime() + "] " + info);
	}
	
	public static synchronized void logNoln(String info) {
		System.out.print("[" + TimeUtility.getTime() + "] " + info);
	}
	
	public static synchronized void blackLine() {
		System.out.println();
	}
	
	public static void main(String[] args) {
		MLog.log("zhou");
		System.out.println(System.getProperty("os.name").toLowerCase());
	}
}
