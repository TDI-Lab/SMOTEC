package pi;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import java.util.HashSet;
import java.util.concurrent.Callable;

public class Publisher implements Callable<Void> {
	
	Context context ;//= ZMQ.context(1);
	Socket pubServer;
	HashSet<String> connectedAgents;
	String myId;
	
	public Publisher(Context context2, String edgeAgent, HashSet<String> connectedSet) {
		context = context2;
		myId = edgeAgent;
		pubServer = context.socket(ZMQ.PUB);
    	this.connectedAgents = connectedSet;
    	pubServer.bind(Constants.traficDataPublisger); 
    	System.out.println("Traffic data is publishing to the services from socket: "+Constants.traficDataPublisger);
     	
	}
    
	@Override
	public Void call() throws Exception {
		int update_nbr; 
		byte[]  msg = makeMessage();
		for (update_nbr = 0; update_nbr < 2; update_nbr ++) {
			pubServer.send(msg);
			
		} 
		System.out.println("Traffic update sent to the running services: "+new String(msg));
         
        
	        return null;  
	}
	
	private byte[] makeMessage() {             
        
        	String constr = "traffic"+":"+myId+"!Connected:";
     		String hoststr = "";
     		 constr += connectedAgents.size()+":Time:";
             //hoststr += hostedList.size()+":";
             //hoststr += Constants.maxTime+":";
             constr += Constants.maxTime;
            // pub(hoststr);
             
             byte[] payload = constr.getBytes();        
            
             
        return payload;
    }

	
	public void close() {
		pubServer.close (); 
		context.term ();
	}
}
