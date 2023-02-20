package EPOS;
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
	
	int waitAndReceive(String[] plans, boolean[] flag) {
    	
		System.out.println("Distributor waiting for plans!");
    	
    	HashSet<Integer> planSet = new HashSet<Integer>(); 
    	consumer.setReceiveTimeOut(2000);
    	int messageNum = 0; 
    	long elapsed ;
    	long startTime = System.currentTimeMillis();
        while (messageNum < receiveCount) {
                // Use trim to remove the tailing '0' character
                //messages.add(subscriber.recvStr(0).trim());
    			//System.out.println("waiting...");
    			byte[] message = consumer.recv();
    			if (message != null) {
        	        String triggerMsg = new String(message);
	                StringTokenizer msg = new StringTokenizer(triggerMsg,"!");
	                int index = Integer.parseInt(msg.nextToken());//edgeAgent id
	                if (!planSet.contains(index)){ //check if the request is already recorded     
	                	System.out.println("Received message from "+index+" ["+triggerMsg+"]");
	 	                plans[index] = msg.nextToken();
						flag[index] = true;
						planSet.add(index);
						messageNum++;
	                }
    			}
                elapsed = System.currentTimeMillis() - startTime;
		        if (elapsed > 50000) {
		        	if (messageNum > 0) {
		        	System.out.println("No more plans received from other EdgeAgents after "+elapsed+" miliseconds");
		        	break;
		        	}
		        }
    	}
    	return messageNum;
    }
	void open() {
		consumer.bind(Constants.SRVDISCON);
		
	}


	void close() {
		consumer.close();
		context.close();
	}
                
}
