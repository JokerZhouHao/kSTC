package precomputation.dataset.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import entity.Rectangle;
import index.IdWordsIndex;
import spatialindex.rtree.Node;
import utility.Global;
import utility.index.rtree.MRTree;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * process Genreate Files
 * @author ZhouHao
 * @since 2018年11月12日
 */
public class ProcessGenerateFiles {
	
	/**
	 * generate id_wids file
	 * @param pathText
	 * @param pathIdWids
	 * @param pathWids
	 * @throws Exception
	 */
	public static void generateIdWidsFile(String pathText, String pathIdWids, String pathWids) throws Exception{
		System.out.println("> start generate id_wids file");
		List<String> words = new ArrayList<>();
		Map<String, Integer> word2Id = new HashMap<>();
		
		String[] texts = FileLoader.loadText(pathText);
		BufferedWriter bw = IOUtility.getBW(pathIdWids);
		bw.write("                                          \n");
		
		Analyzer analyzer = new StandardAnalyzer();
		TokenStream ts = null;
		CharTermAttribute cta = null;
		
		int i;
		Integer wid = null;
		String word = null;
		Boolean sign = Boolean.TRUE;
		
		for(i=0; i<texts.length; i++) {
			if(null == texts[i]) {
				System.out.println(i + "is null");
				return;
			}
			
		}
		
		for(i=0; i<texts.length; i++) {
			bw.write(String.valueOf(i) + Global.delimiterLevel1);
			analyzer = new StandardAnalyzer();
			ts = analyzer.tokenStream("tt", texts[i]);
			cta = ts.addAttribute(CharTermAttribute.class);
			sign = Boolean.TRUE;
			ts.reset();
			while(ts.incrementToken()) {
				word = cta.toString();
				if(null == (wid = word2Id.get(word))) {
					wid = words.size();
					word2Id.put(word, wid);
					words.add(word);
				}
				if(!sign) {
					bw.write(Global.delimiterLevel2);
				} else sign = Boolean.FALSE;
				bw.write(String.valueOf(wid));
			}
			analyzer.close();
			bw.write('\n');
		}
		bw.close();
		IOUtility.setFirstLine(pathIdWids, Global.delimiterPound + String.valueOf(texts.length) + Global.delimiterPound + String.valueOf(words.size()));
		
		bw = IOUtility.getBW(pathWids);
		bw.write(Global.delimiterPound +  String.valueOf(words.size()) + "\n");
		for(i=0; i<words.size(); i++) {
			bw.write(String.valueOf(i) + Global.delimiterLevel1 + words.get(i) + '\n');
		}
		bw.close();
		System.out.println("> over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	
	/**
	 * normalize coordinate
	 * @param pathCoordFile
	 * @param rect
	 * @throws Exception
	 */
	public static void normalizedCoordFile(String pathCoordFile, Rectangle rect) throws Exception{
		System.out.println("> start normalized file " + pathCoordFile);
		double[][] coords = FileLoader.loadCoords(pathCoordFile);
		double spanLon = rect.eastNorthLont - rect.westSouthLont;
		double spanLat = rect.eastNorthLat - rect.westSouthLat;
		
		OrginalFileWriter ofw = new OrginalFileWriter(pathCoordFile + "[normalized]");
		ofw.writeLine(Global.delimiterPound + String.valueOf(coords.length));
		for(int i=0; i<coords.length; i++) {
			ofw.writeCoord(i, String.valueOf((coords[i][0]-rect.westSouthLont)/spanLon), String.valueOf((coords[i][1]-rect.westSouthLat)/spanLat));
		}
		ofw.close();
		System.out.println("> over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	/**
	 * building pid wids index
	 * @param index
	 * @param pidTexts
	 * @throws Exception
	 */
	public static void buildPidWordsIndex(IdWordsIndex index, Set<Integer>[] allWids) throws Exception{
		for(int i=0; i<allWids.length; i++) {
			index.addDoc(i, allWids[i]);
		}
	}
	
	/**
	 * building rtreeid words index
	 * @param index
	 * @param pidTexts
	 * @param rtree
	 * @param rtreeId
	 * @return
	 * @throws Exception
	 */
	public static Set<Integer> buildRtreeidWordsIndex(IdWordsIndex index, Set<Integer>[] allWids, MRTree rtree, int rtreeId) throws Exception{
		Set<Integer> wids = new HashSet<>();
		if(rtreeId == Integer.MIN_VALUE) {
			rtreeId = rtree.getRoot();
		}
		Node node = rtree.readNode(rtreeId);
		if(node.isLeaf()) {
			for(int child = 0; child < node.m_children; child++) {
				wids.addAll(allWids[node.m_pIdentifier[child]]);
			}
		} else {
			for(int child = 0; child < node.m_children; child++) {
				wids.addAll(buildRtreeidWordsIndex(index, allWids, rtree, node.m_pIdentifier[child]));
			}
		}
		index.addDoc(-node.m_identifier - 1, wids);
		return wids;
	}
	
	public static void buildPidAndRtreeIdWordsIndex(String pathIndex) throws Exception{
		System.out.println("> start build pid_rtreeid_words_index");
		IdWordsIndex index = new IdWordsIndex(pathIndex);
		index.openIndexWriter();
		Set<Integer>[] allWids = FileLoader.loadIdWids(Global.pathIdWids);
		ProcessGenerateFiles.buildPidWordsIndex(index, allWids);
		System.out.println("> over build pids");
		ProcessGenerateFiles.buildRtreeidWordsIndex(index, allWids, MRTree.getInstanceInDisk(), Integer.MIN_VALUE);
		System.out.println("> over build rtreeids");
		index.close();
		System.out.println("> over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	public static void buildRTree(String placefile, String treefile, int fanout, int buffersize, int pagesize)throws Exception{
		System.out.println("> start building rtree");
		MRTree.buildRTree(placefile, treefile, fanout, buffersize, pagesize);
		System.out.println("> over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	public static void main(String[] args) throws Exception{
		TimeUtility.init();
		
		// generateIdWidsFile
//		ProcessGenerateFiles.generateIdWidsFile(Global.pathIdText, Global.pathIdWids, Global.pathWidWord);
		
//		String pathCoords = Global.pathIdCoord + Global.subYelpBus1.toString();
//		ProcessGenerateFiles.normalizedCoordFile(pathCoords, Global.subYelpBus1);
		
//		String placeFile = Global.pathIdCoord + Global.signNormalized;
//		String treeFile = Global.rtreePath;
//		ProcessGenerateFiles.buildRTree(placeFile, treeFile, Global.rtreeFanout, Global.rtreeBufferSize, Global.rtreePageSize);
//		MRTree rtree = MRTree.getInstanceInDisk();
//		System.out.println(rtree.getTreeHeight());
//		System.out.println(rtree.getRoot());
		
//		PrintStream ps = new PrintStream(new File(Global.pathTestFile));
//		System.setErr(ps);
		
		String pathIndex = Global.pathPidAndRtreeIdWordsIndex;
		ProcessGenerateFiles.buildPidAndRtreeIdWordsIndex(pathIndex);
	}
}
