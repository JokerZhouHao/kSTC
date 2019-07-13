package precomputation.dataset.file;

import java.util.ArrayList;
import java.util.List;

import entity.Rectangle;
import precomputation.dataset.file.optic.Term2PidNeighborsIndexBuilder;
import precomputation.dataset.yelpbusiness.Json2StringFile;
import utility.Global;
import utility.index.rtree.MRTree;
import utility.io.TimeUtility;

public class SubsetAllFileBuilder {
	public static void main(String[] args) throws Exception{
		Global.displayInputOutputPath();
		
		/************** 运行前提 ****************************
		Dataset文件夹下存在global.properties，且内有类似 curDataset = yelp_academic_dataset_business 参数
		Dataset下的数据集文件夹下存在dataset.properties，且内有类似 (注：顺序是经纬度)subDataset = ([-112.41,33.46],[-111.9,33.68])
		Dataset下的数据集文件夹下的子集文件夹下存在input和output文件夹，且input文件夹内有config.properties
		***********************************************/
		
		/***************** 生成没有对象坐标相同的数据集  ***********************************/
//		SubsetBuilder.buildingNoRepeatCoordSubset();
		
		/***************** 生成不包含某些关键词的数据集  ***********************************/
		//  places_dump_20110628
//		List<String> termFilter = new ArrayList<>();
//		termFilter.add("us");
//		termFilter.add("Us");
//		termFilter.add("US");
//		termFilter.add("services");
//		termFilter.add("Services");
//		SubsetBuilder.buildingFilterTermsSubset(termFilter, ".filterterm");
		
		/***************** 从原始文件中提取所需数据  ***********************************/
//		 yelp_academic_dataset_business
//		Json2StringFile.main(null);
//		([-125.0,28.0],[15.0,60.0])
//		Rectangle subRect = new Rectangle(-112.41,33.46, -111.90,33.68);
//		Rectangle subRect = new Rectangle(-125,28, 15,60);
		
//		meetup
//		Rectangle subRect = new Rectangle(-160,-90, 180,70);
		
//		placedump20110628
//		Rectangle subRect = new Rectangle(-114, 31, -108, 37);	// 亚利桑那州
		Rectangle subRect = new Rectangle(-180, -90, 180, 90);
		
		/***************** 提取某范围内的数据文件 *************************************/
		if(!Global.subDataSetPath.contains(subRect.toString())) {
			throw new Exception("提取出的子集" + subRect.toString() + "所放文件夹" + Global.subDataSetPath + "命名与子集范围不同");
		}
		SubsetBuilder.buildingSubset(subRect);
		
		
		/***************** 正则化坐标 *********************************************/
		ProcessGenerateFiles.normalizedCoordFile(Global.pathIdCoord, subRect);
		
		
		/***************** 创建RTree ********************************************/
//		String placeFile = Global.pathIdNormCoord;
//		String treeFile = Global.rtreePath;
//		ProcessGenerateFiles.buildRTree(placeFile, treeFile, Global.rtreeFanout, Global.rtreeBufferSize, Global.rtreePageSize);
//		MRTree rtree = MRTree.getInstanceInDisk();
//		System.out.println("树高" + rtree.getTreeHeight());
		
		
		/***************** 生成  cellid---pidOrRtreeid---words 索引 ****************/
//		String pathCellidRtreeidOrPidWordsIndex = Global.pathCellidRtreeidOrPidWordsIndex;
//		ProcessGenerateFiles.buildCellidRtreeidOrPidWordsIndex(pathCellidRtreeidOrPidWordsIndex);
		
		
		/* ========================= 以下是 AlgEucDisAdvancedOptics 需用到的文件  ==================================== */
		
		/***************** 生成 term -- 若干个<pid, neighbors> 索引****************/
			/********************* 说明  *************************
			 * config.properties中的
			 * opticMinpts（构建order file时，如果minpts小于opticMinpts，需要重新创建索引）
			 * opticEpsilon（构建order file时，如果xi大于opticEpsilon，需要重新创建索引） 
			 * maxPidNeighborsBytes限制包含term的pid附近的neighbors转换为二进制文件后的Byte数。
			 * 注意：文件term_2_pid_neighbors_len.txt记录的是包含term的所有pid的neighbors转化为二进制文件后的长度（Byte）
			 ***************************************************/
		// 生成 wid_terms 供并行生成索引使用
//		String pathWidTerms = Global.pathWidTerms;
//		ProcessGenerateFiles.generateWidTermsFile(pathWidTerms);
		// 生成索引
//		Term2PidNeighborsIndexBuilder.main(null);
		
	}
}
