package utility.io;

import java.io.BufferedReader;

import utility.Global;

public class FileComparator {
	/*
	 * 比较两个字符文件是否相同
	 */
	public static void comCharFile(String fp1, String fp2) throws Exception{
		BufferedReader br1 = IOUtility.getBR(fp1);
		BufferedReader br2 = IOUtility.getBR(fp2);
		int num = 1;
		String l1 = null;
		String l2 = null;
		while(true) {
			l1 = br1.readLine();
			l2 = br2.readLine();
			if(l1 == null) {
				if(l2 == null) {
					System.out.println("相同");
					break;
				} else {
					System.out.println("1在2前面，但2更大");
					break;
				}
			} else {
				if(l2 == null) {
					System.out.println("2在1前面，但1更大");
					break;
				} else {
					if(!l1.equals(l2)) {
						System.out.println("在第" + num + "行不相同");
						break;
					}
				}
			}
			num++;
		}
		br1.close();
		br2.close();
	}
	
	public static void main(String[] args) throws Exception{
		String orderPath1 = Global.outPath + "order_objects.obj_AlgEucDisBaseOpticsWu";
		String orderPath2 = Global.outPath + "order_objects.obj_AlgEucDisAdvancedOpticsWu";
		FileComparator.comCharFile(orderPath1, orderPath2);
	}
}
