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



public class Agent {
	private static List<EdgeNode> edgeDevices = new ArrayList<>();
	private static String host;
	private static final String brokerUrl ="tcp://localhost:1883";
	static MqttClient mqttClient;
	static boolean win = true;
	
	/**
	 * @param args agent_id, CPU, memory, storage: 10000 250 300 400
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("My agent id is: "+args[0]);
		Constants.win = win;
		Constants.initialize(win,Integer.parseInt(args[0]));
		
		read_infrastructure();
		
		Vehicle v = new Vehicle(args[0],args[1],args[2]);
		
		read_mobility(v, args[3]);
		
		//mqttClient creation:
		MemoryPersistence persistence = new MemoryPersistence();
		String subscriberId = UUID.randomUUID().toString();
		try {
			mqttClient = new MqttClient(brokerUrl,subscriberId, persistence);
	        MqttConnectOptions options = new MqttConnectOptions();
	        options.setAutomaticReconnect(true);
	        options.setCleanSession(true);
	        options.setConnectionTimeout(0);//disable timeout
	        mqttClient.connect(options);
		}
		catch (MqttException me) {
			System.out.println(me);
		}

		int ConnectedEdge = -1;// = Distance.theClosestAp(edgeDevices, u);
		/** 
		 * array to keep the three closest access points to this agent with its initial coordination
		 */
		int[] AP = new int[] {-1,-1,-1};
		int numOfAP = Distance.nextClosestAp(edgeDevices, v, AP);
		System.out.println("Closest ap ids: "+ AP[0]+", "+ AP[1]+", "+AP[2]);
		int hostId = -1, i = 0;
		System.out.println("num "+numOfAP);
		/**
		 * keep sending connection requests to the AP until connect to an access point:
		 */
		while ((hostId == -1)&&(i<numOfAP)) {
				ConnectedEdge = AP[i];	
				if (ConnectedEdge != -1) {
					try {
						System.out.println("Agent "+v.getName()+" trying to connect to ap: "+ConnectedEdge);
						hostId = ConnectToAP(ConnectedEdge, v);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			i++;
			if (i == numOfAP)//infinite loop until connect to an AP and receive a service host
				i = 0;
		}
		
		if(hostId != -1) {
			v.setConnectedEdge(edgeDevices.get(ConnectedEdge));//set edge node
			v.setHostServerCloudlet(edgeDevices.get(hostId));//set host node
			v.toString();
			//start communication with hosted service
			System.out.println("User "+v.getName()+" service is hosted on "+hostId);
		}
		
		/*
		 * update agent location/edge node and migrate its service accordingly
		 */
		while(true) {
			Coordinate.newCoordinate(v);
			/*
			 * the agent moves out of borders or its path list if ended
			 */
			if (!v.isStatus())
				Coordinate.setInitialCoordinate(v);
			//break;//coordinate.setInitialCoordinate(u);
			double dis = Distance.checkDistance(v.getCoord(),v.getConnectedEdge().getCoord());
			if (dis < (Constants.AP_COVERAGE-Constants.Handoff)) {
				System.out.println("Agent "+v.getMyId()+" with distance "+dis+" still connected to ap."+v.getConnectedEdge().id);
				//continue communication with service
			}
			else {//hand off from its connected access point
				host = null;
				System.out.println("Agent "+v.getMyId()+" with distance "+dis+ " disconnecting from ap."+v.getConnectedEdge().id+" ...");
				AP = new int[] {-1,-1,-1};
				numOfAP = Distance.nextClosestAp(edgeDevices, v, AP);
				System.out.println("Next closest ap ids: "+ AP[0]+", "+ AP[1]+", "+AP[2]);
				hostId = -1;
				i = 0;
				/**
				 * keep sending connection requests to the AP until connect to an access point:
				 */
				while ((hostId == -1)&&(i<numOfAP)) {
						ConnectedEdge = AP[i];	
						if (ConnectedEdge != -1) {
							try {
								System.out.println("Agent "+v.getName()+" trying to connect to ap: "+ConnectedEdge);
								hostId = ConnectToAP(ConnectedEdge, v);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					i++;
					if (i == numOfAP)//infinite loop until connect to an AP and receive a host
						i = 0;
				}
				
				v.setConnectedEdge(edgeDevices.get(ConnectedEdge));
				v.setHostServerCloudlet(edgeDevices.get(hostId));
				v.toString();
				//start communication with service
				System.out.println("User "+v.getName()+" service is hosted on "+hostId);
				
			}	
			
		}//end while
		
		
		//System.out.println("Agent "+v.getMyId()+" service finished.");
		
	}

	/**
	 * @param ap candidate ap to connect to: the access point in the coverage range
	 * @param us vehicle
	 * @return	selected host index for the vehicle service
	 * the method sends connection request to ap, waits for ack and a host id from ap
	 * @throws Exception
	 */
	private static int ConnectToAP(int ap, Vehicle us) throws Exception {
		System.out.println("------------------------------------------------------------");
		/*
		 * subscribe for message with the topic ap:agent as the ack
		 */
		String ReqToAck = ap+":"+us.getName();
		/*
		 * send connection request with the topic: response-ap
		 */
		String ReqAck = "response-"+ap;
		     
		/*
		 * wait on ack message reception
		 */
        CountDownLatch receivedSignal = new CountDownLatch(1);
        System.out.println("First latch value: "+receivedSignal.getCount()+", waiting for ack: "+ReqToAck);
        mqttClient.subscribe(ReqToAck, (topic, msg) -> {
	            System.out.println("Connection request ack received: topic="+ ReqToAck+ " payload=" +new String(msg.getPayload())+", signal "+receivedSignal.getCount());
                receivedSignal.countDown();
	        });
        
        /*
         * publish connection request messages
         */
        Callable<Void> target = new MqttPublisher(mqttClient, ap, us);
        ExecutorService executor = Executors.newSingleThreadExecutor();
	    try {
	          target.call();
	    }
	    catch(Exception ex) {
	          throw new RuntimeException(ex);
	    }
	  
	        
	    receivedSignal.await(30,TimeUnit.SECONDS);//await();//
	    executor.shutdown();
	     /*
	      * if connected with the ap then wait for the selected host from the ap   
	      */
        if (receivedSignal.getCount() == 0) {
        	System.out.println("Agent "+us.getName()+" received an ack and waiting for host allocation");
        	/*
        	 * Topic of host selection message: response-ap:agent 
        	 * payload pf message contains host:hostId
        	 */
        	String ResTOPIC = "response-"+ap+":"+us.getMyId();
			CountDownLatch receivedSignal1 = new CountDownLatch(1);
	        mqttClient.subscribe(ResTOPIC, (topic, msg) -> {
	            StringTokenizer st1 = new StringTokenizer(msg.toString(),":");
	            System.out.println("Message received: topic= "+ ResTOPIC+ " payload= " +new String(msg.getPayload())+" signal "+receivedSignal.getCount());
	            st1.nextToken();//first token "host"
	            host = st1.nextToken();
				System.out.println("second token/host: "+host);
				receivedSignal1.countDown();
		        });
	        
		    receivedSignal1.await(100, TimeUnit.SECONDS);
		        
	        }
	        
        if (host != null) {
		        System.out.println("here I am, received the host: "+host);
	        	return Integer.parseInt(host);
	           
        }
        else { 
	        	return -1;
        }
	}

	/**
	 * read json file infrastructure with the characteristics of network: area, #edges, edge locations/coverage
	 */
	private static void read_infrastructure() {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		
	    try {
	        Object obj = parser.parse(new FileReader(Constants.infraFile));
			
	        JSONArray jsonObjects =  (JSONArray) obj;

	        for (Object o : jsonObjects) {
	            JSONObject jsonObject = (JSONObject) o;
	            
	            Constants.MAX_X = (int) (long) jsonObject.get("max_x");
	    		Constants.MIN_X = (int) (long) jsonObject.get("min_x");
	    		Constants.MIN_Y = (int) (long) jsonObject.get("min_y");
	    		Constants.MAX_Y = (int) (long) jsonObject.get("max_y");
	    		Constants.AP_COVERAGE = (int) (long) jsonObject.get("AP_COVERAGE");
	            
	            Constants.numEdgeNodes = (int) (long) jsonObject.get("edge_nodes");
            
                JSONArray jsonArrayX= (JSONArray) jsonObject.get("Xpoints");
	            JSONArray jsonArrayY= (JSONArray) jsonObject.get("Ypoints");
	            
	            for(int i=0; i<jsonArrayY.size(); i++){
	            	edgeDevices.add(i, new EdgeNode(i, (int) (long)(jsonArrayX.get(i)),(int) (long)(jsonArrayY.get(i))));
	            }
	        }  
		      System.out.println("Number of configured EdgeNodes: "+edgeDevices.size());
                
	        
	    
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
