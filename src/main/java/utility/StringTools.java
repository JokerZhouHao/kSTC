package utility;

/**
 * provide some function to deal string
 * @author ZhouHao
 * @since 2018年10月30日
 */
public class StringTools {
	// eg: "ZhouHao" to "Zhou,Hao"
	public static String splitUpperString(String str) {
		StringBuffer sb = new StringBuffer();
		int start = 0;
		char c ;
		for(int i=0; i<str.length(); i++) {
			c = str.charAt(i);
			if(i != 0 && c >= 'A' && c <= 'Z') {
				sb.append(str.substring(start, i));
				sb.append(',');
				start = i;
			}
		}
		sb.append(str.substring(start));
		sb.append(',');
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(StringTools.splitUpperString("Zhou"));
	}
}
