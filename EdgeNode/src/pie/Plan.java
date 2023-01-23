package pie;

import java.util.ArrayList;
import java.util.List;



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
    int assTasks;
    int assToCloud;//costs[7]
    
    double dlViolCost;//costs[0]
    double energyCost;//costs[1]
    double procCost;//costs[2]
    double storCost;//costs[3]
    double deplCost;//costs[4]
    double commCost;//costs[5]
    double co2EmitCost;//costs[6]
    public double [] costs;
	
	double totalLocalCost;
    double globalCost;
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
	        assTasks= 0;
	        assToCloud= 0;
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
        	  setCostsToZero();
        	   
          }
    }
    
    private void setCostsToZero() {
		// TODO Auto-generated method stub
    	setTotalLocalCost(0);
    	setUnassTasks(0);
    	setAssTasks(0);
    	setAssToCloud(0);
    	setDlViolCost(0);
    	setEnergyCost(0);
    	setProcCost(0);
    	setStorCost(0);
    	setDeplCost(0);
    	setCommCost(0);
    	setCo2EmitCost(0);	
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
		
        
		/* memory part:
		 * utilPlan[hostIndex + Constants.EDGE_ROUTERS+Constants.BACKBONE_ROUTERS] =
		 * (ms.getMemDemand() + utilPlan[hostIndex +
		 * Constants.EDGE_ROUTERS+Constants.BACKBONE_ROUTERS] * memCapacity) /
		 * memCapacity; wlPlan[hostIndex +
		 * Constants.EDGE_ROUTERS+Constants.BACKBONE_ROUTERS] += ms.getMemDemand();
		 */

         		
        
   }
 	
	public double getCosts(int i) {
		return costs[i];
	} 
	public double getGlobalCost() {
		return globalCost;
	}

	public void setGlobalCost(double globalCost) {
		this.globalCost = globalCost;
	}
	
	public void setTotalLocalCost(double calcLocalCost) {
		// TODO Auto-generated method stub
		totalLocalCost = calcLocalCost; 
		
		
	}
    public double getTotalLocalCost() {
		return totalLocalCost;
	}

	public void setTotalLocalCost() {
		this.totalLocalCost = dlViolCost + energyCost + procCost + storCost + deplCost + commCost + co2EmitCost;
		
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
	public int getAssTasks() {
		return assTasks;
	}

	public void setAssTasks(int assTasks) {
		this.assTasks = assTasks;
	}

	public int getAssToCloud() {
		return assToCloud;
	}

	public void setAssToCloud(int assToCloud) {
		this.assToCloud = assToCloud;
		
	}

	public int getEdgeNode() {
		return edgeNode;
	}

	public void setEdgeNode(int edgeNode) {
		this.edgeNode = edgeNode;
	}

	public double getDlViolCost() {
		return dlViolCost;
	}
	
    public void setDlViolCost(double dlViolCost) {
		this.dlViolCost = dlViolCost;
		
	}

	public double getEnergyCost() {
		return energyCost;
		
	}

	public void setEnergyCost(double energyCost) {
		this.energyCost = energyCost;
		
	}

	public double getProcCost() {
		return procCost;
	}

	public void setProcCost(double procCost) {
		this.procCost = procCost;
		
	}

	public double getStorCost() {
		return storCost;
	}

	public void setStorCost(double storCost) {
		this.storCost = storCost;
		
	}

	public double getDeplCost() {
		return deplCost;
	}

	public void setDeplCost(double deplCost) {
		this.deplCost = deplCost;
		
	}

	public double getCommCost() {
		return commCost;
	}

	public void setCommCost(double commCost) {
		this.commCost = commCost;
		
	}

	 public double getCo2EmitCost() {
			return co2EmitCost;
		}

	public void setCo2EmitCost(double co2EmitCost) {
		this.co2EmitCost = co2EmitCost;
		
	}

	
}
