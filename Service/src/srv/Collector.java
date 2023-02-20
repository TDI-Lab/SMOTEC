package srv;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class Collector {

	private static String vehAgentId;
	private static String edgeAgentId;
	private static int time;
	private static int currreqId = 0;
	private static String brokerUrl;
	static Socket pubServer;
	//static Socket subServer;
	static String traficDataPublisger;
	
	public static void main(String[] args) throws InterruptedException {

		vehAgentId = args[0];//name of agent to communicate with
		edgeAgentId = args[1];//edge id == index in list of neighbours
		System.out.println("Received args: "+vehAgentId+" "+edgeAgentId);

		//publish to the VehAgent to which it sends data
		Context context = ZMQ.context(1);
		Socket pubServer = context.socket(ZMQ.PUB);
		traficDataPublisger = "tcp://localhost:"+vehAgentId;
		pubServer.bind(traficDataPublisger); 
	   	//Constants.traficDataPublisger = "tcp://localhost:"+(5600+Constants.edgeAgentIndex);
		
		//subscribe to the edgeAgent from which it receives data
		Context context1 = ZMQ.context(1);
		Socket subServer = context1.socket(ZMQ.SUB);
		subServer.connect("tcp://localhost:5601");//id host/edgeagent
		//subServer.subscribe("");
		String ReqTOPIC = "traffic:"+edgeAgentId;
		//String filter = ReqTOPIC;
		subServer.subscribe(ReqTOPIC.getBytes(Charset.forName("UTF-8")));
	    
		/*
		 * pubServer.send(ReqTOPIC); System.out.println("Waiting for updates from ...");
		 * String content = subServer.recvStr(); if (content != null) {
		 * System.out.println("Update received from EdgeAgent: "+content); }
		 */   
     	   
		
		
       	   
		
	    //System.out.println("Collector Service for vehAgent: "+vehAgentId+" started and running on Edge Node: "+edgeAgentId);
        
		collectAndSend(subServer, pubServer);
			
        
		
	}
	
		
	
	/**
	 * @param subServer2 
	 * @param pubServer2 
	 * @param srv service as a mqtt client
	 * @throws MqttException
	 * receives traffic information and send them back to agents running on mobile nodes
	 * @throws InterruptedException 
	 */
	private static void collectAndSend(Socket subServer2, Socket pubServer2) throws InterruptedException {
		//String ReqTOPIC = "traffic:"+edgeAgentId;
		
		subServer2.setReceiveTimeOut(2000);
		
		while(true) {
			
			//pubServer2.send(ReqTOPIC);
		     
		   System.out.println("Waiting for updates from ");
       	   String content = subServer2.recvStr();
       	   if (content != null) {
	       	   System.out.println("Update received from EdgeAgent: "+content);
	       	   StringTokenizer str = new StringTokenizer(content.toString(),"!");//"traffic"+":"+pub+"!Connected:+"connectedAgents+":Time:";
	           str.nextToken();
	            
	            byte[] msg = content.getBytes();
	    		for (int update_nbr = 0; update_nbr < 2; update_nbr ++) {
	    			pubServer2.send(msg);
	    			
	    		} 
	    		System.out.println("Update sent to the VehicleAgent: "+new String(msg));
	             
		         //    Thread.sleep(1000);
	       
	   }
       	   }
	}

}
