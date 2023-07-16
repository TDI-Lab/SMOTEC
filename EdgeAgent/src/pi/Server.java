package pi;

import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * @author zeinab
 * This class is in charge of receiving mobile agents requests and responding to them
 *
 */
public class Server {
	
    Context context;
	Socket Server;// Socket to talk to mobile clients
	String myId;
	
	HashSet<String> connectedset;
	List<Request> connrequests;
	private List<Request> recorequests;
	private List<Request> downrequests;
	private List<Request> disrequests;
	
	int totalrequests;
	
	public Server(Context context2, String add, String myid) {
		context = context2;
		Server = context.socket(ZMQ.REP);
		myId = myid;
		Server.bind(add);
		Server.setReceiveTimeOut(1000);
       
	    	
	}

	/**
	 * @param connRequests
	 * @param connectedSet
	 * @param recoRequests
	 * @param disRequests
	 * @param downRequests
	 * @return
	 * starts zeromq server and waits for requests. upon receiving every requests responds appropriately.
	 */
	public int startAndWaitForRequests(List<Request> connRequests, HashSet<String> connectedSet, List<Request> recoRequests, List<Request> disRequests, List<Request> downRequests) {
	    	this.connrequests = connRequests;
	    	this.connectedset = connectedSet;
	    	this.recorequests = recoRequests;
	    	this.disrequests = disRequests;
	    	this.downrequests = downRequests;
	    	HashSet<String> requesSet1 = new HashSet<String>(); 
	    	totalrequests = 0; 
	    	
	    	System.out.println("0MQ Server lunched and listening for service requests");
	    	
	    	long elapsed ;
	        String recvValue, response;
	        Server.setReceiveTimeOut(2000);
	        int i = 0;
	        
	        long startTime = System.currentTimeMillis();
	        while (true) {//listen for a specific time
		        byte[] recv = Server.recv();
		        if (recv != null) {
				        i++;
				        recvValue = new String(recv);
				        System.out.println("Received request "+i+": ["+recvValue+"]");
				        response = processRequests(recvValue, i, requesSet1);
				        
				        if (response !=  null)
				        	Server.send((response).getBytes(), 0);//sends ack to the sender vehicleagent:
				        	Constants.numMsg++;
				        }
				elapsed = System.currentTimeMillis() - startTime;
		        if (elapsed > 100000) {
		        	System.out.println("Server temporarily shuting down at time: "+elapsed);
		        	break;
		        }
				        
	        }
	        totalrequests = connrequests.size()+downrequests.size()+disrequests.size()+recorequests.size();
	        
	        return totalrequests;
	        
	    }

	
	
	
	/**
	 * @param requesSet 
	 * @param servedreq 
	 * @param hostDis 
	 * @param EdgeNode waits for requests from agents
	 * @return a hashset containing received requests at this time period
	 * @throws Exception
	 * Processes the received requests and makes a response message based on the received message type
	 */
	private String processRequests(String recvValue, int i, HashSet<String> requesSet) {
		int curHost = -1;
		System.out.println("Received request #"+i+": ["+recvValue+"]");
        
		StringTokenizer msg = new StringTokenizer(recvValue,":");
        String type = msg.nextToken();
        String dest = msg.nextToken();
        String vehAgent = msg.nextToken();//request id == agent id
        String C = msg.nextToken();//cpu resource requirement
        String M = msg.nextToken();//memory resource requirement
        String S = msg.nextToken();//storage resource requirement
        String T = msg.nextToken();//time of sender/request initiation
        
        if (dest.compareTo(myId) != 0)
        	return null;
        
        String resp = type+":"+myId+":"+vehAgent;//ack: message to the sender: vehicleagent
        
        //record/process connection msg:
    	if (type.compareTo(Constants.ConReqTOPIC) == 0) {
    		    	
    		    	if (!requesSet.contains(vehAgent)){ //check if the request is already recorded     
    		            int iTime = Integer.parseInt(T);
    		            if (iTime > Constants.maxTime)
    		            	Constants.maxTime = iTime;//update current time
    	            	connrequests.add(new Request(vehAgent, C, M, S, Constants.CONREQ ,iTime, -1));
    	            	connectedset.add(vehAgent);
    	            	requesSet.add(vehAgent);
    	            	
    	            }
    	}
    	
    	//record/process reconnect msg:
    	if (type.compareTo(Constants.RecReqTOPIC) == 0) {
    	            curHost = Integer.parseInt(msg.nextToken());//current host of its service
    		    	 if (!requesSet.contains(vehAgent)){ //check if the request is already recorded     
    		            int iTime = Integer.parseInt(T);
    		            if (iTime > Constants.maxTime)
    		            	Constants.maxTime = iTime;//update current time
    	            	recorequests.add(new Request(vehAgent, C, M, S, Constants.RECREQ, iTime, curHost));
    	            	connectedset.add(vehAgent);
    	            	requesSet.add(vehAgent);
    		    	 }
    	}
    	
      //record/process disconnect msg:
      if (type.compareTo(Constants.DisReqTOPIC) == 0) {
    		       curHost = Integer.parseInt(msg.nextToken());//current host of its service
    	            if (!requesSet.contains(vehAgent)){ //check if the request is already recorded or if its payload if empty     
    			            int iTime = Integer.parseInt(T);
    			            if (iTime > Constants.maxTime)
        		            	Constants.maxTime = iTime;//update current time
        	            	disrequests.add(new Request(vehAgent, C, M, S, Constants.DISREQ, iTime, curHost));
    		            	requesSet.add(vehAgent);
    	            }
      }
	 //record/process down msg:
     if (type.compareTo(Constants.DownReqTOPIC) == 0) {
    		    	 curHost = Integer.parseInt(msg.nextToken());//host
    				  if (!requesSet.contains(vehAgent)){ //check if the request is already recorded or if its payload if empty     
    			            int iTime = Integer.parseInt(T);
    			            if (iTime > Constants.maxTime)
        		            	Constants.maxTime = iTime;//update current time
        	            	downrequests.add(new Request(vehAgent, C, M, S, Constants.DOWN, iTime, curHost));
    		            	requesSet.add(vehAgent);
    				  }	
     }     
    
     return resp; 
        
        
	}
    		   
	public void stop() {
		Server.close();
        context.term();
	}
	
}
