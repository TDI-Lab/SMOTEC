package pie;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import experiment.IEPOSExperiment;



//https://github.com/SolaceSamples/solace-samples-mqtt
//https://www.baeldung.com/java-mqtt-client

public class Edgenode {
	static boolean win = true;
	static String pieName;
	private static List<SRequest> simulationRequests = new ArrayList<>();//all received connection requests during the simulation 
	private static List<Request> requests = new ArrayList<>();//received connection requests 
	private static List<Neighbor> neighbors = new ArrayList<>();
	private static Map<Integer, Integer> SrvToHostSet = new HashMap<>();
	
	
	private static final String brokerUrl ="tcp://localhost:1883";
	    
	/**
	 * @param args id of edge node which is input of corresponding container deployment command of k3s
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Constants.win = win;
		Constants.initialize(win);
		
		pieName = args[0];//edge id == index in list of neighbours
		System.out.println("Edge node "+pieName+" is starting");
	    
		/*
		 * mqttclient creation
		 */
		String publisherId = UUID.randomUUID().toString();
        MqttClient edgeNode = new MqttClient(brokerUrl, publisherId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(0);//no timeout
        edgeNode.connect(options); 
        
        //update resources based on hosts
		read_neighbors(args[1]);
        /*
         * infinite loop waiting for connection requests
         */
        while(true) {
		
			while (requests.size() == 0) {
		        int numReq = waitAndSaveRequests(edgeNode).size();
		        System.out.println("Edge node "+pieName+" received "+ requests.size()+" requests");
	        }
			SrvMapper sMap = new SrvMapper(requests, neighbors, Integer.parseInt(pieName));
			sMap.generatePlans();
			sMap.SelectAndDeploy(args, edgeNode, SrvToHostSet); 
			
			UpdateRequests();
		
        }
        //System.out.println("finished !");
	    
	}
	    

	/**
	 * remove already hosted requests be redefining requests list
	 */
	private static void UpdateRequests() {
		// TODO Auto-generated method stub
		requests = new ArrayList<>();
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
	            int numNeighbors = (int) (long) jsonObject.get("num");
	            
	            
	            JSONArray jsonArrayid= (JSONArray) jsonObject.get("id");
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
		      System.out.println("Number of neighbors: "+neighbors.size());
                
	        
	    
	}catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ParseException e) {
        e.printStackTrace();
    }
		
	}
	
	
	/**
	 * @param EdgeNode waits for requests from agents
	 * @return a hashset containing received requests
	 * @throws Exception
	 */
	public static HashSet<String> waitAndSaveRequests(MqttClient EdgeNode) throws Exception {
		   System.out.println("Waiting for connection requests...");
		   int iTime;
		   String ReqTOPIC = "request-"+pieName;//request topic
	       //System.out.println("my name is : "+pie.name);
		   HashSet<String> requesSet=new HashSet<String>(); //saves received connection requests id
	       CountDownLatch receivedSignal = new CountDownLatch(1);
		    
	        //subscribe to request topic:
	        EdgeNode.subscribe(ReqTOPIC, (topic, msg) -> {
	        	String reqId;
		    	byte[] payload = msg.getPayload();
	            StringTokenizer st1 = new StringTokenizer(msg.toString(),":");
	            //System.out.println("Message received: topic="+ topic+ " payload=" +new String(payload)+", signal "+receivedSignal.getCount());
	            //st1.nextToken();//System.out.println("Message received; first token= "+);
	            reqId = st1.nextToken();
	            String C = st1.nextToken(), M = st1.nextToken(), S = st1.nextToken(), T = st1.nextToken();
	            if ((!requesSet.contains(reqId))&&(C != "")){ //check if the request is already recorded or if its payload if empty     
		            iTime = Integer.parseInt(T);
	            	requests.add(new Request(reqId, C, M, S, iTime));
		            //System.out.println("size "+requests.size());
		            requesSet.add(reqId);
		            String str = "";
			    	byte[] typmsg = str.getBytes();        
				    MqttMessage m1 = new MqttMessage(typmsg); 
				    //publish ack to the sender agent:
		            EdgeNode.publish(pieName+":"+reqId, m1);
		            
		            if (simulationRequests.get(iTime) == null) {
		            	new SRequest(reqId, C, M, S, iTime)
		            	simulationRequests.add(iTime,new Request(reqId, C, M, S, iTime));
		            }
		            else {
		            	
		            }
		            	
	            }
		         
	            receivedSignal.countDown();
	                       
	        });
	        
	        receivedSignal.await(40, TimeUnit.SECONDS);//wait for 40sec for requests
	        
	   return requesSet; 
	   }
	    

	/*
    CountDownLatch ackSentSignal = new CountDownLatch(requesSet.size());
	Callable<Void> target = new Pub(EdgeNode, pie.name);
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleAtFixedRate(() -> {
          try {
              target.call();
          }
          catch(Exception ex) {
              throw new RuntimeException(ex);
          }
      }, 1, 1, TimeUnit.SECONDS);
    
    ackSentSignal.await(20, TimeUnit.SECONDS);
    
    //executor.shutdown();
    
    */
    
    
   // assertTrue(receivedSignal.getCount() == 0 , "Countdown should be zero");

    //.info("[I105] Success !");
	}