package pi;

public class Constants {
	
	public static int EPOS_NUM_PLANS;// = 4;
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
	public static String bp;// = "C:\\Users\\znb_n\\eclipse-workspace\\EdgeNode";
	public static int iteration = 3;
	public static int simulation = 2;
	
	public static String deployDir;
	public static String srvDeployScript;
	public static String srvReleaseScript;
	public static String brokerUrl;
	public static String conf;
	public static int edgeAgentIndex;
	public static int maxTime = 0;
	public static String SRVDISPRO;
	public static String SRVDISCON;
	public static String reqListener;
	public static String traficDataPublisger;
	

}
