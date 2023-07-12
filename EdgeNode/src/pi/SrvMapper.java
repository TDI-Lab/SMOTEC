package pi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author zeinab
 * generates service placement plans for received connection requests
 *
 */
public class SrvMapper {
	
	int edgeAgentIndex;
	
	List<Request> requests = new ArrayList<>();
	List<Neighbor> neighbors = new ArrayList<>();
	public Plan [] CandidatePlans;
	
	
	public SrvMapper(int agent, List<Request> requests2, List<Neighbor> neighbors2) {
		requests = requests2;
		neighbors = neighbors2;
		edgeAgentIndex = agent;
		System.out.println("EdgeAgent "+edgeAgentIndex+" starts plan generation...");
		
		
	}
	/* 
	 * generates placement plans for received requests
	 * @return an array of placement plans named CandidatePlans
	 */
	public Plan[] generatePlans(){
		
		CandidatePlans = new Plan [Constants.EPOS_NUM_PLANS];
		
		 if (requests.size() == 0) {
			 System.out.println("Requests for conection/placement are zero");	
	         }
	     else {
	         
	    	 int numReqToPlacement = agentsWithReq(requests); 
        	 for(short planIndex=0 ; planIndex < Constants.EPOS_NUM_PLANS ; planIndex++){  
	        	 CandidatePlans[planIndex] = makePlan(planIndex, selectHosts(numReqToPlacement));
	         } 
        	 
        	 Utility.writePlans(CandidatePlans);
	        }
	         
	    System.out.println("End of plan generation...........");
	    return CandidatePlans;
	         
	}
	
	/**
	 * @param requests2
	 * @return
	 * counts number of requests for service placement
	 */
	private int agentsWithReq(List<Request> requests2) {
		int req = 0;
		for (int i = 0;i<requests.size(); i++) 
			if (requests.get(i).type == Constants.CONREQ)
				req++;
				
		System.out.println("Number of connection requests: "+req);
		return req;
	}
	
	/**
	 * @return
	 * if no requests received
	 */
	private Plan[] emptyPlanGeneration() {
		return null;
	}

	/**
	 * @param planindex
	 * @param candidateHosts
	 * @return one candidate placement plan
	 * generates one placement plan
	 */
	public Plan makePlan(short planindex, int[] candidateHosts) {
	    
		double [] tempCPULoad = new double [neighbors.size()];//Initialise to zero
    	double [] tempMemLoad = new double [neighbors.size()];//Initialise to zero
    	double [] tempStorageLoad = new double [neighbors.size()];//Initialise to zero
    	int assToCloud = 0;
    	
    	Plan p = new Plan(edgeAgentIndex, planindex, requests.size(), neighbors.size());//create a plan
         
    	int hostIndex;
    	for (int i = 0 ; i<requests.size() ; i++){
        	
    		hostIndex = candidateHosts[i]; //edge node index in the list of neighbours
        	//check if the assigned node has enough capacity?
            if (enoughCapacity(hostIndex, requests.get(i), tempCPULoad[hostIndex], tempMemLoad[hostIndex], tempStorageLoad[hostIndex])) {
            	    //update capacity of edge nodes
	            	p.updatePlan(hostIndex, i, requests.get(i), neighbors.get(hostIndex).CPUMax, neighbors.get(hostIndex).MemMax);
	            	tempCPULoad[hostIndex] += requests.get(i).getCPU();
	            	tempMemLoad[hostIndex] += requests.get(i).getMemory();
	            	tempStorageLoad[hostIndex] += requests.get(i).getStorage();
            	
            }
            else {//not enough capacity in candidate host so assign to cloud
            	p.incUnassTasks();
            	
            }
    	}
    	
    	return p;
        
	}
    
		/**
		 * @param EdgDevId
		 * @param service
		 * @param pre_cpu
		 * @param pre_mem
		 * @param pre_storage
		 * @return true if there is enough resource capacity available in the edge node with the id EdgDevId
		 */
		private boolean enoughCapacity(int EdgDevId, Request service, double pre_cpu, double pre_mem, double pre_storage) {
     		
    		if ((service.getCPU() + pre_cpu > Constants.utilRatio * neighbors.get(EdgDevId).CPUMax) ||
     				(service.getMemory() + pre_mem > Constants.utilRatio * neighbors.get(EdgDevId).MemMax) || 
     				(service.getStorage() + pre_storage > Constants.utilRatio * neighbors.get(EdgDevId).StorageMax)){
     			 return false;
     		}
     		else 
		return true;
	}

		/**
		 * @param numOfServices
		 * @return randomly selected hosts from all the edge nodes available in the network
		 */
		private int[] selectHosts(int numOfServices){
  	        int maxid = neighbors.size();
  	        int minid = 0;
  	    	int i = 0, candid; 
  	        int[] candidateEdgIndex = new int[numOfServices];
  	        
  	        while (i < numOfServices){
  	        	Random ran = new Random();
  	        	candid = ran.nextInt(maxid) + 0;
  	        	candidateEdgIndex[i] = candid;
  	            	
  	            i++;
  	            }
  	        
  	      return candidateEdgIndex;
    }
		
		

	
}