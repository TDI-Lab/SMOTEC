package pie;

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

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import experiment.IEPOSExperiment;


public class SrvMapper {

	List<Request> requests = new ArrayList<>();
	List<Neighbor> neighbors = new ArrayList<>();
	//Map<Integer, Integer> Plan = new HashMap<>();
	public Plan [] Plans;
	int agIndex;
	
	public SrvMapper(List<Request> requests2, List<Neighbor> neighbors2, int myId) {
		// TODO Auto-generated constructor stub
		System.out.println("Edge node "+myId+" starts plan generation...");
		requests = requests2;
		neighbors = neighbors2;
		agIndex = myId;
		
	}
	
	/**
	 * @param args
	 * @param EdgeNode runs IEPOS, read its output, publishes selected hosts to agents, asks K3S to deploy services on selected hosts,
	 *  updates resources accordingly
	 * @param srvToHostSet saves the mapping of agents to service hosts
	 */
	public void SelectAndDeploy(String args[], MqttClient EdgeNode, Map<Integer, Integer> srvToHostSet) {
		// TODO Auto-generated method stub
		IEPOSExperiment iepos = new IEPOSExperiment();
	    iepos.main(args);
	        
		int planId = Utility.ReadMapping(agIndex);
		System.out.println("Selected placement plan index: "+planId);
	    
		responseAgents(EdgeNode, planId, Plans[planId]);
		
		//K3SDeployPlan();
		
		//UpdateResources(Plans[planId], srvToHostSet);//release resources here and from k3s???
		
		
	}
	/**
	 * @param sPlan selected plan by IEPOS
	 * @param srvToHostSet mapping of agents to service hosts
	 * updates edge nodes' resources with respect to the deployed services
	 */
	private void UpdateResources(Plan sPlan, Map<Integer, Integer> srvToHostSet) {
		
		int host;
		for (int i = 0; i<sPlan.y.length; i++) {
			host = sPlan.y[i];
			neighbors.get(host).CPU -= requests.get(i).getCPU();
			neighbors.get(host).Memory -= requests.get(i).getMemory(); 
			neighbors.get(host).Storage -= requests.get(i).getStorage(); 
			neighbors.get(host).IncRunningSrv();
			srvToHostSet.put(requests.get(i).reqId, requests.get(i).hostId);
			
		}
		
	}
	/**
	 * create deployment .yaml file for every request
	 * run a shell script to ask k3s to deploy the files 
	 */
	private void K3SDeployPlan() {
		
		java.util.Map<String, String> env = new HashMap<String, String>();
		//write deployments for agent's service with resource demands using json:
		int i = 0;
		for (i = 0 ; i<requests.size() ; i++)  
			Utility.creatYaml(i, requests.get(i));
		
		
		try {
			//while true; do curl -s https://some.site.com/someImage.jpg > /dev/null & echo blah ; done
			//ask to release already allocated resources to this service
				String ShCommand = "bash"+Constants.srvScript+ requests.size()+ " srvid/port";
			    Process p = Runtime.getRuntime().exec(ShCommand);
			    p.waitFor();
			    
			    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			    bw.write("2012");
			    
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
	 * @param EdgeNode
	 * @param planId
	 * @param selectedPlan
	 * publish messages to the agents who received requests from containing the index of the selected hosts for executing their services
	 */
	private void responseAgents(MqttClient EdgeNode, int planId, Plan selectedPlan) {
		//publishing hosts:
		System.out.println("Sending hosts to agents...");
		for (int i = 0; i <selectedPlan.y.length; i++) {
			
			String ResTopic = "response-"+agIndex+":"+requests.get(i).reqId;//response message topic
			String str = "host:"+selectedPlan.y[i];//response message payload
			requests.get(i).hostId = selectedPlan.y[i];//set selected host to the agent
	        byte[] payload = str.getBytes();        
		    MqttMessage respoMsg = new MqttMessage(payload); 
		    
		    try {
				EdgeNode.publish(ResTopic, respoMsg);
			} catch (MqttPersistenceException e) {
				
				e.printStackTrace();
			} catch (MqttException e) {
				
				e.printStackTrace();
			}
            
	        
		}
	}
	
	/**
	 * generate placement plans for received requests
	 * @return and array of candidate plans
	 */
	public Plan[] generatePlans(){
		
		Plans = new Plan [Constants.EPOS_NUM_PLANS];
		
		 if (requests.size() == 0) {//code that does not access at all
			 System.out.println("Request size is zero");	
			 //Plans = emptyPlanGeneration();
	         }
	     else {
	        	 
        	 for(short planIndex=0 ; planIndex < Constants.EPOS_NUM_PLANS ; planIndex++){  
	        	 //System.out.println("Plan "+planIndex+" generating...");
		         //selectedHosts = selectHosts(serviceList.size()); 
		         Plans[planIndex] = makePlan(planIndex, selectHosts(requests.size()));
	         } 
        	 Utility.writePlans(Plans, agIndex);
	         }
	         
	    System.out.println("End of plan generation...........");
	    return Plans;
	         
	}
	
	private pie.Plan[] emptyPlanGeneration() {
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
    	
    	Plan p = new Plan(agIndex, planindex, requests.size(), neighbors.size());
         
    	int hostIndex;
    	for (int i = 0 ; i<requests.size() ; i++){
        	//service = serviceList.get(i);
            hostIndex = candidateHosts[i]; //index in the list of serverCloudlets
            //System.out.println("service "+i);
            if (enoughCapacity(hostIndex, requests.get(i), tempCPULoad[hostIndex], tempMemLoad[hostIndex], tempStorageLoad[hostIndex])) {
            	    	 
            	p.updatePlan(hostIndex, i, requests.get(i), neighbors.get(hostIndex).CPU, neighbors.get(hostIndex).Memory);
            	tempCPULoad[hostIndex] += requests.get(i).getCPU();
            	tempMemLoad[hostIndex] += requests.get(i).getMemory();
            	tempStorageLoad[hostIndex] += requests.get(i).getStorage();
            	
            }
            else {//not enough capacity in candidate host so assign to cloud
            	hostIndex = assignToCloud();
            	if (enoughCapacity(hostIndex, requests.get(i), tempCPULoad[hostIndex], tempMemLoad[hostIndex], tempStorageLoad[hostIndex])) {
            	    //System.out.println("	assigining to cloud "+host);
            		p.updatePlan(hostIndex, i, requests.get(i), neighbors.get(hostIndex).CPU, 
            				neighbors.get(hostIndex).Memory);
                	tempCPULoad[hostIndex] += requests.get(i).getCPU();
	            	tempMemLoad[hostIndex] += requests.get(i).getMemory();
	            	tempStorageLoad[hostIndex] += requests.get(i).getStorage();
	            	assToCloud++;
            	}
            	else {
            		//System.out.println("Cloud node out of space");
            		p.incUnassTasks();
            	}
            }
	}
    	p.setTotalLocalCost();
    	p.setAssToCloud(assToCloud);
    	return p;
        
	}
    	/**
    	 * @return index of cloud node
    	 */
    	private int assignToCloud() {
		// TODO Auto-generated method stub
    		return neighbors.size()-1;
		
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
     		
    		if ((service.getCPU() + pre_cpu > Constants.utilRatio * neighbors.get(EdgDevId).getCPU()) ||
     				(service.getMemory() + pre_mem > Constants.utilRatio * neighbors.get(EdgDevId).Memory) || 
     				(service.getStorage() + pre_storage > Constants.utilRatio * neighbors.get(EdgDevId).Storage)){//check cpu
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
  	    	int i = 0, candid; double m,n;
  	        int[] candidateEdgIndex = new int[numOfServices];
  	        
  	        while (i < numOfServices){
  	        	//System.out.println("Service "+i+" :");
  	            candid = RandomGenerator.genUniformRandomBetween(minid, maxid-1);
  	            candidateEdgIndex[i] = candid;
  	            	
  	            i++;
  	            }
  	        
  	      return candidateEdgIndex;
    }

		
}
