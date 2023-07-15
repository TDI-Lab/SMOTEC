package srv;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * @author zeinab
 * This program offers a traffic monitoring service which is deployed on Edge Nodes and receives traffic updates from its host node 
 * and then publishes that information to its subscriber clients (mobile agents).
 *
 */
public class Collector {

	private static String vehAgentId;
	private static String edgeAgentId;
	static Socket pubServer;
	static String traficDataPublisger;
	
	public static void main(String[] args) throws InterruptedException {

		vehAgentId = args[0];// id of VehicleAgent to communicate with
		edgeAgentId = args[1];// EdgeAgent id on which the service is hosted
		int agentPort = Integer.parseInt(vehAgentId) + 22400;//the port to the messages from this service
		int port = 32200+Integer.parseInt(edgeAgentId);//host port from which the traffic updates are received from EdgeAgent
		System.out.println("Received args: "+vehAgentId+" "+edgeAgentId);

		//To publish traffic monitoring updates to the subscribed VehicleAgents:
		Context context = ZMQ.context(1);
		Socket pubServer = context.socket(ZMQ.PUB);
		traficDataPublisger = "tcp://*:"+agentPort;
		pubServer.bind(traficDataPublisger);
	   	
		//subscribes to the messages from EdgeAgent on which this service is hosted:
		Context context1 = ZMQ.context(1);
		Socket subServer = context1.socket(ZMQ.SUB);
		subServer.connect("tcp://edge"+edgeAgentId+"-trafmon:"+port);


		String ReqTOPIC = "traffic:"+edgeAgentId;//traffic monitoring topic for filtering messages
		subServer.subscribe(ReqTOPIC.getBytes(Charset.forName("UTF-8")));
	    
		System.out.println("TrafficMonitoring service (Collector) "+ vehAgentId+" started and waiting for updates from EdgeAgent "+edgeAgentId+" to publish");
       	   
		collectAndSend(subServer, pubServer);
		
	}
	
		
	
	/**
	 * @param subServer2 
	 * @param pubServer2 
	 * @param srv service as a mqtt client
	 * @throws MqttException
	 * receives traffic-related information and publish them to the VehicleAgents running on mobile nodes
	 * @throws InterruptedException 
	 */
	private static void collectAndSend(Socket subServer2, Socket pubServer2) throws InterruptedException {
		
		subServer2.setReceiveTimeOut(2000);
		
		while(true) {
			
       	   String content = subServer2.recvStr();
       	   if (content != null) {
	       	   System.out.println("Traffic update received from EdgeAgent: "+content);
	       	   StringTokenizer str = new StringTokenizer(content.toString(),"!");//message format: "traffic"+":"+publisher+"!Connected:+"connectedAgents+":Time:"+updatetime;
	           str.nextToken();
	            
	            byte[] msg = content.getBytes();
	    		for (int update_nbr = 0; update_nbr < 2; update_nbr ++) {
	    			pubServer2.send(msg);
	    			System.out.println("Traffic update sent to VehicleAgent: "+new String(msg));
	          
	    		} 
	    		 
       	   }
       }
	}

}
