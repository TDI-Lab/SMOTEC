package vehicle;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;


/**
 * @author zeinab
 * This program enables a mobile node to communicate with SMOTEC (send/receive messages) and receive traffic monitoring services 
 */
public class MobileAgent {
	private static List<EdgeNode> edgeAgents = new ArrayList<>();
	private static int vehicleAgentInd;
	static Context context = ZMQ.context(1);
	
	/**
	 * @param args 
	 * arguments received are: VehicleAgentId, Access points coverage range, (service requests including: CPU (mips), memory(MB), storage(MB)), mobility_directory 
	 * This program is an abstraction of a mobile agent which handles agent's mobility and its communication with the edge network 
	 * It manages sending service requests and receiving traffic monitoring services from the edge nodes (EdgeAgents) via its View class.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		Constants.conf = new File("").getAbsolutePath()+"/src/conf/TestbedConfig.json";//configuration file of SMOTEC
		
		System.out.println("VehicleAgent "+args[0]+" started.....");
		vehicleAgentInd = Integer.parseInt(args[0])-10000;
        Constants.cpu = Integer.parseInt(args[1]);
        Constants.mem = Integer.parseInt(args[2]);
        Constants.storage = Integer.parseInt(args[3]);
        String mobDir = args[4];
        Constants.numMsg = 0;//saves the communication cost of the agent with SMOTEC
		Constants.filePath = new File("").getAbsolutePath();
		Constants.mobDir = Constants.filePath+"/src/Mobility_Dataset/"+mobDir+"/";
		
		
		
        System.out.println("Input Arguments: cpu demand="+Constants.cpu+" memory demand="+Constants.mem+" storage demand="+Constants.storage+" directory of mobility profile="+Constants.mobDir);
        
		read_infrastructure(Constants.conf, vehicleAgentInd);
		Vehicle veh = new Vehicle(args[0]);
		getMobilityProfile(veh, Constants.mobDir);
		Client client = new Client(veh, context);//zeromq client
		
		String edgeServerAdd;
		String newEdgeServerAdd;
		int ConnectedEdgeAgent = -1;// At the beginning a mobile agent is not connected to any access point (EdgeAgent)
		int numOfAP = 0;
		/** 
		 * array to keep the three closest EdgeAgents to which the mobile agent will try to connect when it is in initial location
		 */
		int[] AP = new int[] {-1,-1,-1};
		numOfAP = Distance.closestEdgeAgents(edgeAgents, veh, AP);
		System.out.println("Closest EdgeAgents at time "+veh.getTravelTime()+" : "+ AP[0]+", "+ AP[1]+", "+AP[2]);
		
		//while there is not any EdgeAgents around keep moving and searching for EdgeAgents:
		while (numOfAP == 0) {
			
			Mobility.newlocation(veh);
			AP = new int[] {-1,-1,-1};
			numOfAP = Distance.closestEdgeAgents(edgeAgents, veh, AP);
			//if the vehicle is out of the city:
			if (!veh.isStatus()) {
				System.out.println("Out of coverage of EdgeAgents");
				System.exit(0);
							
			}
		}
		
		int hostId = -1, i = 0;
		
		/**
		 * keeps sending connection requests to the EdgeAgents until get connected to one:
		 */
		while ((hostId == -1) && (i<numOfAP)) {
				ConnectedEdgeAgent = AP[i];	
				if (ConnectedEdgeAgent != -1) {
					try 
					{
						System.out.println("VehicleAgent "+veh.getName()+" trying to connect to edgeAgent "+ConnectedEdgeAgent);
						edgeServerAdd = "tcp://edge"+ConnectedEdgeAgent+"-listen:"+(Constants.edgePortTopUp+ConnectedEdgeAgent);//EdgeAgent listening port
						hostId = client.sendRequest(edgeServerAdd, ConnectedEdgeAgent, Constants.CONNREQ);//send connection request to the EdgeAgent
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				
			i++;
			if (i == numOfAP)//infinite loop until connect to an AP and receive a service host
				i = 0;
		}
		 
			      
		//received a service host
		if(hostId != -1) {
			veh.setConnectedEdge(edgeAgents.get(ConnectedEdgeAgent));//set connected EdgeAgent
			veh.setHostServerCloudlet(edgeAgents.get(hostId));//set HostNode
			veh.toString();
			System.out.println("VehicleAgent "+veh.getName()+" connected to the host EdgeAgent "+veh.getHostNode().id+ " via EdgeAgent "+ConnectedEdgeAgent);
		}
		
		

		//goes to sleep until its service container is up and running:
		Thread.sleep(15000);
		//connects to the service(collector) container running on its HostNode:
		int CollectorPort = veh.getMyId() + Constants.collectorPortTopUp ;
		String traficDataPublisger = "tcp://srv"+veh.getMyId()+"-service:" + CollectorPort;//collector service port
		
		Callable<Void> target = new View(context, traficDataPublisger, hostId); 
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
              try {
                  target.call();
              }
              catch(Exception ex) {
                  System.out.println(ex.getMessage());
              }
          }, 2, 2, TimeUnit.SECONDS);
        
        
		/*
		 * keeps updating VehicleAgent location and its connected EdgeAgent according to the coverage range of EdgeNodes:
		 */
		while(true) {
			TimeUnit.SECONDS.sleep(1);
			Mobility.newlocation(veh);
			System.out.println("VehicleAgent TrafficMonitoring service hosted on EdgeAgent "+veh.getHostServerCloudlet().id);
			
			/*
			 * the agent moves out of the city borders or its travel is ended
			 */
			if (!veh.isStatus()) 
				break;
				
			
			double distance = Distance.calDistance(veh.getCoord(),veh.getConnectedEdge().getCoord());
			
			//still in coverage range of its connected EdgeAgent:
			if (distance < (Constants.AP_COVERAGE - Constants.HandofRange)) {
				System.out.println("VehicleAgent "+veh.getMyId()+" still connected to the EdgeAgent "+veh.getConnectedEdge().id+" in distance "+distance);
			}
			
			//in hand over area: (distance > (Constants.AP_COVERAGE - Constants.Handoff))
			//hand off from its connected EdgeAgent and try with next closest EdgeAgent
			else {
			
				System.out.println("VehicleAgent "+veh.getMyId()+" in distance "+distance+ " from EdgeAgent "+veh.getConnectedEdge().id+" asks for handover..");
				AP = new int[] {-1,-1,-1};
				numOfAP = Distance.closestEdgeAgents(edgeAgents, veh, AP);
				System.out.println("Next closest EdgeAgents: "+ AP[0]+", "+ AP[1]+", "+AP[2]);
				
				int connectedEdgeId = -1;
				i = 0;
				/**
				 * keeps sending connection requests to the nearby EdgeAgents until gets connected to one:
				 */
				while ((connectedEdgeId == -1)&&(i<numOfAP)) {
						ConnectedEdgeAgent = AP[i];	
						if (ConnectedEdgeAgent != -1) {
							try {
								System.out.println("VehicleAgent "+veh.getName()+" trying to handover from EdgeAgent "+veh.getConnectedEdge().id+" to EdgeAgent: "+ConnectedEdgeAgent);
								
								edgeServerAdd = "tcp://edge"+veh.getConnectedEdge().id+"-listen:"+(Constants.edgePortTopUp+veh.getConnectedEdge().id);// current connected EdgeAgent
								newEdgeServerAdd = "tcp://edge"+ConnectedEdgeAgent+"-listen:"+(Constants.edgePortTopUp+ConnectedEdgeAgent);// next EdgeAgent
								connectedEdgeId = client.handover(edgeServerAdd, newEdgeServerAdd, ConnectedEdgeAgent, veh.getConnectedEdge().id, Constants.RECOREQ);
							    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					i++;
					if (i == numOfAP)//infinite loop until connects to a new EdgeAgent
						i = 0;
				}
				if (connectedEdgeId != -1)
					System.out.println("Handover done successfully");
				
				veh.setConnectedEdge(edgeAgents.get(connectedEdgeId));
				veh.toString();
				
			}	
			
		}//end while
		
		
		//end of MobilityProfile: close connections and shut down
		edgeServerAdd = "tcp://edge"+veh.getConnectedEdge().id+"-listen:"+(32300+veh.getConnectedEdge().id);
		client.down(edgeServerAdd, veh.getConnectedEdge().id, Constants.DOWNREQ);//sends down notification to its connected EdgeAgent
		executor.shutdownNow();
		System.out.println("Agent "+veh.getMyId()+" leaving the city.");
		client.stop();
	}

	/**
	 * reads the configuration .json file of SMOTEC including the characteristics of network: Edge Nodes and their location/coverage range
	 */
	private static void read_infrastructure(String infraFile, int index) {
		JSONParser parser = new JSONParser();
		
	    try {
	        Object obj = parser.parse(new FileReader(infraFile));
			
	        JSONArray jsonObjects =  (JSONArray) obj;

	        for (Object o : jsonObjects) {
	            JSONObject jsonObject = (JSONObject) o;
	            
	            //city coordinates
	            Constants.MAX_X = (int) (long) jsonObject.get("max_x");
	    		Constants.MIN_X = (int) (long) jsonObject.get("min_x");
	    		Constants.MIN_Y = (int) (long) jsonObject.get("min_y");
	    		Constants.MAX_Y = (int) (long) jsonObject.get("max_y");
	    		Constants.AP_COVERAGE = (int) (long) jsonObject.get("AP_COVERAGE");
	    		
	    		//edge nodes
	    		Constants.numEdgeNodes = (int) (long) jsonObject.get("NumEdgeNodes");
	            JSONArray jsonArrayX= (JSONArray) jsonObject.get("Xpoints");
	            JSONArray jsonArrayY= (JSONArray) jsonObject.get("Ypoints");
	            
	            for(int i=0; i<jsonArrayY.size(); i++){
	            	edgeAgents.add(i, new EdgeNode(i, (int) (long)(jsonArrayX.get(i)),(int) (long)(jsonArrayY.get(i))));
	            }
	        }  
		      System.out.println("Number of EdgeAgents around: "+edgeAgents.size());
                
	        
	    
			}catch (FileNotFoundException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
		
	}
	
		/**
	 * @param mobileVehicle agent
	 * reads the mobilityprofile of vehicleagent (SUMO mobility files) 
	 */
	public static void getMobilityProfile(Vehicle mobileVehicle, String mobDir){	
		System.out.println("Reading mobility profile...");
		String line = "";
		String csvSplitBy = ",";
		String pathToMobility = mobDir+mobileVehicle.getMyId()+".csv";
			try (BufferedReader br = new BufferedReader(new FileReader(pathToMobility))) {

				while ((line = br.readLine()) != null) {
					String[] position = line.split(csvSplitBy);
					mobileVehicle.getPath().add(position);
				}

				Mobility mobility = new Mobility();
				mobility.setInitialProfile(mobileVehicle);
			
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

}
