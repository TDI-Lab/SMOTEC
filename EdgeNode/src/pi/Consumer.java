package pi;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;



public class Consumer {
	long messagesReceived = 0;
	int receiveCount = 2;
	Context context;
	Socket consumer;
	
	public Consumer(Context context2) {
	
		context = ZMQ.context(1);
		consumer = context.socket(ZMQ.PULL);
	}
	public int waitAndReceive() {
    	
		System.out.println("EdgeAgent waiting for selected plan!");
		consumer.setReceiveTimeOut(2000);//check more
    	int planIndex = -1; 
    	long elapsed ;
    	long startTime = System.currentTimeMillis();
        while (planIndex == -1) {
                byte[] message = consumer.recv();
                if (message != null) {
    	            String triggerMsg = new String(message);
	                StringTokenizer msg = new StringTokenizer(triggerMsg,"!");
	                String distributor = msg.nextToken();//EPOS
	                System.out.println("Received message from service distributor: ["+triggerMsg+"]");
	                if (distributor.compareTo("EPOS") == 0){  
	                	System.out.println("Received message matched with EPOS");
	                    planIndex = extractPlanIndex(msg.nextToken());
	                    break;
	                }
                }
                elapsed = System.currentTimeMillis() - startTime;
		        if (elapsed > 40000) {
		        	System.out.println("No response received from service distributor after "+elapsed+" miliseconds");
		        	break;
		        }
				     
    	}
    	return planIndex;
    	
    }
	private int extractPlanIndex(String msg) {
		// TODO Auto-generated method stub
		 StringTokenizer str = new StringTokenizer(msg.toString(),":"); 
		 for (int l = 0 ; l<2*((Constants.edgeAgentIndex))+1 ; l++) 
			 str.nextToken();
				  
		 return Integer.parseInt(str.nextToken());//selected host
				  
				  
				  
	}
				 
	
	void open() {
		consumer.bind(Constants.SRVDISCON);
		
	}


	void close() {
		consumer.close();
		//context.close();
	}
                
}
