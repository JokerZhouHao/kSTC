package utility.index.rtree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import entity.Node;
import entity.NodeCollection;
import entity.QueryParams;
import entity.fastrange.NgbNodes;
import spatialindex.rtree.RTree;
import spatialindex.spatialindex.Point;
import spatialindex.spatialindex.RWLock;
import spatialindex.spatialindex.Region;
import spatialindex.storagemanager.DiskStorageManager;
import spatialindex.storagemanager.IBuffer;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.MemoryStorageManager;
import spatialindex.storagemanager.PropertySet;
import spatialindex.storagemanager.TreeLRUBuffer;
import utility.Global;
import utility.MLog;
import utility.io.IOUtility;

/**
 * 
 * @author Monica
 * provide some function for my task
 * @since 2018/10/25
 * 
 */
public class MRTree extends RTree{
	
	private static MRTree rtree = null;
	
	private MRTree(PropertySet ps, IStorageManager sm) {
		super(ps, sm);
	}
	
	public static MRTree getInstanceInDisk(Boolean isSingle) throws Exception{
		if(isSingle && rtree != null) return rtree;
		MRTree tTree = null;
		PropertySet psRTree = new PropertySet();
		String indexRTree = Global.rtreePath;
		psRTree.setProperty("FileName", indexRTree);
		psRTree.setProperty("PageSize", Global.rtreePageSize);
		psRTree.setProperty("BufferSize", Global.rtreeBufferSize);
		psRTree.setProperty("fanout", Global.rtreeFanout);
		
		IStorageManager diskfile = new DiskStorageManager(psRTree);
		IBuffer file = new TreeLRUBuffer(diskfile, Global.rtreeBufferSize, false);
		
		Integer i = new Integer(1); 
		psRTree.setProperty("IndexIdentifier", i);
		tTree = new MRTree(psRTree, file);
		
		if(isSingle) rtree = tTree;
		return tTree;
	}
	
	public static MRTree getInstanceInDisk() throws Exception{
		return getInstanceInDisk(Boolean.TRUE);
	}
	
	public static MRTree getInstanceInMemory(String placeFile, Boolean isSingle) throws Exception{
		if(isSingle && rtree != null) return rtree;
		MRTree tTree = null;
		PropertySet psRTree = new PropertySet();
		Double f = new Double(0.7);
		psRTree.setProperty("FillFactor", f);
		psRTree.setProperty("IndexCapacity", Global.rtreeFanout);
		psRTree.setProperty("LeafCapacity", Global.rtreeFanout);
		psRTree.setProperty("Dimension", new Integer(2));
		psRTree.setProperty("fanout", Global.rtreeFanout);
		IStorageManager rtreeMem = new MemoryStorageManager();
		
		tTree = new MRTree(psRTree, rtreeMem);
		buildRTreeInMemory(rtree, placeFile);
		
		if(isSingle) rtree = tTree;
		return tTree;
	}
	
	public static MRTree getInstanceInMemory(String placeFile) throws Exception{
		return getInstanceInMemory(placeFile, Boolean.TRUE);
	}
	
	public static void buildRTree(String placefile, String treefile, int fanout, int buffersize, int pagesize)throws Exception{
		// Create a disk based storage manager.
		PropertySet ps = new PropertySet();

		Boolean b = new Boolean(true);
		ps.setProperty("Overwrite", b);
		//overwrite the file if it exists.

		ps.setProperty("FileName", treefile);
		Integer i = new Integer(pagesize);
		ps.setProperty("PageSize", i);
		// specify the page size. Since the index may also contain user defined data
		// there is no way to know how big a single node may become. The storage manager
		// will use multiple pages per node if needed. Off course this will slow down performance.


		IStorageManager diskfile = new DiskStorageManager(ps);

		IBuffer file = new TreeLRUBuffer(diskfile, buffersize, false);
		// applies a main memory random buffer on top of the persistent storage manager
		// (LRU buffer, etc can be created the same way).

		// Create a new, empty, RTree with dimensionality 2, minimum load 70%, using "file" as
		// the StorageManager and the RSTAR splitting policy.

		Double f = new Double(0.7);
		ps.setProperty("FillFactor", f);

		i = fanout;
		ps.setProperty("IndexCapacity", i);
		ps.setProperty("LeafCapacity", i);
		// Index capacity and leaf capacity may be different.

		i = new Integer(2);
		ps.setProperty("Dimension", i);

		MRTree rtree = new MRTree(ps, file);
		
		
		BufferedReader reader = IOUtility.getBR(placefile);
		String line = reader.readLine();
		
		int cntLines = 0;
		double[] f1 = new double[2];
		double[] f2 = new double[2];

		long start = System.currentTimeMillis();
		while ((line = reader.readLine()) != null) {
			cntLines++;
			String[] pidCoord = line.split(Global.delimiterLevel1);
			int id = Integer.parseInt(pidCoord[0]);
			String[] coord = pidCoord[1].split(Global.delimiterSpace);
			Double x = Double.parseDouble(coord[0]);
			Double y = Double.parseDouble(coord[1]);
//			if (x < -90 || x > 90 || y < -180 || y > 180) {
//				continue;
//			}
			f1[0] = f2[0] = x;
			f1[1] = f2[1] = y;
			Region r = new Region(f1, f2);
			rtree.insertData(null, r, id);

			if (cntLines % 100000 == 0)
				MLog.log(cntLines + " places inserted");
		}

		long end = System.currentTimeMillis();

		System.out.println("Revision Minutes: " + ((end - start) / 1000.0f) / 60.0f);

		boolean ret = rtree.isIndexValid();
		if (ret == false)
			System.err.println("Structure is INVALID!");

		rtree.flush();
	}
	
	private static void buildRTreeInMemory(MRTree rtree, String placeFile) throws Exception{
		BufferedReader reader = IOUtility.getBR(placeFile);
		String line = reader.readLine();
		int cntLines = 0;
		double[] f1 = new double[2];
		double[] f2 = new double[2];
		String[] pidCoord;

		long start = System.currentTimeMillis();
		while ((line = reader.readLine()) != null) {
			cntLines++;
			if (line.contains(Global.delimiterPound)) {
				continue;
			}
			pidCoord = line.split(Global.delimiterLevel1);
			int id = Integer.parseInt(pidCoord[0]);
			String[] coord = pidCoord[1].split(Global.delimiterSpace);
			Double x = Double.parseDouble(coord[0]);
			Double y = Double.parseDouble(coord[1]);
//			if (x < -90 || x > 90 || y < -180 || y > 180) {
//				continue;
//			}
			f1[0] = f2[0] = x;
			f1[1] = f2[1] = y;
			Region r = new Region(f1, f2);
			rtree.insertData(null, r, id);

//			if (cntLines % 10000 == 0)
//				System.out.println(cntLines + " places inserted");
		}

		long end = System.currentTimeMillis();

		System.err.println(rtree);
		System.err.println("Minutes: " + ((end - start) / 1000.0f) / 60.0f);

		boolean ret = rtree.isIndexValid();
		if (ret == false)
			System.err.println("Structure is INVALID!");

		reader.close();
	}
	
	public LinkedList<Node> rangeQuery(QueryParams qParams, int clusterId, Node centerNode, NodeCollection nodeCol, Point[] allPoints){
		LinkedList<Node> neighbors = new LinkedList<>();
		LinkedList<Integer> idQueue = new LinkedList<>();
		idQueue.add(getRoot());
		int id = 0;
		int child = 0;
		spatialindex.rtree.Node rNode = null;
		Node pNode = null;
		while(!idQueue.isEmpty()) {
			id = idQueue.pollFirst();
			rNode = readNode(id);
			if(rNode.isLeaf()) {
				for(child=0; child < rNode.m_children; child++) {
					if(null != (pNode = nodeCol.get(rNode.m_pIdentifier[child])) &&
						(pNode.hasInCluster(clusterId) || !pNode.isClassified()) &&
						centerNode.location.getMinimumDistance(allPoints[rNode.m_pIdentifier[child]]) <= qParams.epsilon) {
						neighbors.add(pNode);
					}
				}
			} else {
				for(child=0; child < rNode.m_children; child++) {
					if(null != (pNode = nodeCol.get(-rNode.m_pIdentifier[child] - 1)) &&
						centerNode.location.getMinimumDistance(rNode.m_pMBR[child]) <= qParams.epsilon) {
						idQueue.add(rNode.m_pIdentifier[child]);
					}
				}
			}
		}
		if(neighbors.isEmpty())	return null;
		else return neighbors;
	}
	
	public LinkedList<Node> rangeQueryReDescendNodes(QueryParams qParams, int clusterId, Node centerNode, NodeCollection nodeCol, Point[] allPoints){
		NgbNodes ngb = new NgbNodes();	
		LinkedList<Integer> idQueue = new LinkedList<>();
		idQueue.add(getRoot());
		int id = 0;
		int child = 0;
		spatialindex.rtree.Node rNode = null;
		Node pNode = null;
		while(!idQueue.isEmpty()) {
			id = idQueue.pollFirst();
			rNode = readNode(id);
			if(rNode.isLeaf()) {
				for(child=0; child < rNode.m_children; child++) {
					if(null != (pNode = nodeCol.get(rNode.m_pIdentifier[child])) &&
						(pNode.hasInCluster(clusterId) || !pNode.isClassified())) {
						pNode.disToCenter = centerNode.location.getMinimumDistance(allPoints[rNode.m_pIdentifier[child]]);
						if(pNode.disToCenter <= qParams.epsilon) {
							ngb.add(pNode.disToCenter, pNode);
						}
					}
				}
			} else {
				for(child=0; child < rNode.m_children; child++) {
					if(null != (pNode = nodeCol.get(-rNode.m_pIdentifier[child] - 1)) &&
						centerNode.location.getMinimumDistance(rNode.m_pMBR[child]) <= qParams.epsilon) {
						idQueue.add(rNode.m_pIdentifier[child]);
					}
				}
			}
		}
		if(ngb.size() == 0)	return null;
		else return ngb.toList();
	}
	
	
	public List<Node> rangeQuery(Point centerNode, Double radius, Point[] allPoints){
		List<Node> neighbors = new LinkedList<>();
		LinkedList<Integer> idQueue = new LinkedList<>();
		idQueue.add(getRoot());
		int id = 0;
		int child = 0;
		spatialindex.rtree.Node rNode = null;
		double distance = 0.0;
		while(!idQueue.isEmpty()) {
			id = idQueue.pollFirst();
			rNode = readNode(id);
			if(rNode.isLeaf()) {
				for(child=0; child < rNode.m_children; child++) {
					distance = centerNode.getMinimumDistance(allPoints[rNode.m_pIdentifier[child]]);
					if(distance <= radius) {
						neighbors.add(new Node(rNode.m_pIdentifier[child], allPoints[rNode.m_pIdentifier[child]], distance, 0.0));
					}
				}
			} else {
				for(child=0; child < rNode.m_children; child++) {
					if(centerNode.getMinimumDistance(rNode.m_pMBR[child]) <= radius) {
						idQueue.add(rNode.m_pIdentifier[child]);
					}
				}
			}
		}
		if(neighbors.isEmpty())	return null;
		else return neighbors;
	}
	
	public void writeRtreeInfo(String path) throws Exception{
		BufferedWriter bw = IOUtility.getBW(path);
		bw.write("root: " + rtree.getRoot() + "\n");
		bw.write("height: " + rtree.getTreeHeight() + "\n");
		bw.write("NumNode: " + rtree.m_stats.getNumberOfNodes() + "\n");
		bw.write("NumData: " + rtree.m_stats.getNumberOfData() + "\n");
		bw.close();
	}
	
	public RWLock getM_RWLock() {
		return this.m_rwLock;
	}
}
