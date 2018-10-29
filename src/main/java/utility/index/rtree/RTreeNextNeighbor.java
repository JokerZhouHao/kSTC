package utility.index.rtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import spatialindex.rtree.Data;
import spatialindex.rtree.NNEntry;
import spatialindex.rtree.NNEntryComparator;
import spatialindex.rtree.Node;
import spatialindex.rtree.RTree;
import spatialindex.rtree.RTree.NNComparator;
import spatialindex.spatialindex.IEntry;
import spatialindex.spatialindex.IShape;
import spatialindex.spatialindex.RWLock;

/**
 * 
 * @author ZhouHao
 * @since 2018年10月29日
 * @function get query's next nearest neighbor
 */
public class RTreeNextNeighbor{
	private IShape  query = null;
	private LinkedList<NNEntry> queue = null;
	private Node n = null;
	private MRTree rtree = null;
	private NNComparator nnc = null;
	private RWLock m_rwLock = null;
	
	public RTreeNextNeighbor(IShape query) throws Exception{
		init();
		reset(query);
	}
	
	public void init() throws Exception{
		queue = new LinkedList<NNEntry>(); 
		rtree = MRTree.getInstanceInDisk();
		nnc = rtree.new NNComparator();
		m_rwLock = rtree.getM_RWLock();
	}
	
	public void reset(IShape query) throws Exception{
		this.query = query;
		queue.clear();
		queue.add(new NNEntry(rtree.readNode(rtree.getRoot()), 0.0));
	}
	
	public IEntry next() {
		if (query.getDimension() != rtree.getM_dimensoin()) throw new IllegalArgumentException("nearestNeighborQuery: Shape has the wrong number of dimensions.");
		
		m_rwLock.read_lock();

		try
		{
			// I need a priority queue here. It turns out that TreeSet sorts unique keys only and since I am
			// sorting according to distances, it is not assured that all distances will be unique. TreeMap
			// also sorts unique keys. Thus, I am simulating a priority queue using an ArrayList and binarySearch.

			while (queue.size() != 0)
			{
				NNEntry first = (NNEntry) queue.remove(0);

				if (first.m_pEntry instanceof Node)
				{
					n = (Node) first.m_pEntry;

					for (int cChild = 0; cChild < n.m_children; cChild++)
					{
						IEntry e;

						if (n.m_level == 0)
						{
							e = new Data(n.m_pData[cChild], n.m_pMBR[cChild], n.m_pIdentifier[cChild]);
						}
						else
						{
							e = (IEntry) rtree.readNode(n.m_pIdentifier[cChild]);
						}

						NNEntry e2 = new NNEntry(e, nnc.getMinimumDistance(query, e));

						// Why don't I use a TreeSet here? See comment above...
						int loc = Collections.binarySearch(queue, e2, new NNEntryComparator());
						if (loc >= 0) queue.add(loc, e2);
						else queue.add((-loc - 1), e2);
					}
				}
				else
				{
					// report all nearest neighbors with equal furthest distances.
					// (neighbors can be more than k, if many happen to have the same
					//  furthest distance).
					return first.m_pEntry;
				}
			}
		}
		finally
		{
			m_rwLock.read_unlock();
		}
		return null;
	}
	
}
