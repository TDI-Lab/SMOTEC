package EPOS;

import java.util.Map;
import java.util.StringTokenizer;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;



/**
 * @author Zeinab
 * Consumes service placement plans received from EdgeAgents
 *
 */
public class Consumer {
	
	long messagesReceived = 0;
	int receiveCount = 2;
	Context context;
	Socket consumer;
	
	public Consumer(Context context2) {
		
		context = context2;
		consumer = context.socket(ZMQ.PULL);
	}
	
	int waitAndReceive(String[] plans, String[] bplans, boolean[] flag, Map<Integer, Integer> edgeTimeSet) {
    	
		System.out.println("Distributor waiting for plans!");
    	
    	consumer.setReceiveTimeOut(2000);
    	int messageNum = 0; 
    	long elapsed ;
    	long startTime = System.currentTimeMillis();
        while (messageNum < receiveCount) {
                
        	byte[] message = consumer.recv();
			if (message != null) {
    	        String triggerMsg = new String(message);
                StringTokenizer msg = new StringTokenizer(triggerMsg,"!");
                
                //received message format: "EPOS!"+edgeAgentIndex+"!"+Constants.maxTime+"!";
                if (msg.nextToken().compareTo("EPOS") == 0) {//first token: EPOS
	                int edgeindex = Integer.parseInt(msg.nextToken());//second token: EdgeAgent id
	                int sentTime =  Integer.parseInt(msg.nextToken());//third token: time
	                
	                if (!edgeTimeSet.containsKey(edgeindex)){ //checks if the request is already recorded for this EdgeAgent   
	                	System.out.println("Received message from EdgeAgent "+edgeindex);
	                	System.out.println("[\n"+triggerMsg+"]");
	 	                plans[edgeindex] = msg.nextToken();//forth token: utilization plan of EdgeAgent
	 	                bplans[edgeindex] = msg.nextToken();//fifth token: binary plan of EdgeAgent
						flag[edgeindex] = true;
						edgeTimeSet.put(edgeindex,sentTime);
						messageNum++;
	                }
	                else {
	                	if(edgeTimeSet.get(edgeindex) < sentTime) {//if the request is a new request from an EdgeAgent
	                		System.out.println("Received message from EdgeAgent "+edgeindex);
		                	System.out.println("[\n"+triggerMsg+"]");
		 	                plans[edgeindex] = msg.nextToken();
		 	                bplans[edgeindex] = msg.nextToken();
							flag[edgeindex] = true;
							edgeTimeSet.put(edgeindex,sentTime);
							messageNum++;
	                	
	                	}
	                }
	                }
    			}
				/*
				 * elapsed = System.currentTimeMillis() - startTime; if (elapsed > 120000) { if
				 * (messageNum > 0) {
				 * System.out.println("No more plans received after "+elapsed+" miliseconds");
				 * break; } }
				 */
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
