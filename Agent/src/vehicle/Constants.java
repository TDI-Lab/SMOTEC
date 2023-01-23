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
	public static boolean win;
	public static String infraFile;
	
	public static void initialize(boolean win, int uId) {
		 
		if (win == true) {
			
			infraFile = "C:\\Users\\znb_n\\eclipse-workspace\\Agent\\src\\vehicle\\infrastructure.json";
			//mobilityfile = "C:\\Users\\znb_n\\eclipse-workspace\\Agent\\src\\Mobility_Dataset\\"+ uId+".csv";
		
		}
		else {
			//mobilityfile = "/home/spring/Documents/Agent/src/Mobility_Dataset/" + uId+".csv";
			infraFile = "/home/spring/Documents/Agent/src/vehicle/infrastructure.json";
		}	
	}
}
