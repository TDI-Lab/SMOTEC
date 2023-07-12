package V2I;
	
/**
 *  identifies pod definition 
 */
public class PodDefinition {
		
	private String version = "apps/v1";
	private String podname;
	private String appname;
	private int numReplica = 1;
	private String registrykeyName;
	private String containerName;
	private String imageName;
	private String imagePullPolicy = "Always";
	
	private int type;
	private int arg1;
	private int arg2;
	private String arg3;
	private String arg4;
	private int arg5;
	private String arg6;
	
	private int containerPort1;
	private int containerPort2;
	private int containerPort3;
	
	
	
	public String getPodname() {
		return podname;
	}
	
	public void setPodname(String podname) {
		this.podname = podname;
	}
	
	public String getAppname() {
		return appname;
	}
	
	public void setAppname(String appname) {
		this.appname = appname;
	}
	
	public int getNumReplica() {
		return numReplica;
	}
	
	public void setNumReplica(int numReplica) {
		this.numReplica = numReplica;
	}
	
	public String getMyregistrykey() {
		return registrykeyName;
	}
	
	public void setMyregistrykey(String myregistrykey) {
		this.registrykeyName = myregistrykey;
	}
	
	public String getContainerName() {
		return containerName;
	}
	
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	public int getContainerPort() {
		return containerPort1;
	}
	
	public void setContainerPort(int containerPort) {
		this.containerPort1 = containerPort;
	}
	    
	@Override
	public String toString() {
	    	return "apiVersion: " + version + "\n"
	        		+"kind: Deployment\n" 
	                +"metadata: " + "\n"
	                +"  name: " + podname + "\n"
	                +"  labels: " + "\n"
	                +"    app: " + appname + "\n"
	                +"spec:\n" 
	                +"  replicas: " + numReplica + "\n"
	                +"  selector:\n"
	                +"    matchLabels:\n"
	                +"      app: " + appname + "\n"
	                +"  template:\n"
	                +"    metadata:\n"
	                +"      labels:\n"
	                +"        app: " + appname + "\n"
	                +"    spec:\n"
	                +"      imagePullSecrets:\n"
	                +"      - name: " + registrykeyName+ "\n"
	                +"      containers:\n"
	                +"      - name: " + containerName + "\n"
	                +"        image: " + imageName + "\n"
	                +"        imagePullPolicy: " + imagePullPolicy+ "\n"
	                +"        ports:\n"
	                +"        "+getPorts()
	    			+"        args: "+getArgs();
	    		
	                 
	    }

	public String getPorts() {
		
		if (type == 2)//service distributor
			return "- containerPort: " + containerPort1 + "\n"+"        - containerPort: " + containerPort2 + "\n";
		else if (type == 3)//edgeagent
			return "- containerPort: " + containerPort1 + "\n"+"        - containerPort: " + containerPort2 + "\n"+"        - containerPort: " + containerPort3 + "\n";
		else//vehicleagent
			return "- containerPort: " + containerPort1+ "\n";
		
	}
	
	
	/**
	 * @return input arguments for deployment files of containers
	 */
	public String getArgs(){
		
		if (type == 3)//edgeagent
			return "[\""+arg1+"\"]";
			
		else if (type == 2)//service distributor
			return "[\""+ containerPort1 +"\", \""+ containerPort2 +"\"]";
		
		else//vehicleagent
			return "[\""+arg1+"\", \""+arg3+"\", \""+arg4+"\", \""+arg5+"\", \""+arg6+"\"]";
		
	}

	/**
	 * @param i
	 * @param containerImagesPath
	 * @param srvdislis
	 * @param srvdisres
	 * @param eposplans
	 * Sets input arguments for edgeagent
	 */
	public void setArg(int i, String containerImagesPath, String srvdislis, String srvdisres, int eposplans) {
		arg1 = i ;
		/*
		 * arg3 = containerImagesPath; arg4 = srvdislis; arg6 = srvdisres; arg5 =
		 * eposplans;
		 */
		
	}

	/**
	 * @param port1
	 * @param port2
	 * Sets container ports for service distributor
	 */
	public void setContainerPort(int port1, int port2) {
		// TODO Auto-generated method stub
		type = 2;
		containerPort1 = port1;
		containerPort2 = port2; 
		
		
	}
	
	public void setArg(int i, int j) {
		// TODO Auto-generated method stub
		arg1 = i ;//port
		arg2 = j;
			
		}

	/**
	 * @param port1
	 * @param port2
	 * @param port3
	 * Sets container port for edgeagent
	 */
	public void setContainerPort(int port1, int port2, int port3) {
		// TODO Auto-generated method stub
		type = 3;
		containerPort1 = port1;
		containerPort2 = port2; 
		containerPort3 = port3;
	}
	
	/**
	 * @param id
	 * @param edge_COVERAGE
	 * @param cpu
	 * @param memory
	 * @param storage
	 * @param mobDataset
	 * Sets input arguments for vehicleagent container 
	 */
	public void setArg(int id, int edge_COVERAGE, int cpu, int memory, int storage, String mobDataset) {
		type = 0;
		arg1 = id ;
		arg2 = edge_COVERAGE;
		arg3 = ""+cpu;
		arg4 = ""+memory;
		arg5 = storage;
		arg6 = mobDataset;
		
	}
	}
