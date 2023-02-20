package pi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * @author rooyesh
 *
 */
public class Plan {
	int edgeNode;//associated node
    public short planIndex;//excessive
    int agIndex;//index of the associated cloudlet in the cloudlet list 
    
	public boolean empty_node = false;
    public int ServiceSize;
	public short[][] x; // x_aj
    public int[] y;
    /**
     * total mips load on hosts
     */
    public double[] utilPlan;// = new double[numOfNodes*2];//CPU and Mem
    /**
     * total "mi per request" load on hosts
     */
    public double[] wlPlan;// = new double[numOfNodes*2];//CPU : mi per request and Mem
    public boolean selected = false;
    
    //public double cost;
    int unassTasks;
    double migCost;
   
	private int numOfServers;
	
        		
    /**Global response
     * The constructor of the class. Initializes the arrays 
     * @param numServices the number of services
     * @param numFogNodes the number of fog nodes
     * @param numCloudServers the number of cloud servers
     */
    
    
    //service list is empty --> plan contains zero as workload and utilization
    public Plan(int agindex, short planindex, int serviceSize, int nodes) {
        
        	planIndex = planindex;
        	agIndex = agindex;
        	ServiceSize = serviceSize;
        	unassTasks = 0;
        	migCost = 0;
        	
	        numOfServers = nodes;
	        if (serviceSize != 0) {
	        	
	        	x = new short[serviceSize][nodes];
		        y = new int[serviceSize];
		        //v = new int[serviceSize][Constants.EDGE_ROUTERS+Constants.BACKBONE_ROUTERS];
	            //d = new double[serviceSize][Constants.EDGE_ROUTERS+Constants.BACKBONE_ROUTERS];
	            //Vper = new double[serviceSize];
		        
	        }
	        
	        utilPlan = new double[2*(nodes)];//CPU and energy
            wlPlan = new double[2*(nodes)];//CPU and energy
            initializewl();
            
          if (serviceSize == 0 ) {
        	  empty_node = true;
              //zero other parameters like costs
        	  migCost = 0;unassTasks = 0;
        	   
          }
    }
    
  
  	private void initializewl() {
  		
		for(int i = 0; i<wlPlan.length ; i++) {
			wlPlan[i] = 0;
			
		}
		
		
	}

	
 	/*
		 * //Memory utilPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes]
		 * = (ms.getMemDemand() +
		 * wlPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes]) /
		 * (ramCapacity); wlPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes] += ms.getMemDemand();
        */
	public void updatePlan(int hostIndex, int serviceIndex, Request ms, double mipsCapacity, double memCapacity) {
		
 		x[serviceIndex][hostIndex] = 1;
 		y[serviceIndex] = hostIndex;
 		
 		//CPU demand: be careful with utilPlan:
        utilPlan[hostIndex] = (ms.getCPU() + utilPlan[hostIndex]*mipsCapacity) / (mipsCapacity);
        //mi per request:
        wlPlan[hostIndex] += ms.getCPU();
        //deployPlan[hostIndex] = true;
		
        
		// memory part: 
        wlPlan[hostIndex+numOfServers] += ms.getMemory();
		utilPlan[hostIndex + numOfServers] = wlPlan[hostIndex+numOfServers] / memCapacity;
		 

         		
        
   }
 	
	public double getCosts() {
		return migCost+unassTasks;
	} 
	
	public void setCost(int c) {
		
		/*
		for (int i = 0; i<numOfServers; i++)
			if(requests.get(i).hostId != y[i])
				migCost++;
				*/
		
	}
	public int getUnassTasks() {
		return unassTasks;
	}

	public int getAgIndex() {
		return agIndex;
	}

	public void setAgIndex(int agIndex) {
		this.agIndex = agIndex;
	}

	public void setUnassTasks(int unassTasks) {
		this.unassTasks = unassTasks;
	}

	public void incUnassTasks() {
		this.unassTasks++;
	}
	

	public int getEdgeNode() {
		return edgeNode;
	}

	public void setEdgeNode(int edgeNode) {
		this.edgeNode = edgeNode;
	}

		
}
