package pi;

import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class Server {
	// Socket to talk to clients
    Context context;// = ZMQ.context(1);
	Socket Server ;
	String myId;
	List<Request> requests;
	HashSet<String> connectedSet;
	private List<Request> recorequests;
	private List<Request> downrequests;
	private List<Request> disrequests;
	HashSet<String> requesSet;
	
	public Server(Context context2, String add, String myid) {
		context = context2;
		Server = context.socket(ZMQ.REP);
		myId = myid;
		Server.bind(add);
		System.out.println("listener add: "+add);
		Server.setReceiveTimeOut(1000);
       
	    	
	}

	public int startAndWaitForRequests(List<Request> requests, HashSet<String> connectedSet, List<Request> recorequests, List<Request> disrequests, List<Request> downrequests) {
	    	this.requests = requests;
	    	this.connectedSet = connectedSet;
	    	this.recorequests = recorequests;
	    	this.disrequests = disrequests;
	    	this.downrequests = downrequests;
	    	System.out.println("Server lunched and waiting for requests");
	    	//Server.bind(add);
	    	
	    	long elapsed ;
	        String recvValue, response;
	        Server.setReceiveTimeOut(2000);
	        int i = 0;
	        
	        long startTime = System.currentTimeMillis();
	        while (true) {
		        byte[] recv = Server.recv();
		        if (recv != null) {
				        i++;
				        recvValue = new String(recv);
				        System.out.println("0Received request "+i+": ["+recvValue+"]");
				        
				        response = processRequests(recvValue, i);
				        //publish ack to the sender agent:
				        if (response !=  null)
				        	Server.send((response).getBytes(), 0);
			            
			            }
				elapsed = System.currentTimeMillis() - startTime;
		        if (elapsed > 20000) {
		        	System.out.println("Server shuting down at time: "+elapsed);
		        	break;
		        }
				        
	        }
	        System.out.println("request size: "+requests.size()+" "+connectedSet.size());
	        
	        return requests.size();
	        
	    }

	
	
	
	/**
	 * @param EdgeNode waits for requests from agents
	 * @return a hashset containing received requests at this time period
	 * @throws Exception
	 */
	private String processRequests(String recvValue, int i) {
		HashSet<String> requesSet = new HashSet<String>(); 
		String vehAgent;
		int curHost = -1;
		StringTokenizer msg = new StringTokenizer(recvValue,":");
        String type = msg.nextToken();
        String dest = msg.nextToken();
        System.out.println("1Received request "+i+": ["+recvValue+"]");
        vehAgent = msg.nextToken();//request id == agent id
        String C = msg.nextToken();//cpu resource requirement
        String M = msg.nextToken();//memory resource requirement
        String S = msg.nextToken();//storage resource requirement
        String T = msg.nextToken();//time of request initiated
        
        if (dest.compareTo(myId) != 0)
        	return null;
        
        String resp = type+":"+myId+":"+vehAgent;
        
        //record connection msg:
    	if (type.compareTo(Constants.ConReqTOPIC) == 0) {
    		    	
    		    	if (!requesSet.contains(vehAgent)){ //check if the request is already recorded     
    		            int iTime = Integer.parseInt(T);
    		            if (iTime > Constants.maxTime)
    		            	Constants.maxTime = iTime;//update current time
    	            	requests.add(new Request(vehAgent, C, M, S, Constants.CONREQ ,iTime, -1));
    	            	connectedSet.add(vehAgent);
    	            	//System.out.println("conn request "+i+": ["+recvValue+"]");
    	            	requesSet.add(vehAgent);
    	            	}
    	}
    	
    	//record reconnect msg:
    	if (type.compareTo(Constants.RecReqTOPIC) == 0) {
    	            curHost = Integer.parseInt(msg.nextToken());//current host of its service
    		    	//String preAP = msg.nextToken();//previous AP the agent is connecting to
    		    	 if (!requesSet.contains(vehAgent)){ //check if the request is already recorded     
    		            int iTime = Integer.parseInt(T);
    		            if (iTime > Constants.maxTime)
    		            	Constants.maxTime = iTime;//update current time
    	            	recorequests.add(new Request(vehAgent, C, M, S, Constants.RECREQ, iTime, curHost));
    	            	connectedSet.add(vehAgent);
    	            	requesSet.add(vehAgent);
    		    	 }
    	}
    	
      //record disconnect msg:
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
	 //record down msg:
     if (type.compareTo(Constants.DownReqTOPIC) == 0) {
    		    	 curHost = Integer.parseInt(msg.nextToken());//host
    				  if (!requesSet.contains(vehAgent)){ //check if the request is already recorded or if its payload if empty     
    			            int iTime = Integer.parseInt(T);
    			            if (iTime > Constants.maxTime)
        		            	Constants.maxTime = iTime;//update current time
        	            	//downrequesSet.add(vehAgent);
    		            	downrequests.add(new Request(vehAgent, C, M, S, Constants.DOWN, iTime, curHost));
    		            	requesSet.add(vehAgent);
    				  }	
     }     
     
    		            	
    //int request_size = requests.size()+downrequests.size()+disrequests.size()+recorequests.size();
    return resp; 
        
        
	}
    		   
	public void stop() {
		Server.close();
        context.term();
	}
	
}
