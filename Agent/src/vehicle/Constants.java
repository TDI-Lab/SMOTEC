package vehicle;

import java.io.File;

public class Constants {

	
	public static short[][] VehiclesToAP = null;
	public static int MAX_X;//6500 for test,
	public static int MAX_Y;//8700,
	public static int MIN_X;//493,
	public static int MIN_Y;//2060,
	public static int AP_COVERAGE;
	public static int numEdgeNodes;
	
	public static int ADD = 0;
	public static int REMOVE = 1;
	public static int MaxSmartThingPerAP = 300;
	public static int Handoff = 200;
	
	public static String FOG_DEVICE_ARCH = "x86";
	public static String FOG_DEVICE_OS = "Linux";
	public static String FOG_DEVICE_VMM = "Xen";
	public static double FOG_DEVICE_TIMEZONE = 10.0;
	public static double FOG_DEVICE_COST = 3.0;
	public static double FOG_DEVICE_COST_PER_MEMORY = 0.05;
	public static double FOG_DEVICE_COST_PER_STORAGE = 0.001;
	public static double FOG_DEVICE_COST_PER_BW = 0.0;
	public static String mobilityfile;
	public static String filePath;
	public static String mobDir;
	public static String brokerUrl;
	public static int cpu;
	public static int mem;
	public static String conf;
	public static int storage;
	
	public static final int CONNREQ = 0;
	public static final int RECOREQ = 1;
	public static final int DISCREQ = 2;
	public static final int HOSREQ = 3;
	public static final int DOWNREQ = 4;
	
	
}
