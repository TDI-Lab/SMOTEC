package vehicle;

import java.util.StringTokenizer;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;



public class Consumer {
	long messagesReceived = 0;
	int receiveCount = 2;
	Context context;
	Socket consumer ;
	
	Consumer(Context context2) {
		context = context2;
		consumer = context.socket(ZMQ.PULL);
	}
    
	/**
	 * @param vehAgentId
	 * waits to receive a response from edge network which is matched with its id
	 * @return
	 */
	public int waitAndReceive(String vehAgentId) {
    	
		System.out.println("Waiting for messages from EdgeAgent");
		//consumer.setReceiveTimeOut(3000);
    	int hostIndex = -1; 

        while (hostIndex == -1) {
                byte[] message = consumer.recv();
                if (message != null) {
    	            String triggerMsg = new String(message);
	                StringTokenizer msg = new StringTokenizer(triggerMsg,":");
	                String token = msg.nextToken();//"host:vehAgent"
	                
	                System.out.println("Received message from connected EdgeAgent: ["+triggerMsg+"]");
	                if ((token.compareTo("host") == 0) && (msg.nextToken().compareTo(vehAgentId) == 0)){  
	                	System.out.println("Received message match with my id");
	                    hostIndex = Integer.parseInt(msg.nextToken());
	                    break;
	                }
                }
				/*
				 * elapsed = System.currentTimeMillis() - startTime; if (elapsed > 100000) {
				 * System.out.println("No response received from EdgeAgent after "
				 * +elapsed+" miliseconds"); break; }
				 */ 
    	}
    	return hostIndex;
    	
	}		 
	
	void open(String prodAdd) {
		consumer.connect(prodAdd);
		
	}


	void close() {
		consumer.close();
	}
                
}
