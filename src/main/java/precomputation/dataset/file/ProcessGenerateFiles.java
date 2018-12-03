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
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import entity.Cell;
import entity.CellCollection;
import entity.KSortedCollection;
import entity.Rectangle;
import entity.SGPLInfo;
import index.CellidPidWordsIndex;
import index.IdWordsIndex;
import index.Term2CellColIndex;
import spatialindex.rtree.Node;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.index.rtree.MRTree;
import utility.io.IOUtility;
import utility.io.LuceneUtility;
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
	 * generate id_terms file
	 * @param pathIdTerms
	 * @throws Exception
	 */
	public static void generateIdTermsFile(String pathIdTerms) throws Exception{
		System.out.println("> start generate file " + pathIdTerms);
		String[] allTxts = FileLoader.loadText(Global.pathIdText);
		BufferedWriter bw = IOUtility.getBW(pathIdTerms);
		bw.write(Global.delimiterPound + String.valueOf(allTxts.length) + "\n");
		Set<String> terms = new HashSet<>();
		for(int i=0; i<allTxts.length; i++) {
			terms.clear();
			bw.write(String.valueOf(i));
			bw.write(Global.delimiterLevel1);
			terms.addAll(LuceneUtility.getTerms(allTxts[i]));
			for(String st : terms) {
				bw.write(st);
				bw.write(Global.delimiterSpace);
			}
			bw.write('\n');
		}
		bw.close();
		System.out.println("> Over.");
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
	 * buiding rtree
	 * @param placefile
	 * @param treefile
	 * @param fanout
	 * @param buffersize
	 * @param pagesize
	 * @throws Exception
	 */
	public static void buildRTree(String placefile, String treefile, int fanout, int buffersize, int pagesize)throws Exception{
		System.out.println("> start building rtree");
		MRTree.buildRTree(placefile, treefile, fanout, buffersize, pagesize);
		System.out.println("> over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	/**
	 * building pid wids index
	 * @param index
	 * @param texts
	 * @throws Exception
	 */
	public static void buildPidWordsIndex(IdWordsIndex index, String[] texts ) throws Exception{
		for(int i=0; i<texts.length; i++) {
			index.addWordsDoc(i, texts[i]);
		}
	}
	
	/**
	 * building rtreeid words index
	 * @param index
	 * @param texts
	 * @param rtree
	 * @param rtreeId
	 * @return
	 * @throws Exception
	 */
	public static StringBuffer buildRtreeidWordsIndex(IdWordsIndex index, String[] texts , MRTree rtree, int rtreeId) throws Exception{
		StringBuffer sb = new StringBuffer();
		if(rtreeId == Integer.MIN_VALUE) {
			rtreeId = rtree.getRoot();
		}
		Node node = rtree.readNode(rtreeId);
		if(node.isLeaf()) {
			for(int child = 0; child < node.m_children; child++) {
				sb.append(" ");
				sb.append(texts[node.m_pIdentifier[child]]);
			}
		} else {
			for(int child = 0; child < node.m_children; child++) {
				sb.append(" ");
				sb.append(buildRtreeidWordsIndex(index, texts, rtree, node.m_pIdentifier[child]));
			}
		}
		index.addWordsDoc(-node.m_identifier - 1, sb.toString());
		return sb;
	}
	
	/**
	 * building pid and rtree id words index
	 * @param pathIndex
	 * @throws Exception
	 */
	public static void buildPidAndRtreeIdWordsIndex(String pathIndex) throws Exception{
		System.out.println("> start build " + pathIndex);
		IdWordsIndex index = new IdWordsIndex(pathIndex);
		index.openIndexWriter();
		String[] texts = FileLoader.loadText(Global.pathIdText);
		ProcessGenerateFiles.buildPidWordsIndex(index, texts);
		System.out.println("> over build pids");
		ProcessGenerateFiles.buildRtreeidWordsIndex(index, texts, MRTree.getInstanceInDisk(), Integer.MIN_VALUE);
		System.out.println("> over build rtreeids");
		index.close();
		System.out.println("> over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	/**
	 * build Cellid Pid Words Index
	 * @param pathIndex
	 * @throws Exception
	 */
	public static void buildCellidPidWordsIndex(String pathIndex) throws Exception{
		System.out.println("> start build " + pathIndex);
		SGPLInfo sInfo = Global.sgplInfo;
		CellidPidWordsIndex index = new CellidPidWordsIndex(pathIndex);
		index.openIndexWriter();
		String[] texts = FileLoader.loadText(Global.pathIdText);
		Point[] points = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
		for(int i=0; i<texts.length; i++) {
			index.addWordsDoc(sInfo.getZOrderId(points[i].m_pCoords), i, texts[i]);
		}
		index.close();
		System.out.println("> over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	
	/**
	 * build Pid Words Index
	 * @param sInfo
	 * @param index
	 * @param points
	 * @param texts
	 * @throws Exception
	 */
	public static void buildPidWordsIndex(SGPLInfo sInfo, CellidPidWordsIndex index, Point[] points, String[] texts ) throws Exception{
		for(int i=0; i<texts.length; i++) {
			index.addWordsDoc(sInfo.getZOrderId(points[i].m_pCoords), i, texts[i]);
		}
	}
	
	/**
	 * build Rtreeid Words Index
	 * @param sInfo
	 * @param index
	 * @param texts
	 * @param rtree
	 * @param rtreeId
	 * @return
	 * @throws Exception
	 */
	public static StringBuffer buildRtreeidWordsIndex(SGPLInfo sInfo, CellidPidWordsIndex index, String[] texts , MRTree rtree, int rtreeId) throws Exception{
		StringBuffer sb = new StringBuffer();
		if(rtreeId == Integer.MIN_VALUE) {
			rtreeId = rtree.getRoot();
		}
		Node node = rtree.readNode(rtreeId);
		if(node.isLeaf()) {
			for(int child = 0; child < node.m_children; child++) {
				sb.append(" ");
				sb.append(texts[node.m_pIdentifier[child]]);
			}
		} else {
			for(int child = 0; child < node.m_children; child++) {
				sb.append(" ");
				sb.append(buildRtreeidWordsIndex(sInfo, index, texts, rtree, node.m_pIdentifier[child]));
			}
		}
		index.addWordsDoc(CellidPidWordsIndex.signRtreeNode, -node.m_identifier - 1, sb.toString());
		return sb;
	}
	
	public static void buildCellidRtreeidOrPidWordsIndex(String pathIndex) throws Exception{
		System.out.println("> start build " + pathIndex);
		SGPLInfo sInfo = Global.sgplInfo;
		CellidPidWordsIndex index = new CellidPidWordsIndex(pathIndex);
		index.openIndexWriter();
		String[] texts = FileLoader.loadText(Global.pathIdText);
		Point[] points = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
		ProcessGenerateFiles.buildPidWordsIndex(sInfo, index, points, texts);
		System.out.println("> over build pids");
		ProcessGenerateFiles.buildRtreeidWordsIndex(sInfo, index, texts, MRTree.getInstanceInDisk(), Integer.MIN_VALUE);
		System.out.println("> over build rtreeids");
		index.close();
		System.out.println("> over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	/**
	 * build Term CellCol Index
	 * @param pathIndex
	 * @throws Exception
	 */
	public static void buildTermCellColIndex(String pathIndex) throws Exception{
		System.out.println("> start " + pathIndex + " . . . ");
		double[][] coords = FileLoader.loadCoords(Global.pathIdCoord + Global.signNormalized);
		List<String>[] allTerms = FileLoader.loadTerms(Global.pathIdTerms);
		SGPLInfo sInfo = Global.sgplInfo;
		Map<String, CellCollection> allTerm2CellC = new HashMap<>();
		CellCollection cellC = null;
		Cell cell = null;
		int cellId = 0;
		for(int i=0; i<allTerms.length; i++) {
			cellId = sInfo.getZOrderId(coords[i]);
			for(String st : allTerms[i]) {
				if(null == (cellC = allTerm2CellC.get(st))) {
					cellC = new CellCollection();
					allTerm2CellC.put(st, cellC);
				}
				if(null == (cell = cellC.get(cellId))) {
					cell = new Cell(cellId);
					cellC.add(cell);
				}
				cell.addPid(i);
			}
		}
		
		Term2CellColIndex index = new Term2CellColIndex(pathIndex);
		index.openIndexWriter();
		for(Entry<String, CellCollection> en : allTerm2CellC.entrySet()) {
			index.addDoc(en.getKey(), en.getValue());
		}
		index.close();
		System.out.println("> Over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	
	
	/**
	 * generate K Neighbor Dis File
	 * @param filePath
	 * @param k
	 * @throws Exception
	 */
	public static void generateKNeighborDisFile(String filePath, int k) throws Exception{
		System.out.println("> start build " + filePath);
		Point[] allPoints = FileLoader.loadPoints(Global.pathIdCoord + Global.signNormalized);
		MRTree rtree = MRTree.getInstanceInDisk();
		List<entity.Node> neighbors = null;
		KSortedCollection<entity.Node> tNodes = null;
		KSortedCollection<entity.Node> allSortedNodes = new KSortedCollection<>(Integer.MAX_VALUE);
		double radius = 0.00002;
		for(int i=0; i<allPoints.length; i++) {
			radius = 0.00002;
			neighbors = null;
			while(null == neighbors || neighbors.size() < k) {
				radius *= 5;
				neighbors = rtree.rangeQuery(allPoints[i], radius, allPoints);
			}
			tNodes = new KSortedCollection<entity.Node>(k, neighbors);
			allSortedNodes.add(new entity.Node(i, allPoints[i], tNodes.getK().distance, 0.0));
		}
		
		BufferedWriter bw = IOUtility.getBW(filePath);
		bw.write(Global.delimiterPound + String.valueOf(allSortedNodes.size()) + "\n");
		for(entity.Node nd : allSortedNodes.toList()) {
			bw.write(String.valueOf(nd.distance));
			bw.write(Global.delimiterLevel1);
			bw.write(String.valueOf(nd.id));
			bw.write('\n');
		}
		bw.close();
		System.out.println("> over, spend time : " + TimeUtility.getGlobalSpendTime());
	}
	
	public static void main(String[] args) throws Exception{
		/*********************  alg ecu dis dbscan starting *******************************/ 
		/* generateIdWidsFile */
//		ProcessGenerateFiles.generateIdWidsFile(Global.pathIdText, Global.pathIdWids, Global.pathWidWord);
		
//		String pathCoords = Global.pathIdCoord + Global.subYelpBus1.toString();
//		ProcessGenerateFiles.normalizedCoordFile(pathCoords, Global.subYelpBus1);
		
		/* generate id_terms file */
//		String pathIdTerms = Global.pathIdTerms;
//		ProcessGenerateFiles.generateIdTermsFile(pathIdTerms);
		
		/* generate rtree */
//		String placeFile = Global.pathIdCoord + Global.signNormalized;
////		String placeFile = Global.pathIdCoord;
//		String treeFile = Global.rtreePath;
//		ProcessGenerateFiles.buildRTree(placeFile, treeFile, Global.rtreeFanout, Global.rtreeBufferSize, Global.rtreePageSize);
//		MRTree rtree = MRTree.getInstanceInDisk();
//		System.out.println(rtree.getTreeHeight());
//		System.out.println(rtree.getRoot());
		
		/* set error stream */
//		PrintStream ps = new PrintStream(new File(Global.pathTestFile));
//		System.setErr(ps);
		
		/* building pid and rtreeid words index */
//		String pathIndex = Global.pathPidAndRtreeIdWordsIndex;
//		ProcessGenerateFiles.buildPidAndRtreeIdWordsIndex(pathIndex);
		
		/* building cellidpid words index */
//		String pathCellidpidWordsIndex = Global.pathCellidPidWordsIndex;
//		ProcessGenerateFiles.buildCellidPidWordsIndex(pathCellidpidWordsIndex);
		
		/* building cellid rtreeid pid words index */
//		String pathCellidRtreeidOrPidWordsIndex = Global.pathCellidRtreeidOrPidWordsIndex;
//		ProcessGenerateFiles.buildCellidRtreeidOrPidWordsIndex(pathCellidRtreeidOrPidWordsIndex);
		
		/* building term_cellCol_index */
//		String pathTerm2CellCIndex = Global.pathTerm2CellColIndex;
//		ProcessGenerateFiles.buildTermCellColIndex(pathTerm2CellCIndex);
//		Term2CellColIndex t2CIndex = new Term2CellColIndex(pathTerm2CellCIndex);
//		t2CIndex.openIndexReader();
//		System.out.println(t2CIndex.searchTerm("f"));
//		t2CIndex.close();
		/*********************  alg ecu dis dbscan ending *******************************/ 
		
		/* building k neighbor dis file */
		int k = 20;
		String pathKNeighborDis = Global.outPath + String.valueOf(k) + Global.signKNeighborDis;
		ProcessGenerateFiles.generateKNeighborDisFile(pathKNeighborDis, k);
	}
}
