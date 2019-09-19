package fpgrowth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.QueryParams;
import entity.Term2Fre;
import entity.fastrange.Cellid2Nodes;
import index.CellidPidWordsIndex;
import precomputation.dataset.file.FileLoader;
import sample.SampleChooser;
import spatialindex.spatialindex.Point;
import utility.Global;
import utility.io.LuceneUtility;


/**
 * FP-Growth Procedure
 * 
 * @author Yue Shang   github: https://github.com/mogumogu2333/FPGrowth
 *
 */
public class FPGrow {
	FPNode root;
	int min_sup = 1;
	private Map<List<String>, Integer> frequentMap = new HashMap<List<String>, Integer>();
	
	public FPGrow(int min_sup) {
		// 只要频率至少为min_sup的词
		this.min_sup = min_sup;
	}
	
	public void FPGrowthAlgorithm(List<List<String>> transactions){
		//-- This is the first Data Scan
		HashMap<String, Integer> itemCount = getFreqCount(transactions);
		System.out.println("Finish 1st Data Scan....");
		//Sort items according to itemCount
		for(List<String> transaction: transactions){
			Collections.sort(transaction, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					// TODO Auto-generated method stub
					if(itemCount.get(o1)>itemCount.get(o2))return -1;
					else if(itemCount.get(o1)<itemCount.get(o2)) return 1;
					return 0;
				}
			});
		}
		
		//build tree
		FPGrowth(transactions, null);
	}
	
	
	
	
	/**
	 * 
	 * @param transactions
	 * @param postModel
	 */
	public void FPGrowth(List<List<String>> transactions, List<String> postModel){
		Map<String, Integer> itemCount = getFreqCount(transactions);
		Map<String, FPNode> headerTable = new HashMap<>();
		
		// set header table
		for(Entry<String, Integer> entry:itemCount.entrySet()){
			String itemName = entry.getKey();
			Integer count = entry.getValue();
			
			//check the min_support
			if(count>=this.min_sup){
				FPNode node = new FPNode(itemName);
				node.support = count;
				headerTable.put(itemName, node);
			}
		}
		
		FPNode root = buildTree(transactions, itemCount, headerTable);
		
		if(root==null) return;

		if(root.children==null || root.children.size()==0) return;
		
		//optimization for single path
		if(isSingleBranch(root)){
			ArrayList<FPNode> path = new ArrayList<>();
			FPNode curr = root;
			while(curr.children!=null && curr.children.size()>0){
				String childName = curr.children.keySet().iterator().next();
				curr = curr.children.get(childName);
				path.add(curr);
			}
			
			List<List<FPNode>> combinations = new ArrayList<>();
			getCombinations(path, combinations);
			
			for(List<FPNode> combine : combinations){
				int supp = 0;
				List<String> rule = new ArrayList<>();
				for(FPNode node : combine){
					rule.add(node.itemName);
					supp = node.support;
				}
				if(postModel!=null){
					rule.addAll(postModel);
				}
				
				frequentMap.put(rule, supp);
			}
			
			return;
		}
		
		for(FPNode header : headerTable.values()){
			
			List<String> rule = new ArrayList<>();
			rule.add(header.itemName);// header is item >= min_support
			
			if (postModel != null) {
                rule.addAll(postModel);
            }
			
			frequentMap.put(rule, header.support);
			
			List<String> newPostPattern = new ArrayList<>();
			newPostPattern.add(header.itemName);
            if (postModel != null) {
                newPostPattern.addAll(postModel);
            }
            
            //new conditional pattern base
            List<List<String>> newCPB = new LinkedList<List<String>>();
            FPNode nextNode = header;
			while((nextNode = nextNode.next)!=null){
				int leaf_supp = nextNode.support;
				
				//get the path from root to this node
				LinkedList<String> path = new LinkedList<>();
				FPNode parent = nextNode;
				while(!(parent = parent.parent).itemName.equals("ROOT")){
					path.push(parent.itemName);
				}
				if(path.size()==0)continue;
				
				while(leaf_supp-- >0){
					newCPB.add(path);
				}
			}
			FPGrowth(newCPB, newPostPattern);
		}
	}
	
	/**
	 * Generate all the possible combinations for a given item set. Use bitmap
	 * @param path
	 * @param combinations
	 */
	private void getCombinations(ArrayList<FPNode> path, List<List<FPNode>> combinations){
		if(path==null || path.size()==0)return;
		int length = path.size();
		for(int i = 1;i<Math.pow(2, length);i++){
			String bitmap = Integer.toBinaryString(i);
			List<FPNode> combine = new ArrayList<>();
			for(int j = 0;j<bitmap.length();j++){
				if(bitmap.charAt(j)=='1'){
					combine.add(path.get(length-bitmap.length()+j));
				}
			}
			combinations.add(combine);
		}
	}
	
	
	private FPNode buildTree(List<List<String>> transactions, final Map<String, Integer> itemCount, final Map<String, FPNode> headerTable){
		FPNode root = new FPNode("ROOT");
		root.parent = null;
		
		for(List<String> transaction : transactions){
			FPNode prev = root;
			HashMap<String, FPNode> children = prev.children;
			
			for(String itemName:transaction){
				//not in headerTable, then not qualify the min support.
				if(!headerTable.containsKey(itemName))continue;
				
				FPNode t;
				if(children.containsKey(itemName)){
					children.get(itemName).support++;
					t = children.get(itemName);
				}
				else{
					t = new FPNode(itemName);
					t.parent = prev;
					children.put(itemName, t);
					
					//add to header
					FPNode header = headerTable.get(itemName);
					if(header!=null){
						header.attach(t);
					}
				}
				prev = t;
				children = t.children;
			}
		}
		
		return root;
		
	}
	
	 private boolean isSingleBranch(FPNode root) {
	        boolean rect = true;
	        while (root.children != null && root.children.size()>0) {
	            if (root.children.size() > 1) {
	                rect = false;
	                break;
	            }
	            String childName = root.children.keySet().iterator().next();
	            root = root.children.get(childName);
	        }
	        return rect;
	    }
	

	
	private HashMap<String, Integer> getFreqCount(List<List<String>> transactions){
		HashMap<String, Integer> itemCount = new HashMap<String, Integer>();
		for(List<String> transac: transactions){
			for(String item: transac){
				if(itemCount.containsKey(item)){
					int count = itemCount.get(item);
					itemCount.put(item, ++count);
				}
				else{
					itemCount.put(item, 1);
				}
			}
		}
		
		return itemCount;
	}
	
	/**
	 * Load census data
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public List<List<String>> loadTransactions(String filename) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		List<List<String>> transactions = new ArrayList<>();
		
		//remove gain= loss=
		Pattern pattern = Pattern.compile("gain=\\w*|loss=\\w*");
		
		String newline;
		while((newline = br.readLine())!=null){
			Matcher matcher = pattern.matcher(newline);
			newline = matcher.replaceAll("");
			newline = newline.replaceAll("( )+", " "); //remove multiple spaces
			String[] items = newline.split(" ");
			transactions.add(new ArrayList<String>(Arrays.asList(items)));
		}
		br.close();
		
		return transactions;
	}
	
	
	public List<List<String>> loadTransactionsByTxts(String pathIdText, Set<String> filter) throws Exception{
		List<List<String>> transactions = new ArrayList<>();
		
		String[] txts = FileLoader.loadText(pathIdText);
		for(String st : txts) {
			if(st == null)	continue;
			Set<String> words = new HashSet(LuceneUtility.getTerms(st));
			List<String> nwords = new ArrayList<>();
			for(String w : words) {
				if(null == filter || !filter.contains(w))
					nwords.add(w);
			}
			if(!nwords.isEmpty()) {
				transactions.add(nwords);
			}
		}
		return transactions;
	}
	
	
	
	/**
	 * For test, print headers
	 * @param headers
	 */
	private void testHeadTable(HashMap<String, FPNode> headers){
		if(headers==null) return;
		for(Entry<String, FPNode> entry : headers.entrySet()){
			String headerName = entry.getKey();
			int supp = headers.get(headerName).support;
			StringBuffer buff = new StringBuffer();
			FPNode currPointer = entry.getValue().next;
			while(currPointer!=null){
				buff.append(currPointer.itemName+"("+currPointer.support+")---->");
				currPointer = currPointer.next;
			}
			
			System.out.println(headerName+"("+supp+") : "+buff.toString());
		}
	}
	
	/**
	 * test only
	 * @param minLength
	 */
	public void printResult(int minLength){
		for(Entry<List<String>, Integer> entry : this.frequentMap.entrySet()){
			List<String> rule = entry.getKey();
			if(rule.size() < minLength)continue;
			Integer support = entry.getValue();
			System.out.println(Arrays.toString(rule.toArray())+"\t\t"+support);
		}
	}
	
	/**
	 * 该算法的实现（可能是bug）中找出的组合中有重复词
	 * @param length
	 * @param filter
	 * @return
	 */
	public List<Term2Fre> getResult(int length, Set<String> filter) {
		List<Term2Fre> tfs = new ArrayList<>();
		Set<String> rec = new HashSet<>();
		boolean hasRepeat = false;
		boolean isAllTooLong = true;
		for(Entry<List<String>, Integer> entry : this.frequentMap.entrySet()){
			List<String> rule = entry.getKey();
			if(rule.size() != length)	continue;
			Integer support = entry.getValue();
			
			// 去重
			rec.clear();
			hasRepeat = false;
			for(String r : rule) {
				if(rec.contains(r)) {
					hasRepeat = true;
					break;
				}
				else rec.add(r);
			}
			if(hasRepeat)	continue;
			
			// 排除组合中每个词的index 都是too long 的组合
			isAllTooLong = true;
			for(String r : rule) {
				if(filter == null || !filter.contains(r)) {
					isAllTooLong = false;
					break;
				}
			}
			if(isAllTooLong)	continue;
			
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<rule.size() - 1; i++) {
				sb.append(rule.get(i));
				sb.append(' ');
			}
			sb.append(rule.get(rule.size() - 1));
			tfs.add(new Term2Fre(support, sb.toString()));
		}
		return tfs;
	}
	
	private static void testGroupFre() throws Exception{
		
//		goods retail vehicles autos: 242865
//		retail motor vehicles autos: 242865
//		goods retail food beverages: 258890
//		goods retail motor vehicles: 242907
//		building trades construction contractor: 386282
//		goods retail motor autos: 242865
//		goods retail home garden: 245296
//		goods motor vehicles autos: 242865
		
		Set<String> terms = new HashSet<>();
		terms.add("goods");
		terms.add("retail");
		terms.add("vehicles");
		terms.add("autos");
		
		QueryParams qParams = new QueryParams();
		qParams.location = new Point(new double[]{0.0, 0.0});
		qParams.sWords = new ArrayList<>(terms);
		CellidPidWordsIndex cellidWIndex = null;
		String path =  Global.getPathCellidRtreeidOrPidWordsIndex(300, 12);
		cellidWIndex = new CellidPidWordsIndex(path);
		cellidWIndex.openIndexReader();
		
		Cellid2Nodes cid2nds = cellidWIndex.searchWordsReCellid2Nodes(qParams, Global.allLocations);
		System.out.println(cid2nds.pNodes.size());
		
		
		cellidWIndex.close();
				
		String[] txts = FileLoader.loadText(Global.pathIdText);
		int fre = 0;
		boolean isContain = true;
		for(String st : txts) {
			if(st == null)	continue;
			Set<String> words = new HashSet(LuceneUtility.getTerms(st));
			isContain = true;
			for(String t : terms) {
				if(!words.contains(t)) {
					isContain = false;
					break;
				}
			}
			if(isContain)	fre++;
		}
		System.out.println(terms + " 's fre = " + fre);
		
		
	}
	
	public static void testFG() throws Exception {
//		String infile = args[0];
		
//		String infile = "D:\\kSTC\\FPGrowth-master\\data\\census-sample20.dat";
//		String infile = "D:\\kSTC\\FPGrowth-master\\data\\t1.txt";
		String infile = Global.baseDatasetPath + "FG_test.dat";
		
		FPGrow model = new FPGrow(200000);
		
		//Load data from text file into List
//		List<List<String>> transactions = model.loadTransactions(infile);
		
		
		String pathNgbLen = Global.outPath + "term_2_pid_neighbors_len.txt,opticMinpts=4,opticEpsilon=2.0E-4,maxPidNeighborsBytes=2147483631";
		Set<String> filter = FileLoader.loadTooLongTerms(pathNgbLen);
		List<List<String>> transactions = model.loadTransactionsByTxts(Global.pathIdText, null);
		
		model.FPGrowthAlgorithm(transactions);
		
		//Set the default length of frequent items set >=2 
//		model.printResult(2);
		List<Integer> nums = new ArrayList<>();
		nums.add(2);
		nums.add(3);
		nums.add(4);
		
		for(Integer n : nums) {
			List<Term2Fre> tfs = model.getResult(n, filter);
			System.out.println("nw = " + n + ", 组合数：" + tfs.size());
			int avgFre = 0;
			for(Term2Fre tf : tfs) {
				System.out.println(tf);
				avgFre += tf.frequency;
			}
			System.out.println("avg fre = " + (avgFre / tfs.size()));
			System.out.println();
		}
	}
	
	public static void main(String[] args) throws Exception {
		testFG();
//		testGroupFre();
	}
}

