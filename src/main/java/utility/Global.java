package utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import entity.Rectangle;
import utility.io.IOUtility;

/**
 * 
 * @author ZhouHao
 * provide some global variables
 * 2018/10/24
 */
public class Global {
	
	private static Properties configProps = null;
	
	public static String projectName = "kSTC";
	public static String basePath = null;
	public static String baseDatasetPath = null;
	
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
	
	public static String pathTestFile = null;
	
	// index
	public static String pathPidAndRtreeIdWordsIndex = null;
	public static String pathTestIndex = null;
	
	// sign
	public static String signNormalized = "[normalized]";
	
	/* file content delimiter sign */
	public static String delimiterLevel1 = ": ";
	public static String delimiterLevel2 = ",";
	public static String delimiterSpace = " ";
	public static String delimiterPound = "#";
	
	/* sub dataset */
	public static Rectangle subYelpBus1 = new Rectangle(-125, 28, 15, 60);
	
//	public static String suffixFile = "";
	public static String suffixFile = subYelpBus1.toString();
	
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
		
		// set file path
		pathTestFile = outPath + "test.txt";
		pathIdName = inputPath + (String)configProps.get("fileIdName") + suffixFile;
		pathIdText = inputPath + (String)configProps.get("fileIdText") + suffixFile;
		pathIdCoord = inputPath + (String)configProps.get("fileCoord") + suffixFile;
		pathIdWids = inputPath + (String)configProps.get("fileIdWids") + suffixFile;
		pathWidWord = inputPath + (String)configProps.get("fileWidWord") + suffixFile;
		
		
		// set index path
		pathPidAndRtreeIdWordsIndex = outPath + (String)configProps.get("pathPidAndRtreeIdWordsIndex") + suffixFile + File.separator;
		pathTestIndex = outPath + "test" + File.separator;
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
		rtreePath = Global.outPath + "rtree" + ".rtreeBufferSize" + String.valueOf(Global.rtreeBufferSize)
								   + ".rtreePageSize" + String.valueOf(Global.rtreePageSize) 
								   + ".rtreeFanout" + String.valueOf(Global.rtreePageSize) +  suffixFile + File.separator;
									
		if(!IOUtility.exists(rtreePath))	new File(rtreePath).mkdir();
		
		Global.rtreeBufferSize = Integer.parseInt((String)configProps.get("rtreeBufferSize"));
		Global.rtreePageSize = Integer.parseInt((String)configProps.get("rtreePageSize"));
		Global.rtreeFanout = Integer.parseInt((String)configProps.get("rtreeFanout"));
		Global.iindexBufferSize = Integer.parseInt((String)configProps.get("iindexBufferSize"));
		Global.iindexPageSize = Integer.parseInt((String)configProps.get("iindexPageSize"));
		Global.iindexIsCreate = Boolean.parseBoolean((String)configProps.get("iindexIsCreate"));
		Global.iindexIsWeighted = Boolean.parseBoolean((String)configProps.get("iindexIsWeighted"));
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
	}
	
	static {
		try {
			// set paths
			initBasePath();
			setAllPaths(DatasetType.values()[0]);
			
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
