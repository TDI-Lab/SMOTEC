package pi;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * @author zeinab
 * An abstraction for the edge nodes in SMOTEC network which manages the communication and computation tasks of an edge node
 */
public class EdgeAgent {
	
	static int myId;
	static String edgeAgent;
	static int timeline = 600; //lifetime of this edgeAgent; user can change it to  the value she wants to run this EdgeAgent
	static int run = 0;
	
	static Context context = ZMQ.context(1);
	
	private static HashSet<Integer> hostedList = new HashSet<>();//list of hosted services on this EdgeNode
	private static Map<Integer, Integer> SrvToHostSet = new HashMap<>();//keeps the mapping between VehicleAgents services and network EdgeAgents/hosts 
	private static List<Neighbor> neighbors = new ArrayList<>();//list of EdgeAgents in the network
	
	
	private static List<Request> conrequests = new ArrayList<>();//current received connection requests 
	private static HashSet<String> connectedSet = new HashSet<String>(); //connected VehicleAgents to this EdgeAgent 
	private static List<Request>  recorequests = new ArrayList<>();//current received handover (connection) requests
	private static List<Request>  disrequests = new ArrayList<>();//current received disconnect requests
    private static List<Request>  downrequests = new ArrayList<>();//current received down requests
	
	
	/**
	 * @param args id of edgeagent which is the input received from corresponding deployment file (for K3s)
	 * @throws Exception
	 * This program creates an agent, edgeAgent, for an edge node. The agent listens to the requests from mobile agents and then assigns those requests to the
	 * edge servers in the network. Meanwhile it generates deployment files for the requests. Those files are then deployed via K3s by Testbed front-end program.
	 * Apart from the service requests, this agent handles disconnection and handover requests of mobile agents and migration of services.
	 * The service placement is done using communication and exchanging messages by service distributor module. 
	 */
	public static void main(String[] args) throws Exception {
		
		edgeAgent = args[0];
		Constants.edgeAgentIndex = Integer.parseInt(args[0]);// index in the list of neighbours
		Constants.numMsg = 0;
		int size;
		Constants.initialize();
		
		Utility.read_neighbors(Constants.conf, neighbors);
		System.out.print("EdgeAgent "+edgeAgent+" starts running at time "+Constants.maxTime);
		
		Constants.SRVDISPRO = "tcp://"+Constants.srvdisSrvProducer+":"+Constants.PPORT;//producer
		Constants.SRVDISCON = "tcp://"+Constants.srvdisSrvListener+":"+Constants.CPORT;//consumer
		Constants.reqListener = "tcp://*:"+(Constants.srvDisListenerTopUp+Constants.edgeAgentIndex);//0MQ server listening address to the received requests on this edgeAgent
		Constants.traficDataPublisger = "tcp://*:"+(Constants.updatePublishTopUp+Constants.edgeAgentIndex);//0MQ traffic data publisher running on this EdgeAgent
		Constants.connectedEdgeAddr = "tcp://*:"+(Constants.hostDistributorTopUp+Constants.edgeAgentIndex);//selected hosts distributor
		
		//System.out.println("Input Args: "+Constants.srvdisSrvListener+" "+Constants.EPOS_NUM_PLANS+" "+Constants.srvdisSrvProducer);
		
		 
		HostDistributor hostDist = new HostDistributor(context);
		hostDist.open(Constants.connectedEdgeAddr);
		
		PlanDistributor planDist = new PlanDistributor(context);
		planDist.open(Constants.SRVDISPRO);
		
		Consumer cons = new Consumer(context);
	    cons.open();
		
		Server server = new Server(context, Constants.reqListener, edgeAgent);//receives requests
		
		ResourceMgm rm = new ResourceMgm(context, edgeAgent, neighbors, hostDist, planDist, cons);//makes deploy/release decision for the requests
		
		//publish number of current connected VehicleAgents and hosted services on this EdgeAgent to the running (collector) services on this edge node
		Callable<Void> target = new TrafficMonitorPublisher(context, edgeAgent, connectedSet, hostedList); 
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
              try {
                  target.call();
              }
              catch(Exception ex) {
                  throw new RuntimeException(ex);
              }
          }, 12, 4, TimeUnit.SECONDS);
        
        
	    
        while(true) {
	        
        	if (Constants.maxTime >= timeline) {
	        		System.out.println("Time horizon reached, edgeAgent going down");
	        		//Utility.switchOff(edgeAgent);
	        		break;
	        	}
	        	
        	size = 0;
			while (size == 0) {//keep listening to the requests from mobile agent
					size = server.startAndWaitForRequests(conrequests, connectedSet, recorequests, disrequests, downrequests);
			        System.out.println("EdgeAgent "+edgeAgent+" received "+size+" requests; conn: "+ conrequests.size()+", reco: "+recorequests.size()+", disc: "+disrequests.size()+", down: "+downrequests.size());
			        
		        }
			
			rm.manageRequests(conrequests, connectedSet, hostedList, SrvToHostSet, recorequests, disrequests, downrequests);
			
			UpdateRequests();
				
            System.out.println("EdgeAgent: "+edgeAgent+", time: "+Constants.maxTime+", number of connected vehicles: "+connectedSet.size()+", number of hosted services: "+hostedList.size());
			System.out.println("Num of control Msg exchanged: "+Constants.numMsg);
            
        }
        System.out.println("End of time line reached, EdgeAgent "+edgeAgent+" is powering off!");
        executor.shutdown();
        server.stop();
	       
	}
	    

		/**
	 * remove already hosted requests be redefining requests list
	 */
	private static void UpdateRequests() {
		// TODO Auto-generated method stub
		conrequests = new ArrayList<>();
		downrequests = new ArrayList<>();
		disrequests = new ArrayList<>();
		recorequests = new ArrayList<>();
		
		
	}
	
	
	
}
	
	
	