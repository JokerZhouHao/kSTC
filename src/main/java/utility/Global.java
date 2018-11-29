package utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import entity.Rectangle;
import entity.SGPLInfo;
import utility.io.IOUtility;
import utility.io.TimeUtility;

/**
 * 
 * @author ZhouHao
 * provide some global variables
 * 2018/10/24
 */
public class Global {
	
	public static final double minPositiveDouble = 0.000000000001;
	
	public static int compareDouble(double d1, double d2) {
		if(d1 - d2 >= minPositiveDouble)	return 1;
		else if(d1 - d2 >= -minPositiveDouble) return 0;
		else return -1;
	}
	
	public static Boolean isZero(double d) {
		if(d >= -minPositiveDouble && d < minPositiveDouble)	return Boolean.TRUE;
		else return Boolean.FALSE;
	}
	
	private static Properties configProps = null;
	
	public static String projectName = "kSTC";
	public static String basePath = null;
	public static String baseDatasetPath = null;
	
	public static final double alpha = 0.5; // the weight of distance in caculate score
	
	// set the paths for dealed dataset
	public static DatasetType datasetType = null;
	public static String datasetPath = null;
	public static String inputPath = null;
	public static String outPath = null;
	public static String configPath = null;
	
	public static String pathIdName = null;
	public static String pathIdText = null;
	public static String pathIdCoord = null;
	public static String pathIdWids = null;
	public static String pathWidWord = null;
	public static String pathIdTerms = null;
	
	public static String pathTestFile = null;
	
	// index
	public static String pathPidAndRtreeIdWordsIndex = null;
	public static String pathCellidPidWordsIndex = null;
	public static String pathCellidRtreeidOrPidWordsIndex = null;
	public static String pathTestIndex = null;
	
	// SGPL
	public static int zorderWidth = 0;
	public static int zorderHeight = 0;
	public static SGPLInfo sgplInfo = null;
	public static String pathTerm2CellColIndex = null;
	
	// sign
	public final static String signNormalized = "[normalized]";
	public final static String signKNeighborDis = "_neighbor_dis.txt";
	
	/* file content delimiter sign */
	public static String delimiterLevel1 = ": ";
	public static String delimiterLevel2 = ",";
	public static String delimiterSpace = " ";
	public static String delimiterPound = "#";
	
	/* num */
	public static int numNode = 0;
	
	/* sub dataset */
	public static Rectangle subYelpBus1 = new Rectangle(-125, 28, 15, 60);
	
//	public static String suffixFile = "";
	public static String suffixFile = null;
	
	// initBasePath
	public static void initBasePath() throws Exception{
		basePath = getBasePath();
		IOUtility.existsOrThrowsException(basePath);
		
		baseDatasetPath = getBaseDatasetPath();
		IOUtility.existsOrThrowsException(baseDatasetPath);
	}
	
	// getBasePath
	public static String getBasePath() {
		if(null==basePath) {
			basePath = Global.class.getResource("").getPath();
			basePath = basePath.substring(1, basePath.indexOf(projectName) + projectName.length() + 1).replace("/", File.separator);
			if(File.separator.equals("/")) basePath = File.separator + basePath;
		}
		return basePath;
	}
	
	// getBaseDatasetPath
	public static String getBaseDatasetPath() {
		if(null == baseDatasetPath) {
			baseDatasetPath = Global.getBasePath() + "Dataset" + File.separator;
		}
		return baseDatasetPath;
	}
	
	// setAllPathsForDataset
	public static void setAllPaths(DatasetType datasetType) throws Exception{
		Global.datasetType = datasetType;
		switch(datasetType) {
		case yelp_academic_dataset_business:
			datasetType = DatasetType.yelp_academic_dataset_business;
		}
		datasetPath = baseDatasetPath + datasetType.toString() + File.separator;
		IOUtility.existsOrThrowsException(datasetPath);
		
		inputPath = datasetPath + "input" + File.separator;
		IOUtility.existsOrThrowsException(inputPath);
		
		outPath = datasetPath + "output" + File.separator;
		IOUtility.existsOrThrowsException(outPath);
		
		configPath = inputPath + "config.properties";
		IOUtility.existsOrThrowsException(configPath);
		
		// loading config properties
		configProps = new Properties();
		configProps.load(new FileInputStream(Global.configPath));
		
		suffixFile = (String)configProps.get("suffixFile");
		
		// set file path
		pathTestFile = outPath + "test.txt";
		pathIdName = inputPath + (String)configProps.get("fileIdName") + suffixFile;
		pathIdText = inputPath + (String)configProps.get("fileIdText") + suffixFile;
		pathIdCoord = inputPath + (String)configProps.get("fileCoord") + suffixFile;
		pathIdWids = inputPath + (String)configProps.get("fileIdWids") + suffixFile;
		pathIdTerms = inputPath + (String)configProps.get("fileIdTerms") + suffixFile;
		pathWidWord = inputPath + (String)configProps.get("fileWidWord") + suffixFile;
		
		// set index path
		pathPidAndRtreeIdWordsIndex = outPath + (String)configProps.get("pathPidAndRtreeIdWordsIndex") + suffixFile + File.separator;
		pathCellidPidWordsIndex = outPath + (String)configProps.get("pathCellidPidWordsIndex") + suffixFile + signNormalized + File.separator;
		pathCellidRtreeidOrPidWordsIndex = outPath + (String)configProps.get("pathCellidRtreeidOrPidWordsIndex") + suffixFile + signNormalized + File.separator;
		pathTestIndex = outPath + "test" + File.separator;
		
		// set num
		numNode = Integer.parseInt((String)configProps.get("numNode"));
		
		// SGPL
		zorderWidth = Integer.parseInt((String)configProps.get("zorderWidth"));
		zorderHeight = Integer.parseInt((String)configProps.get("zorderHeight"));
		sgplInfo = SGPLInfo.getGlobalInstance();
		pathTerm2CellColIndex =outPath + (String)configProps.get("pathTerm2CellColIndex") + suffixFile + signNormalized + File.separator;
	}
	
	/* rtree index setting parameters */
	public static String rtreePath = null;
	public static int rtreeBufferSize = 4096000;
	public static int rtreePageSize = 32768;	// 400
	public static int rtreeFanout = 5;
	public static int iindexBufferSize = 4096000;
	public static int iindexPageSize = 128;
	public static boolean iindexIsCreate = false;
	public static boolean iindexIsWeighted = false;
	
	private static void initRTreeParameters() {
		Global.rtreeBufferSize = Integer.parseInt((String)configProps.get("rtreeBufferSize"));
		Global.rtreePageSize = Integer.parseInt((String)configProps.get("rtreePageSize"));
		Global.rtreeFanout = Integer.parseInt((String)configProps.get("rtreeFanout"));
		Global.iindexBufferSize = Integer.parseInt((String)configProps.get("iindexBufferSize"));
		Global.iindexPageSize = Integer.parseInt((String)configProps.get("iindexPageSize"));
		Global.iindexIsCreate = Boolean.parseBoolean((String)configProps.get("iindexIsCreate"));
		Global.iindexIsWeighted = Boolean.parseBoolean((String)configProps.get("iindexIsWeighted"));
		
		rtreePath = Global.outPath + "rtree" + ".rtreeBufferSize" + String.valueOf(Global.rtreeBufferSize)
								   + ".rtreePageSize" + String.valueOf(Global.rtreePageSize) 
								   + ".rtreeFanout" + String.valueOf(Global.rtreeFanout) +  suffixFile + File.separator;
		if(!IOUtility.exists(rtreePath))	new File(rtreePath).mkdir();
	}
	
	public static void display() {
		System.out.println("--------------------------- base path --------------------------");
		System.out.println("projectName : " + Global.projectName);
		System.out.println("basePath : " + Global.basePath);
		System.out.println("baseDatasetPath : " + Global.baseDatasetPath);
		System.out.println("datasetType : " + Global.datasetType);
		System.out.println("datasetPath : " + Global.datasetPath);
		System.out.println("inputPath : " + Global.inputPath);
		System.out.println("outPath : " + Global.outPath);
		System.out.println("configPath : " + Global.configPath);
		System.out.println("pathIdName" + Global.pathIdName);
		System.out.println("pathIdText : " + Global.pathIdText);
		System.out.println("pathIdTerms : " + Global.pathIdTerms);
		System.out.println("pathIdCoord : " + Global.pathIdCoord);
		
		System.out.println("\n--------------------------- rtree parameters --------------------------");
		System.out.println("rtreePath : " + Global.rtreePath);
		System.out.println("rtreeBufferSize : " + Global.rtreeBufferSize);
		System.out.println("rtreePageSize : " + Global.rtreePageSize);
		System.out.println("rtreeFanout : " + Global.rtreeFanout);
		System.out.println("iindexBufferSize : " + Global.iindexBufferSize);
		System.out.println("iindexPageSize : " + Global.iindexPageSize);
		System.out.println("iindexIsCreate : " + Global.iindexIsCreate);
		System.out.println("iindexIsWeighted : " + Global.iindexIsWeighted);
		
		System.out.println("\n--------------------------- num parameters --------------------------");
		System.out.println("numNode : " + Global.numNode);
		
		System.out.println("\n--------------------------- SGPL --------------------------");
		System.out.println("zorderWidth : " + Global.zorderWidth);
		System.out.println("zorderHeight : " + Global.zorderHeight);
		System.out.println("SGPLInfo : " + Global.sgplInfo);
		System.out.println("pathTerm2CellColIndex : " + Global.pathTerm2CellColIndex);
	}
	
	static {
		try {
			TimeUtility.init();
			
			// set paths
			initBasePath();
//			setAllPaths(DatasetType.values()[0]);
			setAllPaths(DatasetType.values()[1]);
			
			// set rtree parameters
			Global.initRTreeParameters();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) throws Exception{
		Global.display();
	}
	
}
