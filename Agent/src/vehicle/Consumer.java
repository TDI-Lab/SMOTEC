package vehicle;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;



public class Consumer {
	long messagesReceived = 0;
	int receiveCount = 2;
	Context context = ZMQ.context(1);
	Socket consumer = context.socket(ZMQ.PULL);
	
	public int waitAndReceive(String i) {
    	
		System.out.println("VehicleAgent waiting for selected host!");
		consumer.setReceiveTimeOut(3000);//check more
    	int hostIndex = -1; 
    	long elapsed ;
    	long startTime = System.currentTimeMillis();
        while (hostIndex == -1) {
                byte[] message = consumer.recv();
                if (message != null) {
    	            String triggerMsg = new String(message);
	                StringTokenizer msg = new StringTokenizer(triggerMsg,":");
	                String token = msg.nextToken();//"host-vehAgent"
	                
	                System.out.println("Received message from connected EdgeAgent: ["+triggerMsg+"]");
	                if (token.compareTo("host-"+i) == 0){  
	                	System.out.println("Received message match with my id");
	                    hostIndex = Integer.parseInt(msg.nextToken());
	                    break;
	                }
                }
                elapsed = System.currentTimeMillis() - startTime;
		        if (elapsed > 90000) {
		        	System.out.println("No response received from EdgeAgent after "+elapsed+" miliseconds");
		        	break;
		        }
				     
    	}
    	return hostIndex;
    	
	}		 
	
	void open(String prodAdd) {
		consumer.connect(prodAdd);
		
	}


	void close() {
		consumer.close();
		context.close();
	}
                
}
