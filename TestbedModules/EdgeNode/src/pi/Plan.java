package pi;



/**
 * @author zeinab
 * defines  a service placement plan which includes a binary array showing the mapping from service requests to available edge nodes
 * an associated array showing the utilization of edge nodes corresponding to the binary plan.
 */
public class Plan {
	
	int edgeNode;
    public short planIndex;
    int agIndex;//index of the EdgeAgent
    private int numOfEdgeNodes;
    public int ServiceSize;
	
	public boolean empty_node = false;//if no requests received by this EdgeAgent
    public short[][] x; //binary plan: mapping from requests to edge nodes
    public int[] y;
   
    public double[] utilPlan;//utilization plan with the dimension: numOfEdgeNodes*2;//one half for CPU and one half for Memory 
    
    public double[] wlPlan;// workload 
    public boolean selected = false;
    
    //public double cost;
    int unassTasks;
    double migCost;
   
	/**
     * @param agindex
     * @param planindex
     * @param serviceSize
     * @param nodes
     * 
     * if service list is empty --> plan contains zero as workload and utilization value
     */
    public Plan(int agindex, short planindex, int serviceSize, int nodes) {
        
        	planIndex = planindex;
        	agIndex = agindex;
        	ServiceSize = serviceSize;
        	unassTasks = 0;
        	migCost = 0;
        	numOfEdgeNodes = nodes;
	        
        	if (serviceSize != 0) {
	        	
	        	x = new short[serviceSize][nodes];
		        y = new int[serviceSize];
		    }
	        
	        utilPlan = new double[2*(nodes)];//CPU and memory
            wlPlan = new double[2*(nodes)];//CPU and memory
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

	/**
	 * @param hostIndex
	 * @param serviceIndex
	 * @param ms
	 * @param mipsCapacity
	 * @param memCapacity
	 * updates utilization and binary plan whenever a service is deployed to an EdgeNode
	 */
	public void updatePlan(int hostIndex, int serviceIndex, Request ms, double mipsCapacity, double memCapacity) {
		
 		x[serviceIndex][hostIndex] = 1;//update binary plan
 		y[serviceIndex] = hostIndex;
 		
 		//update CPU utilization
        utilPlan[hostIndex] = (ms.getCPU() + utilPlan[hostIndex]*mipsCapacity) / (mipsCapacity);
        //update cpu load in mips
        wlPlan[hostIndex] += ms.getCPU();
        
        //update memory load in MB
		wlPlan[hostIndex+numOfEdgeNodes] += ms.getMemory();
		//update memory utilization
        utilPlan[hostIndex + numOfEdgeNodes] = wlPlan[hostIndex+numOfEdgeNodes] / memCapacity;
		         
   }
 	
	public double getCosts() {
		return migCost+unassTasks;
	} 
	
	public void setCost(int c) {
	
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
