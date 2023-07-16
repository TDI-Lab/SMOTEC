package pi;

import java.util.StringTokenizer;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * @author zeinab
 * This class listens on SRVDISCON to receive selected plans from service distributor
 *
 */
public class Consumer {
	
		long messagesReceived = 0;
		int receiveCount = 2;
		Context context;
		Socket consumer;
		
	public Consumer(Context context2) {
			context = ZMQ.context(1);
			consumer = context.socket(ZMQ.PULL);
	}
	
	/**
	 * @return the selected hosts for its received service requests
	 */
	public int waitAndReceive() {
			System.out.println("EdgeAgent waiting for selected plan from service distributor!");
	    	int planIndex = -1; 
	    	long elapsed ;
	    	long startTime = System.currentTimeMillis();
	        while (planIndex == -1) {
	                byte[] message = consumer.recv();
	                if (message != null) {
	    	            String triggerMsg = new String(message);
		                StringTokenizer msg = new StringTokenizer(triggerMsg,"!");
		                String distributor = msg.nextToken();//EPOS
		                System.out.println("Received message from Service distributor: ["+triggerMsg+"]");
		                if (distributor.compareTo("EPOS") == 0){  
		                	System.out.println("Received message matched with EPOS");
		                    planIndex = extractPlanIndex(msg.nextToken());
		                    break;
		                }
	                }
	                elapsed = System.currentTimeMillis() - startTime;
			        if (elapsed > 80000) {
			        	System.out.println("Nom. of response received from service distributor after "+elapsed+" miliseconds");
			        	break;
			        }
					     
	    	}
	    	return planIndex;
	    	
	    }
	private int extractPlanIndex(String msg) {
			 StringTokenizer str = new StringTokenizer(msg.toString(),":"); 
			 for (int l = 0 ; l<2*((Constants.edgeAgentIndex))+1 ; l++) 
				 str.nextToken();
					  
			 return Integer.parseInt(str.nextToken());//selected host		  
					  
		}
					 
		
	public void open() {
			consumer.connect(Constants.SRVDISCON);
			
		}
	
	
	public void close() {
			consumer.close();
			
		}
                
}
