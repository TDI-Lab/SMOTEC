package EPOS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import experiment.IEPOSExperiment;


public class Distributor {


	static String brokerUrl;
	static boolean []flag;
	static HashSet<String> planSet;
	static String [] plans;
	//@SuppressWarnings("static-access")
	public static void main(String [] args) {
		    System.out.println("Service distributor started and running...");
		    String conf = new File("").getAbsolutePath()+"/src/TestbedConfig.json";
			
		    Constants.SRVDISPRO = "tcp://localhost:5501";//plan producer
			Constants.SRVDISCON = "tcp://localhost:5500";//plan consumer
			plans = new String[3];
		    flag = new boolean[3];
		    
	
		    //brokerUrl = "tcp://localhost:1883";;// "tcp://mosquitto:1883";
		    
		    //?????
			//while(true) {
			
			Consumer co = new Consumer();
		    co.open();
		    int numReceivedPlans = co.waitAndReceive(plans, flag);
			co.close();
			
			
            if(numReceivedPlans >= 1) {
				System.out.println("EPOS writing plans..");
				writePlans(plans);
				IEPOSExperiment iepos = new IEPOSExperiment();
				iepos.main(args);
	        
				Utility.sendSelectedPlans();
			    System.out.println("EPOS results are on the way....");
				
            }
            
            
            updateSettings();
		 
	}
	
	private static void updateSettings() {
		// TODO Auto-generated method stub
		planSet = new HashSet<String>() ;
		plans = new String[3];
	    //flag = new boolean[3];
	    flag[0] = flag[1] = flag[2] =false;
	    
	}

	private static void waitAndResponse(Socket distributor) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
        System.out.println("launch and wait server.");
        String recvValue ;
        distributor.setReceiveTimeOut(2000);
        
        int i = 0;
        while (true) {
	        byte[] recv = distributor.recv();
	        if (recv != null) {
	        	distributor.send("pong".getBytes(), 0);
		            recvValue = new String(recv);
		            //System.out.println("Received " + recvValue);
			        StringTokenizer str = new StringTokenizer(recvValue,":");//recv.toString()
		            str.nextToken();
		            
		            System.out.println("Received request "+i+" ["+recvValue+"]:"+str.nextToken());
		            
		            i++;
		            }
			      long elapsed = System.currentTimeMillis() - startTime;
	        if (elapsed > 80000) {
	        	System.out.println("Server shuting down at time: "+elapsed);
	        	break;
	        }
			        
        }
        distributor.close();
        
	}

	

	private static void writePlans(String[] plans) {
		String datasets = new File("").getAbsolutePath()+("/datasets/Utilization/");
		// TODO Auto-generated method stub
		for (int i = 0; i<2; i++) {
			if (flag[i]) {
				System.out.println("Writing plans for EdgeAgent "+i);
				try (PrintWriter out = new PrintWriter(datasets+"agent_"+i+".plans")) {
					out.println(plans[i]);
				}
				catch (IOException e) 
			    {
			        e.printStackTrace();
			    }  
			}
	    }
		
		
	}
}
