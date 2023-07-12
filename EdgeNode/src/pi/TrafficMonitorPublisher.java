package pi;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import java.util.HashSet;
import java.util.concurrent.Callable;

/**
 * @author zeinab
 * This class published traffic-related data to the subscriber services
 *
 */
public class TrafficMonitorPublisher implements Callable<Void> {
	
	Context context;
	Socket pubServer;
	HashSet<String> connectedAgents;
	HashSet<Integer> hostedSrvs;
	String myId;
	
	public TrafficMonitorPublisher(Context context2, String edgeAgent, HashSet<String> connectedSet, HashSet<Integer> hostedList) {
		context = context2;
		myId = edgeAgent;
		pubServer = context.socket(ZMQ.PUB);
    	this.connectedAgents = connectedSet;
    	this.hostedSrvs = hostedList;
    	pubServer.bind(Constants.traficDataPublisger); 
    	System.out.println("Traffic data is publishing to the TrafficMonitoring services from socket: "+Constants.traficDataPublisger);
     	
	}
    
	@Override
	public Void call() throws Exception {
		int update_nbr; 
		byte[]  msg = makeMessage();
		for (update_nbr = 0; update_nbr < 2; update_nbr ++) {
			pubServer.send(msg);
			
		} 
		System.out.println("Traffic update message sent to the running service: \""+new String(msg)+"\"");
         
        
	        return null;  
	}
	
	/**
	 * @return traffic message including the number of connected mobile agents (vehicles) to this edgeAgent. 
	 * The connected vehicles are the vehicles which are in the coverage range of this EdgeAgent.
	 */
	private byte[] makeMessage() {             
        
        	String constr = "traffic"+":"+myId+"!Connected:";
     		constr += connectedAgents.size()+":Time:";
            constr += Constants.maxTime+":hostedSrv:"+hostedSrvs.size();//number of services hosted on this EdgeNode by this EdgeAgent
             
            byte[] payload = constr.getBytes();        
             
        return payload;
    }

	
	public void close() {
		pubServer.close (); 
		context.term ();
	}
}
