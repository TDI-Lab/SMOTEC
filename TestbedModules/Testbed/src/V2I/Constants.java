package V2I;

import java.io.File;

public class Constants {

	//city border coordinates:
	public static int MAX_X;//top right corner x coordinate
	public static int MAX_Y;//top right corner y coordinate
	public static int MIN_X;//bottom left corner x coordinate
	public static int MIN_Y;//bottom left corner y coordinate
	
	public static int numEdgeNodes;
	public static int numVehicleAgents;
	public static int Edge_COVERAGE;
	public static int EPOSNumPlans;
	
	public static int ADD = 0;
	public static int REMOVE = 1;

	//ports for communication with service distributor:
	public static int listenSrvDis = 32500;
	public static int resSrvDis =32501;
	
	//ports for communication with edgenodes
	public static int edgeListenTopUpPort = 32300;
	public static int edgeResponseTopUpPort = 32100;
	public static int edgeTrafMonTopUpPort = 32200;
    
	public static String MobilityDataset;
	public static String SrvDisListen;
	public static String SrvDisRes;
	
	public static String cityconfigfile;
	public static String filePath;
	public static String yamlPath;
	public static String agentBash;
	public static String ContainerImagesPath;
	public static String edgeBash;
	public static String Secret;
	public static String SrvDisBash;
	public static String outBash;
	public static String updateBash;
	public static String downBash;
	public static String srvReleaseScript;
	public static String srvDeployScript;
	public static String homePath;
	public static String loadFile;
	
	//mountpath for testbedconfig.json in testbed containers:
	public static String configMap;
	public static String SubPath = "TestbedConfig.json";
	public static String agentMountPath = "/tmp/src/conf/"+SubPath;
	public static String edgeMountPath = "/tmp/src/conf/"+SubPath;
	public static String sdMountPath = "/tmp/conf/"+SubPath;
	public static String ServiceDistributorImage;
	public static String EdgeAgentImage;
	public static String MobileAgentImage;
	public static String K3sMaster;
	
	
	static void initialize(){
		
		Constants.filePath = new File("").getAbsolutePath();
																					
		Constants.yamlPath = Constants.filePath + "/src/deployments/deployment";
		Constants.agentBash = Constants.filePath + "/src/mobileagentDeploy.sh ";
		Constants.edgeBash = Constants.filePath + "/src/edgeagentDeploy.sh ";
		Constants.SrvDisBash = Constants.filePath + "/src/srvDistributorDeploy.sh ";
		Constants.outBash = Constants.filePath + "/src/outlog.sh ";
		Constants.updateBash = Constants.filePath + "/src/update.sh ";
		Constants.downBash = Constants.filePath + "/src/delete.sh ";
		Constants.srvReleaseScript = Constants.filePath + "/src/srvRelease.sh ";
		Constants.srvDeployScript = Constants.filePath + "/src/srvDeploy.sh ";
		Constants.homePath = System.getProperty("user.home");
		Constants.loadFile = Constants.homePath.concat("/Documents/output/");
		
	}
}
