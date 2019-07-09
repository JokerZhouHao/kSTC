package utility;

public class SystemInfoUtility {
	public static String systemName() {
		return System.getProperty("os.name").toLowerCase();
	}
	
	public static Boolean isWindow() {
		if(systemName().contains("windows"))	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public static Boolean isLinux() {
		if(systemName().contains("linux"))	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(systemName());
	}
}
