package EPOS;

public class Constants {

	static int curTime = 0;
	
	public static int iteration = 3;
	public static int simulation = 2;
	public static int numofEdgeAgents = 3;
	
	static final int CPORT = 32500;
	static final int PPORT = 32501;
	public static String SRVDISPRO= "tcp://*:"+PPORT;
	public static String SRVDISCON= "tcp://*:"+CPORT;
	
	public static int numMsg;

}
