package pi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


import experiment.IEPOSExperiment;


public class SrvMapper {

	List<Request> requests = new ArrayList<>();
	List<Neighbor> neighbors = new ArrayList<>();
	public Plan [] CandidatePlans;
	
	int edgeAgentIndex;
	
	public SrvMapper(int agent, List<Request> requests2, List<Neighbor> neighbors2) {
		// TODO Auto-generated constructor stub
		requests = requests2;
		neighbors = neighbors2;
		edgeAgentIndex = agent;
		System.out.println("edgeAgent "+edgeAgentIndex+" starts plan generation...");
		
		
	}
	/* 
	 * generate placement plans for received requests
	 * @return and array of candidate plans
	 */
	public Plan[] generatePlans(){
		
		CandidatePlans = new Plan [Constants.EPOS_NUM_PLANS];
		
		 if (requests.size() == 0) {//code that does not access at all
			 System.out.println("Requests for conection are  zero");	
			 //Plans = emptyPlanGeneration();
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
	
	private int agentsWithReq(List<Request> requests2) {
		// TODO Auto-generated method stub
		int req = 0;
		for (int i = 0;i<requests.size(); i++) 
			if (requests.get(i).type == Constants.CONREQ)
				req++;
				
		System.out.println("Num of conn requests: "+req);
		return req;
	}
	private Plan[] emptyPlanGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param planindex
	 * @param candidateHosts
	 * @return one candidate placement plan
	 */
	public Plan makePlan(short planindex, int[] candidateHosts) {
	    
		double [] tempCPULoad = new double [neighbors.size()];//Initialise to zero
    	double [] tempMemLoad = new double [neighbors.size()];//Initialise to zero
    	double [] tempStorageLoad = new double [neighbors.size()];//Initialise to zero
    	int assToCloud = 0;
    	
    	Plan p = new Plan(edgeAgentIndex, planindex, requests.size(), neighbors.size());
         
    	int hostIndex;
    	for (int i = 0 ; i<requests.size() ; i++){
        	//service = serviceList.get(i);
            hostIndex = candidateHosts[i]; //index in the list of serverCloudlets
            //System.out.println("service "+i);
            if (enoughCapacity(hostIndex, requests.get(i), tempCPULoad[hostIndex], tempMemLoad[hostIndex], tempStorageLoad[hostIndex])) {
            	    	 
            	p.updatePlan(hostIndex, i, requests.get(i), neighbors.get(hostIndex).CPUMax, neighbors.get(hostIndex).MemMax);
            	tempCPULoad[hostIndex] += requests.get(i).getCPU();
            	tempMemLoad[hostIndex] += requests.get(i).getMemory();
            	tempStorageLoad[hostIndex] += requests.get(i).getStorage();
            	
            }
            else {//not enough capacity in candidate host so assign to cloud
            	p.incUnassTasks();
            	
            }
	}
    	//p.setCost(requests);
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
     				(service.getStorage() + pre_storage > Constants.utilRatio * neighbors.get(EdgDevId).StorageMax)){//check cpu
     			//System.out.println("	capacity constraint on edge unsatisfied:");
     			//System.out.println("		cpu req: "+service.getCpuDemand1() +" host_cpu: "+ Constants.alpha * Constants.FP[FogDevId]+
    			//	" mem req: "+service.getMemDemand() + " host_mem: "+ Constants.alpha * Constants.FM[FogDevId]+" storage req:"+
    			//	service.getStorageDemand()+" host_storage: "+ Constants.alpha * Constants.FS[FogDevId]);
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

  	        	//candid = minid + (int)(Math.random() * ((maxid - minid) + 1));
  	            //candid = ThreadLocalRandom.current().nextInt(minid, maxid + 1);
  	           // System.out.println("candid "+candid);
  	            candidateEdgIndex[i] = candid;
  	            	
  	            i++;
  	            }
  	        
  	      return candidateEdgIndex;
    }
		
		

	
}