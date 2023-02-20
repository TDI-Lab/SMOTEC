package vehicle;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;



public class Agent {
	private static List<EdgeNode> edgeAgents = new ArrayList<>();
	private static String servicehost;
	//private static String brokerUrl = "tcp://localhost:1883";
	static MqttClient mqttClient;
	private static int time = 0;
	private static int vehAgentInd;
	static Context context = ZMQ.context(1);
	
	/**
	 * @param args AgentId, CPU (mips), memory(MB), mobility_dir infrastructureFilePath: 10000 250 300 "/home/spring/Documents/Agent/src/Mobility_Dataset" "/home/spring/Documents/Agent/src/vehicle/infrastructure.json"
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		Constants.brokerUrl = "tcp://localhost:1883";
		System.out.println("VehAgent "+args[0]+" just deployed and started.....");
		Constants.conf = new File("").getAbsolutePath()+"/src/conf/TestbedConfig.json";
		vehAgentInd = Integer.parseInt(args[0])-10000;
		
		Constants.filePath = new File("").getAbsolutePath();
		Constants.mobDir = Constants.filePath+"/src/Mobility_Dataset/";
		
		read_infrastructure(Constants.conf, vehAgentInd);
		
		
		Vehicle v = new Vehicle(args[0]);
		
		read_mobility(v, Constants.mobDir);
		
		Client client = new Client(v, context);
		
		
		String edgeServerAdd;String newEdgeServerAdd;
		int ConnectedEdgeAgent = -1;// firstly agent is not connected to any access point
		/** 
		 * array to keep the three closest access points to which agent can connect in its initial coordination
		 */
		int numOfAP = 0;
		int[] AP = new int[] {-1,-1,-1};
		numOfAP = Distance.closestEdgeAgents(edgeAgents, v, AP);
		System.out.println("Closest EdgeAgent ids at time: "+v.getTravelTime()+" are: "+ AP[0]+", "+ AP[1]+", "+AP[2]);
		
		while (numOfAP == 0) {
			Coordinate.newCoordinate(v);
			AP = new int[] {-1,-1,-1};
			numOfAP = Distance.closestEdgeAgents(edgeAgents, v, AP);
			//System.out.println("Closest edgeAgent ids at time: "+v.getTravelTime()+" are: "+ AP[0]+", "+ AP[1]+", "+AP[2]);
			
			if (!v.isStatus()) {
				System.out.println("Out of coverage of EdgeAgents");
				//down
				System.exit(0);
							
			}
		}
		
		
		int hostId = -1, i = 0;
		
		/**
		 * keep sending connection requests to the APs until connect to an access point:
		 */
		
		while ((hostId == -1)&&(i<numOfAP)) {
				ConnectedEdgeAgent = AP[i];	
				if (ConnectedEdgeAgent != -1) {
					try {
						System.out.println("VehAgent "+v.getName()+" trying to connect to edgeAgent: "+ConnectedEdgeAgent);
						edgeServerAdd = "tcp://127.0.0.1:"+(5700+ConnectedEdgeAgent);
						hostId = client.sendRequest(edgeServerAdd, ConnectedEdgeAgent, Constants.CONNREQ);
					    //client.stop();
					   //hostId = ConnectToEdgeAgent(ConnectedEdgeAgent, v);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			i++;
			if (i == numOfAP)//infinite loop until connect to an AP and receive a service host
				i = 0;
		}
		 
			      
						
		if(hostId != -1) {//get a host for its service
			v.setConnectedEdge(edgeAgents.get(ConnectedEdgeAgent));//set edge node
			v.setHostServerCloudlet(edgeAgents.get(hostId));//set host node
			v.toString();
			System.out.println("VehicleAgent "+v.getName()+" connected to "+ConnectedEdgeAgent+" , its service is hosted on "+v.getHostNode().id);
		}
		
		

		//sleep until the container is up:
		Thread.sleep(5000);
		String traficDataPublisger = "tcp://localhost:"+v.getMyId();
		Callable<Void> target = new asyncSubscriber(context, traficDataPublisger, hostId); 
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
              try {
                  target.call();
              }
              catch(Exception ex) {
                  throw new RuntimeException(ex);
              }
          }, 2, 2, TimeUnit.SECONDS);
        
        
		
		
		//new Subscriber(traficDataPublisger, v.getHostServerCloudlet().id).run(null);
		//new Subscriber(traficDataPublisger1, 1).run(null);
		System.out.println("VehicleAgent connected to its service and receiving updates");
		
		 // System.exit(0);
		/*
		 * update agent location/edge node and chech if its still in the coverage range of ap
		 */
		while(true) {
			TimeUnit.SECONDS.sleep(1);
			Coordinate.newCoordinate(v);
			/*
			 * the agent moves out of borders or its path list if ended
			 */
			if (!v.isStatus()) {
				
				break;//coordinate.setInitialCoordinate(u);
				
			}
			double dis = Distance.calDistance(v.getCoord(),v.getConnectedEdge().getCoord());
			//in coverage range:
			if (dis < (Constants.AP_COVERAGE-Constants.Handoff)) {
				System.out.println("VehicleAgent "+v.getMyId()+" with distance "+dis+" still connected to EdgeAgent "+v.getConnectedEdge().id);
			}
			//out of coverage range of its connected access point:
			else {//hand off from its connected access point and try with next closest access point
				//host = null;
				System.out.println("VehAgent "+v.getMyId()+" with distance "+dis+ " disconnecting from EdgeAgent "+v.getConnectedEdge().id+" ...");
				AP = new int[] {-1,-1,-1};
				numOfAP = Distance.closestEdgeAgents(edgeAgents, v, AP);
				System.out.println("Next closest EdgeAgents ids: "+ AP[0]+", "+ AP[1]+", "+AP[2]);
				
				int connectedEdgeId = -1;
				i = 0;
				/**
				 * keep sending connection requests to the APs until connect to an access point:
				 */
				while ((connectedEdgeId == -1)&&(i<numOfAP)) {
						ConnectedEdgeAgent = AP[i];	
						if (ConnectedEdgeAgent != -1) {
							try {
								System.out.println("VehicleAgent "+v.getName()+" trying to connect to EdgeAgent: "+ConnectedEdgeAgent);
								
								edgeServerAdd = "tcp://localhost:"+(5700+v.getConnectedEdge().id);
								newEdgeServerAdd = "tcp://localhost:"+(5700+ConnectedEdgeAgent);
								connectedEdgeId = client.handover(edgeServerAdd, newEdgeServerAdd, ConnectedEdgeAgent, v.getConnectedEdge().id, Constants.RECOREQ);
							    
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					i++;
					if (i == numOfAP)//infinite loop until connect to an AP and receive a host
						i = 0;
				}
				if (connectedEdgeId != -1)
					System.out.println("Handover done successfully");
				
				v.setConnectedEdge(edgeAgents.get(connectedEdgeId));
				v.toString();
				//System.out.println("VehicleAgent "+v.getName()+" connecting to traffic monitoring service hosted on "+hostId);
				
			}	
			
		}//end while
		
		
		//end of mobility profile: going down and shutting down
		edgeServerAdd = "tcp://localhost:"+(5700+v.getConnectedEdge().id);
		client.down(edgeServerAdd, v.getConnectedEdge().id, Constants.DOWNREQ);
		
		System.out.println("Agent "+v.getMyId()+" going out of the city.");
		 client.stop();
	}


	/**
	 * read json file infrastructure with the characteristics of network: area, #edges, edge locations/coverage
	 */
	private static void read_infrastructure(String infraFile, int index) {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		
	    try {
	        Object obj = parser.parse(new FileReader(infraFile));
			
	        JSONArray jsonObjects =  (JSONArray) obj;

	        for (Object o : jsonObjects) {
	            JSONObject jsonObject = (JSONObject) o;
	            
	            Constants.MAX_X = (int) (long) jsonObject.get("max_x");
	    		Constants.MIN_X = (int) (long) jsonObject.get("min_x");
	    		Constants.MIN_Y = (int) (long) jsonObject.get("min_y");
	    		Constants.MAX_Y = (int) (long) jsonObject.get("max_y");
	    		Constants.AP_COVERAGE = (int) (long) jsonObject.get("AP_COVERAGE");
	            Constants.numEdgeNodes = (int) (long) jsonObject.get("edge_nodes");
	            //Constants.brokerUrl = (String) (jsonObject.get("mosquittoBrokerUrl"));
	            
	            JSONArray jsonArraycpu= (JSONArray) jsonObject.get("CPUResourceDemand");
	            Constants.cpu = (int) (long)(jsonArraycpu.get(index));
	            		
	            JSONArray jsonArraymem= (JSONArray) jsonObject.get("MemResourceDemand");
	            Constants.mem = (int) (long)(jsonArraymem.get(index));
	            
	            JSONArray jsonArrayStor= (JSONArray) jsonObject.get("StoResourceDemand");
	            Constants.storage = (int) (long)(jsonArrayStor.get(index));
	            
	            JSONArray jsonArrayX= (JSONArray) jsonObject.get("Xpoints");
	            JSONArray jsonArrayY= (JSONArray) jsonObject.get("Ypoints");
	            
	            for(int i=0; i<jsonArrayY.size(); i++){
	            	edgeAgents.add(i, new EdgeNode(i, (int) (long)(jsonArrayX.get(i)),(int) (long)(jsonArrayY.get(i))));
	            }
	        }  
		      System.out.println("Number of configured EdgeAgents: "+edgeAgents.size());
                
	        
	    
	}catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ParseException e) {
        e.printStackTrace();
    }
		
	}
	
		/**
	 * @param u agent
	 * read the mobility of agent compatible with the SUMO mobility files 
	 */
	public static void read_mobility(Vehicle u, String mobDir){	
		System.out.println("Reading mobility dataset...");
		String line = "";
		String csvSplitBy = ",";
		String pathToMobility = mobDir+u.getMyId()+".csv";
			try (BufferedReader br = new BufferedReader(new FileReader(pathToMobility))) {

				while ((line = br.readLine()) != null) {
					String[] position = line.split(csvSplitBy);
					u.getPath().add(position);
				}

				Coordinate coordinate = new Coordinate();
				coordinate.setInitialCoordinate(u);
				//saveMobility(u);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	/*
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleAtFixedRate(() -> {
          try {
              target.call();
              
          }
          catch(Exception ex) {
              throw new RuntimeException(ex);
          }
      }, 1, 1, TimeUnit.SECONDS);
    */
   // System.out.println("Wating for connection acceptance...");


}
