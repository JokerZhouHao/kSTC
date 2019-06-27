package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import index.optic.NeighborsNode;
import utility.Global;
import utility.MLog;
import utility.RandomNumGenerator;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * 比较word的neighbors用binary或object stream转化的速度
 * @author ZhouHao
 * @since 2019年6月26日
 */
public class PNeisTransSpeedTest {
	/*******************  path *****************/
	public static String path(int numByte, Boolean isBin) {
		if(isBin)	return Global.outPath + "Test_PNeisTransSpeed_" + numByte + ".bin";
		else return Global.outPath + "Test_PNeisTransSpeed_" + numByte + ".object";
	}
	
	/*******************  binary *****************/
	public static void toBinary(String path, Map<Integer, List<NeighborsNode>> pid2Nodes) throws Exception{
		MLog.log("start write file " + path + " . . . ");
		long startTime = System.currentTimeMillis();
		DataOutputStream dos = IOUtility.getDos(path);
		dos.writeInt(pid2Nodes.size());
		for(Entry<Integer, List<NeighborsNode>> en : pid2Nodes.entrySet()) {
			dos.writeInt(en.getKey());
			dos.writeInt(en.getValue().size());
			List<NeighborsNode> li = en.getValue();
			for(NeighborsNode f : li) {
				dos.writeInt(f.id);
				dos.writeDouble(f.disToCenter);
			}
		}
		dos.close();
		MLog.log("over, spend time : " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	public static Map<Integer, List<NeighborsNode>> loadBinary(String path) throws Exception {
		DataInputStream dis = IOUtility.getDis(path);
		Map<Integer, List<NeighborsNode>> pid2Nodes = new HashMap<>();
		int num = dis.readInt();
		for(int i=0; i<num; i++) {
			int pid = dis.readInt();
			List<NeighborsNode> li = new ArrayList<>();
			int size = dis.readInt();
			for(int j=0; j<size; j++) {
				li.add(new NeighborsNode(dis.readInt(), dis.readDouble()));
			}
			pid2Nodes.put(pid, li);
		}
		dis.close();
		return pid2Nodes;
	}
	
	/*******************  object *****************/
	public static void toObject(String path, Map<Integer, List<NeighborsNode>> pid2Nodes) throws Exception{
		MLog.log("start write file " + path + " . . . ");
		long startTime = System.currentTimeMillis();
		ObjectOutputStream oos = IOUtility.getOOS(path);
		oos.writeObject(pid2Nodes);
		oos.close();
		MLog.log("over, spend time : " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	public static Map<Integer, List<NeighborsNode>> loadObject(String path) throws Exception {
		ObjectInputStream ois = IOUtility.getOIS(path);
		Map<Integer, List<NeighborsNode>> pid2Nodes = (Map<Integer, List<NeighborsNode>>)ois.readObject();
		ois.close();
		return pid2Nodes;
	}
	
	/*******************  generate pid2Nodes *****************/
	public static Map<Integer, List<NeighborsNode>> generate(int numByte){
		RandomNumGenerator genPid = new RandomNumGenerator(1, 1000000000);
		RandomNumGenerator genSize = new RandomNumGenerator(1, 200);
		RandomNumGenerator genNeiPid = new RandomNumGenerator(1, 10000000);
		Map<Integer, List<NeighborsNode>> pid2Nodes = new HashMap<>();
		numByte /= 4;
		int curNumByte = 1;
		while(curNumByte < numByte) {
			int pid = genPid.getRandomInt();
			int size = genSize.getRandomInt();
			List<NeighborsNode> nei = new ArrayList<>();
			curNumByte += 1 + 1 + 3 * size;
			for(int i=0; i<size; i++) {
				nei.add(new NeighborsNode(genNeiPid.getRandomInt(), RandomNumGenerator.getRandomDouble()));
			}
			pid2Nodes.put(pid, nei);
		}
		return pid2Nodes;
	}
	
	/*******************  test *****************/
	public static void testBin(int numByte) throws Exception {
		String path = path(numByte, true);
		long startTime = System.currentTimeMillis();
		MLog.log("start  testBin . . . ");
		loadBinary(path);
		MLog.log("over, spend time : " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	public static void testObject(int numByte) throws Exception {
		String path = path(numByte, false);
		long startTime = System.currentTimeMillis();
		MLog.log("start testObject . . . ");
		loadObject(path);
		MLog.log("over, spend time : " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	public static void main(String[] args) throws Exception {
		int numByte = 400000000;
		String pathBin = path(numByte, true);
		String pathObj = path(numByte, false);
		
		/*** 写文件 ****/
//		Map<Integer, List<NeighborsNode>> pidNodes = generate(numByte);
//		toBinary(pathBin, pidNodes);
//		toObject(pathObj, pidNodes);
		
		/*** 测试 ***/
		testBin(numByte);
		testObject(numByte);
	}
}

















