package pi;

import java.io.File;

public class Constants {
	
	public static int maxTime = 0;
	public static double utilRatio = 0.6;
	
	public static final int CONREQ = 0;
	public static final int RECREQ = 1;
	public static final int DISREQ = 2;
	public static final int HOSREQ = 3;
	public static final int DOWN = 4;
	
	static String ConReqTOPIC = "conn";//request topic
	static String RecReqTOPIC = "reco";//reconnect topic
	static String DisReqTOPIC = "disc";//disconnect topic
	static String DownReqTOPIC = "down";//down topic
		
	
	public static String PlanUtilDataset;
	public static String PlanBinaryDataset;
	public static boolean win;
	public static String bp;
	
	public static int iteration = 3;
	public static int simulation = 2;
	
	public static String deployDir;
	public static String srvDeployScript;
	public static String srvReleaseScript;
	public static String brokerUrl;
	public static String conf;
	public static int edgeAgentIndex;
	
	//service distributor specifications:
	public static String srvdisSrvListener;
	public static String srvdisSrvProducer;
	public static int PPORT = 32500;
	public static int CPORT = 32501;

	public static String SRVDISPRO ;//producer
	public static String SRVDISCON ;//consumer
	public static int EPOS_NUM_PLANS;
	
	public static String reqListener;//0MQ server listening to the received requests on this edgeAgent
	public static String traficDataPublisger;//0MQ traffic data publisher running on this EdgeAgent
	public static String connectedEdgeAddr;//selected hosts distributor address for communicating with mobile agents
	
	public static int srvDisListenerTopUp = 32300;
	public static int updatePublishTopUp = 32200;
	public static int hostDistributorTopUp = 32100;
	
	public static String containerImagesPath;
	public static int numMsg;//records the communication cost (number of control messages)
	public static String ServiceName;
	
	public static void initialize(){
		
		String basePath = new File("").getAbsolutePath();
		Constants.bp = basePath;
		Constants.conf = basePath+"/src/conf/TestbedConfig.json";
		Constants.PlanBinaryDataset = basePath+"/datasets/Binary/";
		Constants.PlanUtilDataset = basePath+"/datasets/Utilization/";
		
		Constants.deployDir = basePath+"/src/deployments/";
		Constants.srvDeployScript = basePath+"/src/srvDeploy.sh ";
		Constants.srvReleaseScript = basePath+"/src/srvRelease.sh";
		
	}

}
