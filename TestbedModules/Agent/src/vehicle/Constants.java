package vehicle;


public class Constants {

	
	public static int MAX_X;
	public static int MAX_Y;
	public static int MIN_X;
	public static int MIN_Y;
	public static int AP_COVERAGE;
	public static int numEdgeNodes;
	
	public static String mobDir;
	public static String mobilityfile;
	public static String filePath;

	public static int cpu;
	public static int mem;
	public static String conf;
	public static int storage;
	public static int numMsg;//records control messages this agent sends 
	
	public static int ResPortTopUp = 32100;
	//types of messages a vehicleagent sends to the edge network:
	public static final int CONNREQ = 0;
	public static final int RECOREQ = 1;
	public static final int DISCREQ = 2;
	public static final int HOSREQ = 3;
	public static final int DOWNREQ = 4;
	public static final int HandofRange = 200;
	
	public static int collectorPortTopUp = 22400;
	public static int edgePortTopUp = 32300;
}
