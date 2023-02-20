package pi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class Edgenode {
	static int myId;
	static String edgeAgent;
	//static int maxTime = 0;
	static int timeline = 500; 

	//private static List<SRequest> simulationRequests = new ArrayList<>();//all received connection requests during the experiment 
	private static List<Integer> hostedList = new ArrayList<>();
	private static Map<Integer, Integer> SrvToHostSet = new HashMap<>();//keeps the mapping between agents and service hosts 
	
	private static List<Neighbor> neighbors = new ArrayList<>();
	private static int planIndex = -1;
	
	private static List<Request> requests = new ArrayList<>();//received connection requests 
	private static HashSet<String> connectedSet = new HashSet<String>(); //saves current connections to agents 
	private static List<Request>  recorequests = new ArrayList<>();
	private static HashSet<String> disrequesSet = new HashSet<String>(); 
	private static List<Request>  disrequests = new ArrayList<>();
    private static HashSet<String> downrequesSet = new HashSet<String>(); 
	private static List<Request>  downrequests = new ArrayList<>();
	//private static Socket requesterDis;    
	private static ArrayList reconnectedSet;
	static Context context = ZMQ.context(1);
	
	/**
	 * @param args id of edge node which is input of corresponding container deployment command of k3s
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		edgeAgent = args[0];//edgeIid == index in list of neighbours
		Constants.edgeAgentIndex = Integer.parseInt(args[0]);
		String myname = "edge"+edgeAgent;
		
		Constants.SRVDISPRO = "tcp://localhost:5500";
		Constants.SRVDISCON = "tcp://localhost:5501";
		
		Constants.reqListener = "tcp://localhost:"+(5700+Constants.edgeAgentIndex);//myname+port
		
		Constants.traficDataPublisger = "tcp://localhost:"+(5600+Constants.edgeAgentIndex);
		
		String basePath = new File("").getAbsolutePath();
		Constants.conf = basePath+"/src/conf/TestbedConfig.json";
		Constants.bp = basePath;//+"/output";
		Constants.PlanBinaryDataset = basePath+"/datasets/Binary/";
		Constants.PlanUtilDataset = basePath+"/datasets/Utilization/";
		
		Constants.deployDir = basePath+"/src/deployments/";
		Constants.srvDeployScript = basePath+"/src/srvDeploy.sh ";
		Constants.srvReleaseScript = basePath+"/src/srvRelease.sh";
		
		read_neighbors(Constants.conf);
		System.out.println("EdgeAgent "+edgeAgent+" is starting at time "+Constants.maxTime);
		System.out.println("Config file: "+Constants.conf);
		
		Server server = new Server(context, Constants.reqListener, edgeAgent);
		ResourceMgm rm = new ResourceMgm(context, edgeAgent, neighbors);
		
		//publish number of current connection/hosted requests on this edge node to MQTT:
				
		//time
		Callable<Void> target = new Publisher(context, edgeAgent, connectedSet); //not updated
        
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
              try {
                  target.call();
              }
              catch(Exception ex) {
                  throw new RuntimeException(ex);
              }
          }, 2, 2, TimeUnit.SECONDS);
        
        	int run = 0;
			int numHostedOnMe=0;
			int size ;
	        while(true) {
	        	if (Constants.maxTime >= timeline) {
	        		System.out.println("Time horizon reached, edgeAgent going down");
	        		switchOff();
	        		break;
	        	}
	        	size = 0;
				while (size == 0) {
					size = server.startAndWaitForRequests(requests, connectedSet, recorequests, disrequests, downrequests);
			        System.out.println("EdgeAgent "+edgeAgent+" total received requests: "+ requests.size()+", handover: "+recorequests.size()+", down: "+downrequests.size());
			        
		        }
				 
				 
				//connectedSet.addAll(reconnectedSet);
				rm.initialize(requests, connectedSet, hostedList, SrvToHostSet);
				rm.manageRequests(recorequests, disrequests, downrequests);
				
			       
				/*
				 * Context contextDis = ZMQ.context(1); Socket requesterDis =
				 * contextDis.socket(ZMQ.REQ); requesterDis.connect("tcp://localhost:5558");
				 * 
				 * sendToDistributor(requesterDis); 
				 * contextDis.close();
				 */

				UpdateRequests();
				/*while (planIndex == -1) {
					sendPlan(rm.manageRequests());
				}
				//rm.SelectAndDeploy(args, SrvToHostSet);
				*/
				
				
				//sendReports();
            
            System.out.println("EdgeAgent: "+edgeAgent+", time: "+Constants.maxTime+", number of connected vehicles: "+connectedSet.size()+", number of hosted: "+numHostedOnMe);
			//Utility.WriteResults(simulationRequests, maxTime);//every five minutes
	        //System.out.println("finished !");
         // System.exit(0);

        }
        System.out.println("End of time line (one hour) reached, EdgeAgent "+edgeAgent+" is powering off !");
        executor.shutdown();
        server.stop();
	       
	}
	    

	/*
	 * private static void sendToDistributor(Socket requesterDis) { // TODO
	 * Auto-generated method stub requesterDis.setReceiveTimeOut(2000);
	 * 
	 * System.out.println("launch and connect to distributor."); String replyValue ;
	 * boolean flag = false; //while flag == false
	 * requesterDis.setReceiveTimeOut(2000);
	 * 
	 * for (int request_nbr = 0; request_nbr < 2; request_nbr++) {
	 * requesterDis.send(("ping:"+edgeAgent).getBytes(), ZMQ.NOBLOCK);
	 * 
	 * byte[] reply = requesterDis.recv();
	 * 
	 * if (reply != null) { replyValue = new String(reply);
	 * System.out.println("Received reply "+request_nbr+" ["+replyValue+"]"); }
	 * 
	 * }
	 * 
	 * // We never get here but clean up anyhow requesterDis.close();
	 * 
	 * }
	 */





	/**
	 * remove already hosted requests be redefining requests list
	 */
	private static void UpdateRequests() {
		// TODO Auto-generated method stub
		requests = new ArrayList<>();
		downrequests = new ArrayList<>();
		disrequests = new ArrayList<>();
		recorequests = new ArrayList<>();
		
		
	}


	
	private static void switchOff() {//release hosted agents also
		// TODO Auto-generated method stub
		try {
			String ShCommand = "bash "+Constants.srvReleaseScript+ " edge"+edgeAgent ;
		    Process p = Runtime.getRuntime().exec(ShCommand);
		    p.waitFor();
		    
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		    
		    String line = "";
		    
		    while ((line = reader.readLine()) != null) {
		        System.out.println(line);
		    }

		    line = "";
		    while ((line = errorReader.readLine()) != null) {
		        System.out.println(line);
		    }
		
		    } catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}


		    

	
	


	/**
	 * read the edge nodes location and their resource characteristics
	 * @param infrastructure 
	 */
	private static void read_neighbors(String infrastructure) {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		
	    try {
	        Object obj = parser.parse(new FileReader(infrastructure));
			
	        JSONArray jsonObjects =  (JSONArray) obj;

	        for (Object o : jsonObjects) {
	            JSONObject jsonObject = (JSONObject) o;
	            int numNeighbors = (int) (long) jsonObject.get("edge_nodes");
	            //Constants.brokerUrl = (String) jsonObject.get("mosquittoBrokerUrl");
	            Constants.EPOS_NUM_PLANS = (int) (long) jsonObject.get("EPOSNumPlans");
	            JSONArray jsonArrayid= (JSONArray) jsonObject.get("EdgeId");
	            JSONArray jsonArrayX= (JSONArray) jsonObject.get("Xpoints");
	            JSONArray jsonArrayY= (JSONArray) jsonObject.get("Ypoints");
	            JSONArray jsonArrayCPU= (JSONArray) jsonObject.get("cpuCap");
	            JSONArray jsonArrayMem= (JSONArray) jsonObject.get("memCap");
	            JSONArray jsonArrayStorage= (JSONArray) jsonObject.get("storageCap");
	            
	            for(int i=0; i<numNeighbors; i++){
	            	neighbors.add(i, new Neighbor((int) (long)(jsonArrayid.get(i)),(int) (long)(jsonArrayX.get(i)),
	            			(int) (long)(jsonArrayY.get(i)),(int) (long)(jsonArrayCPU.get(i)),
	            			(int) (long)(jsonArrayMem.get(i)),(int) (long)(jsonArrayStorage.get(i))));
	            }
	        }  
		     // System.out.println("Number of edgeAgents in the network: "+neighbors.size());
                
	        
	    
	}catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ParseException e) {
        e.printStackTrace();
    }
		
	}
	
}
	
	
	