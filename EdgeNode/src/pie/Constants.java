package pie;

public class Constants {
	
	static int EPOS_NUM_PLANS = 2;
	public static double utilRatio = 0.6;
	public static String PlanUtilDataset;
	public static String PlanBinaryDataset;
	public static String EPOS_DatasetADDRESS_WIN = "C:\\Users\\znb_n\\eclipse-workspace\\EdgeNode\\datasets\\";
	public static String EPOS_DatasetADDRESS_LIN = "/home/zeinab/EdgeNode/datasets/";
	public static boolean win;
	public static String outpath;// = "C:\\Users\\znb_n\\eclipse-workspace\\EdgeNode";
	public static int iteration = 3;
	public static int simulation = 2;
	public static String infraFile;
	public static String deployDir;
	public static String srvScript;
	
	public static void initialize(boolean win) {
	    	
		 if (win == true) {
			    outpath = "C:\\Users\\znb_n\\eclipse-workspace\\EdgeNode";
				PlanBinaryDataset = EPOS_DatasetADDRESS_WIN + "Binary\\";
				PlanUtilDataset = EPOS_DatasetADDRESS_WIN+"Utilization\\";
				deployDir = "C:\\Users\\znb_n\\eclipse-workspace\\EdgeNode\\src\\deployments\\";
				srvScript = "C:\\Users\\znb_n\\eclipse-workspace\\EdgeNode\\src\\srv.sh ";
				infraFile = "C:\\Users\\znb_n\\eclipse-workspace\\EdgeNode\\src\\pie\\infrastructure.json";
				
	    	}
	    else {
	    	    outpath = "/home/zeinab/EdgeNode";
	    		PlanBinaryDataset = EPOS_DatasetADDRESS_LIN+"Binary/";
		    	PlanUtilDataset = EPOS_DatasetADDRESS_LIN+"Utilization/";
		    	deployDir = "/home/spring/Documents/Testbed/src/deployments/";
		    	srvScript = " /home/spring/Documents/Testbed/src/srv.sh ";
		    	infraFile = "/home/spring/Documents/EdgeNode/src/pie/infrastructure.json";
				   
		    	
	    	}
}
}
