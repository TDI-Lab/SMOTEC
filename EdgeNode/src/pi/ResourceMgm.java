package pi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

/**
 * @author zeinab
 * This class manages the received requests of connection, disconnection/handover, down types.
 * It also generated deployment files and one release file for testbed program which later updates deployments via K3s
 *
 */
public class ResourceMgm {
	List<Request> connrequests ;
	List<Neighbor> neighbors ;
	HashSet<String> connectedset;
	HashSet<Integer> hosted;
	public Plan [] Plans;
	Map<Integer, Integer> SrvToHostSet;
	int edgeAgentIndex;
	int planIndex = -1;
	Context context ;
	HostDistributor hostDist;
	PlanDistributor planDist;
	Consumer planCons;
	
	public ResourceMgm(Context context2, String agent, List<Neighbor> neighbors2, HostDistributor hostDist2, PlanDistributor planDist2, Consumer cons2) {
		edgeAgentIndex = Integer.parseInt(agent);
		context = context2;
		neighbors = neighbors2;
		hostDist = hostDist2;
		planDist = planDist2;
		planCons = cons2;
	}
			
	
	public void manageRequests(List<Request> requests2, HashSet<String> connectedSet, HashSet<Integer> hostedList, Map<Integer, Integer> srvToHostSet, List<Request> recorequests, List<Request> disrequests, List<Request> downrequests)  {
		connrequests = requests2;
		connectedset = connectedSet;
		hosted = hostedList;
		SrvToHostSet = srvToHostSet;
		List<Request> conrequests1 = new ArrayList<>();
		System.out.println("EdgeAgent "+edgeAgentIndex+" starts processing requests...");
		
		if(connrequests.size() + recorequests.size() + disrequests.size() + downrequests.size() == 0) {
			System.out.println("No request to be managed!");
			return;
		}
		chkLazyClients(conrequests1);
		
		ProDownReq(downrequests);
		ProDisReq(disrequests);
		
		if(conrequests1.size() == 0) {
			System.out.println("No request for placement!");
			return;
		}
		
		//process connection (service placement) requests:
		String msg = makeMsg(ProConReq(conrequests1));
		planExchang(msg);
		
		allocateResources(conrequests1);
		System.out.println("Management of requests finished!");
		
	}
	
	

	/**
	 * @param conrequests1
	 * if there is a request for a service/connection which is already received/processed just answer by the previously allocated host for that request
	 */
	private void chkLazyClients(List<Request> conrequests1) {
		
		for(int i = 0; i <connrequests.size(); i++) {
				if (SrvToHostSet.get(connrequests.get(i).vehAgentId) != null){
					String selectedHostMSg = "host:"+connrequests.get(i).vehAgentId+":"+SrvToHostSet.get(connrequests.get(i).vehAgentId);
					System.out.println("lazy client message..."+selectedHostMSg);
					hostDist.send_message(selectedHostMSg);//do not process repetitive requests again with EPOS
					connrequests.get(i).hostId = 0;
				}
			
			}
			
			for(int i = 0; i <connrequests.size(); i++) {
				if (connrequests.get(i).hostId != 0){
					conrequests1.add(new Request(connrequests.get(i).vehAgentId, connrequests.get(i).getCPU(), connrequests.get(i).getMemory(), connrequests.get(i).getStorage(), Constants.CONREQ ,connrequests.get(i).travelTime, -1));
				}
			}
		
	}


	/**
	 * @param disrequests
	 * process disconnection requests by simply removing that mobile agent from its connected agents list
	 */
	private void ProDisReq(List<Request> disrequests) {

		for (int i = 0;i<disrequests.size(); i++) {
			
			if (disrequests.get(i).type == Constants.DISREQ)
				connectedset.remove(disrequests.get(i).getReqIdStr());
		}
	}


	/**
	 * @param conrequests1
	 * @return
	 * process requests for connection/service
	 */
	private Plan[] ProConReq(List<Request> conrequests1) {
		SrvMapper srvDisrtibutor = new SrvMapper(edgeAgentIndex, conrequests1, neighbors);
		Plans = srvDisrtibutor.generatePlans();//generate service placement plans for the received connection requests
		return Plans;
		
	}

	/**
	 * @param candPlanMsg
	 * @return
	 * send generated service placement plans to service distributor and wait for the response from service distributor which is a selected plan
	 */
	private int planExchang(String candPlanMsg) {
		
		System.out.println("Sending plans to the Service distributor");
		planDist.send_message(candPlanMsg);
		
		/*
	      * if connected with service distributor then wait for receiving selected host from it  
	      */
        System.out.println("Waiting for response from service distributor");
	    planIndex = planCons.waitAndReceive();
		
	    if (planIndex != -1) {
        	System.out.println("Selected placement plan index: "+planIndex);
				return planIndex;	//index of selected plan among generated plans
			}
		else {
			System.out.println("Service distributor not responding, DEBUG!");
				
			}
			return -1;
	}

	/**
	 * @param myplans
	 * @return
	 * make a message of service placement plans according to a specified format to send to service distributor (EPOS)
	 */
	private String makeMsg(Plan[] myplans) {
		String plans = "EPOS!"+edgeAgentIndex+"!"+Constants.maxTime+"!";
		//utilization plans:
        for(int col =0; col < Constants.EPOS_NUM_PLANS; col++){
            	plans += String.valueOf(myplans[col].getCosts());
                plans += ":";
                int size = myplans[col].utilPlan.length;
                for (int i =0; i<size ; i++){//both of CPU and Mem will be printed
                	plans += String.format("%.12f", myplans[col].utilPlan[i]);//preventing wrong written of values as negative values
                     if (i != size-1){
                    	 plans += ",";
                     }
                 }
                plans += "\r\n";
            } 
        
        plans += "!";
        //associated binary plans:
        for(int col = 0; col < Constants.EPOS_NUM_PLANS; col++){
            
	        int size = myplans[col].y.length;
			for (int i = 0; i<size ; i++){
				plans += String.format(String.valueOf(myplans[col].y[i]));
	             System.out.println("i "+i+" "+myplans[col].y[i]);
	     	 	
	             if (i != myplans[col].y.length-1){
	            	 plans += ",";
	             }
	 	}
 
			plans += "\r\n";
        }
        
        
    return plans;
	}

	
	
	/**
	 * @param conrequests1
	 * deploy/release service/resource based on the selected plans for the received service requests
	 * Update service to hosts map and then publish messages to the agents from which it received service requests: message contains the name/address of the selected hosts for their services
	 */
	private void allocateResources(List<Request> conrequests1) {
		String selectedHostMSg; 
			
		for (int i = 0; i <Plans[planIndex].y.length; i++) {
			
			Migrate(i, conrequests1);			
			
			conrequests1.get(i).hostId = Plans[planIndex].y[i];//assign/records selected host for the receive request
	        
			if (Plans[planIndex].y[i] == edgeAgentIndex)
	        	hosted.add(conrequests1.get(i).vehAgentId);
			
			SrvToHostSet.put(conrequests1.get(i).vehAgentId, conrequests1.get(i).hostId);
			
			//response message to mobile agents with their hosts: host:agentid:hostid
			selectedHostMSg = "host:"+conrequests1.get(i).vehAgentId+":"+Plans[planIndex].y[i];
			hostDist.send_message(selectedHostMSg);
		}
		System.out.println("service migration done");
		
	}
	
	/**
	 * @param i
	 * @param conrequests1
	 * deploy new services and release resources
	 */
	private void Migrate(int i, List<Request> conrequests1) {
		System.out.println("Releasing resources");
		releaseResources(i, conrequests1);
		System.out.println("Deploying new services\n");
		deployPlan(i, conrequests1);
		
	}
	
	/**
	 * 
	 * @param i2 
	 * @param conrequests1 
	 * create deployment .yaml file for every service request placement
	 */
	private void deployPlan(int i2, List<Request> conrequests1) {
		
		int selectedHost;
		java.util.Map<String, String> env = new HashMap<String, String>();
		if((SrvToHostSet.get(conrequests1.get(i2).vehAgentId) == null) ||(SrvToHostSet.get(conrequests1.get(i2).vehAgentId) != Plans[planIndex].y[i2])) {
			
			selectedHost = Plans[planIndex].y[i2];
			Utility.creatYaml(i2, conrequests1.get(i2).vehAgentId, conrequests1.get(i2), selectedHost, neighbors.get(selectedHost).nodelabel);//write deployments for agent's service with resource demands of the request:
			Utility.updateDepRel(""+conrequests1.get(i2).vehAgentId, "deploy", 1, selectedHost);//identify deployment/release for system management (testbed program)

			//update available resources in the network:
			neighbors.get(selectedHost).CPU -= conrequests1.get(i2).getCPU();
			neighbors.get(selectedHost).Memory -= conrequests1.get(i2).getMemory(); 
			neighbors.get(selectedHost).Storage -= conrequests1.get(i2).getStorage(); 
			neighbors.get(selectedHost).IncRunningSrv();

			
		}
		
	}
	
	/**
	 * @param i2
	 * @param conrequests1
	 * update release file for testbed program.
	 */
	private void releaseResources(int i2, List<Request> conrequests1) {
			if(SrvToHostSet.get(conrequests1.get(i2).vehAgentId) != null) {
				
				if(SrvToHostSet.get(conrequests1.get(i2).vehAgentId) != Plans[planIndex].y[i2]) {//check if its value is null
					//update network resources:
					neighbors.get(SrvToHostSet.get(conrequests1.get(i2).vehAgentId)).CPU += conrequests1.get(i2).getCPU();
					neighbors.get(SrvToHostSet.get(conrequests1.get(i2).vehAgentId)).Memory += conrequests1.get(i2).getMemory();
					neighbors.get(SrvToHostSet.get(conrequests1.get(i2).vehAgentId)).Storage += conrequests1.get(i2).getStorage();
					neighbors.get(Plans[planIndex].y[i2]).DecRunningSrv();
				
				}
				else {
					
				}
				
		}
	}

	/**
	 * @param downrequests
	 * process down requests from connected mobile agents by releasing their allocated resources and their records 
	 */
	private void ProDownReq(List<Request> downrequests) {
		
		for (int i = 0;i<downrequests.size(); i++) {
			
				if ((downrequests.get(i).type == Constants.DOWN)&&(downrequests.get(i).getHostId() != -1)){
					
					if(SrvToHostSet.get(downrequests.get(i).hostId) != null) {
							neighbors.get(downrequests.get(i).hostId).CPU += downrequests.get(i).getCPU();
							neighbors.get(downrequests.get(i).hostId).Memory += downrequests.get(i).getMemory(); 
							neighbors.get(downrequests.get(i).hostId).Storage += downrequests.get(i).getStorage(); 
							SrvToHostSet.remove(downrequests.get(i).vehAgentId);
						}
						if (downrequests.get(i).hostId == edgeAgentIndex)
				        	hosted.remove(downrequests.get(i).vehAgentId);
							
						Utility.updateDepRel(""+downrequests.get(i).vehAgentId, "release", 0, downrequests.get(i).hostId);
						
					
				}
				connectedset.remove(downrequests.get(i).getReqIdStr());
					
		}

	}
	}
	