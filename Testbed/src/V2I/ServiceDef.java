package V2I;

/**
 * This class defines the services for SMOTEC components including vehicleagents, edgeagents, and servicedistributor
 */
public class ServiceDef {

	private String version;
  
    private String srvname;
    private String srvname1, srvname2, srvname3;
    private String username;
	private String password;
	private String appname;
	
	private String Pname1;
	private int targetPort1;
	private int Port1;
	private int nodePort1;
	
	private String Pname2;
	private int targetPort2;
	private int nodePort2;
	private int Port2;

	private String Pname3;
	private int targetPort3;
	private int nodePort3;
	private int Port3;

	private int pNum;
	
	

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSrvname() {
		return srvname;
	}
	public void setSrvname(String srvname) {
		this.srvname = srvname;
	}
	public void setSrvname(String srvname1, String srvname2) {
		this.srvname1 = srvname1;
		this.srvname2 = srvname2;
	}
	public void setSrvname(String srvname1, String srvname2, String srvname3) {
		this.srvname1 = srvname1;
		this.srvname2 = srvname2;
		this.srvname3 = srvname3;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	 
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override      
    public String toString() {
    	if (pNum == 1)
    	return "\n---" + "\n"
        		+"apiVersion: v1\n"
        		+"kind: Service\n" 
                +"metadata:\n" 
                +"  name: " + srvname + "\n"
                +"spec:\n" 
                +"  selector:\n"
                +"    app: "+ appname + "\n"
                +"  type: NodePort" + "\n"
                +"  ports:\n" 
                +getPorts(1);
        else if (pNum == 2)
        	return "\n---" + "\n"
    		+"apiVersion: v1\n"
    		+"kind: Service\n" 
            +"metadata:\n" 
            +"  name: " + srvname1 + "\n"
            +"spec:\n" 
            +"  selector:\n"
            +"    app: "+ appname + "\n"
            +"  type: NodePort" + "\n"
            +"  ports:\n" 
            +getPorts(1)
            +"\n---" + "\n"
    		+"apiVersion: v1\n"
    		+"kind: Service\n" 
            +"metadata:\n" 
            +"  name: " + srvname2 + "\n"
            +"spec:\n" 
            +"  selector:\n"
            +"    app: "+ appname + "\n"
            +"  type: NodePort" + "\n"
            +"  ports:\n" 
            +getPorts(2);
        else // (pNum == 3)
        	return "\n---" + "\n"
    		+"apiVersion: v1\n"
    		+"kind: Service\n" 
            +"metadata:\n" 
            +"  name: " + srvname1 + "\n"
            +"spec:\n" 
            +"  selector:\n"
            +"    app: "+ appname + "\n"
            +"  type: NodePort" + "\n"
            +"  ports:\n" 
            +getPorts(1)
            +"\n---" + "\n"
    		+"apiVersion: v1\n"
    		+"kind: Service\n" 
            +"metadata:\n" 
            +"  name: " + srvname2 + "\n"
            +"spec:\n" 
            +"  selector:\n"
            +"    app: "+ appname + "\n"
            +"  type: NodePort" + "\n"
            +"  ports:\n" 
            +getPorts(2)
            +"\n---" + "\n"
    		+"apiVersion: v1\n"
    		+"kind: Service\n" 
            +"metadata:\n" 
            +"  name: " + srvname3 + "\n"
            +"spec:\n" 
            +"  selector:\n"
            +"    app: "+ appname + "\n"
            +"  type: NodePort" + "\n"
            +"  ports:\n" 
            +getPorts(3);
        	
    }
    
    
	public void setPorts(String pname1, int port1, String pname2, int port2) {
		pNum = 2;
		setPname(pname1, pname2);
		setTargetPort(port1, port2);
		setNodePort(port1, port2);
	}
	
	public void setPorts(String pname1, int port1, String pname2, int port2, String pname3, int port3) {
		pNum = 3;
		setPname(pname1, pname2, pname3);
		setTargetPort(port1, port2, port3);
		setNodePort(port1, port2, port3);
	}
	
	public void setPort(String pname1, int port1) {
		pNum = 1;
		setPname(pname1);
		setTargetPort(port1);
		setNodePort(port1);
	}
	
	private void setNodePort(int i) {
		nodePort1 = i;
	}
	private void setTargetPort(int port12) {
		targetPort1 = port12;
		this.Port1 = port12;
	}
	private void setPname(String pname12) {
		Pname1 = pname12;
	}
	public String getPorts(int index) {
		
		if (index == 2)
			return "    - protocol: TCP\n"
	        +"      port: " + Port2 + "\n"
	        +"      name: " + Pname2 +"\n"
	        +"      targetPort: " + targetPort2 + "\n"
	        +"      nodePort: " + nodePort2 ;
		else if (index == 3)
			return "    - protocol: TCP\n"
	        +"      port: " + Port3 + "\n"
	        +"      name: " + Pname3 +"\n"
	        +"      targetPort: " + targetPort3 + "\n"
	        +"      nodePort: " + nodePort3 ;
			
		else
			return "    - protocol: TCP\n"
	        +"      port: " + Port1 + "\n"
	        +"      name: " + Pname1 +"\n"
	        +"      targetPort: " + targetPort1 + "\n"
	        +"      nodePort: " + nodePort1;
	}
	
	 
	 
		public void setPname(String pname, String pname2) {
			this.Pname1 = pname;
			this.Pname2 = pname2;
		}
		
		public void setTargetPort(int i, int j) {
			this.targetPort1 = i;
			this.Port1 = i;
			
			this.targetPort2 = j;
			this.Port2 = j;
		}
		
		
		public void setNodePort(int i, int j) {
			this.nodePort1 = i;
			this.nodePort2 = j;
		}

		public void setPname(String pname, String pname2, String pname3) {
			this.Pname1 = pname;
			this.Pname2 = pname2;
			this.Pname3 = pname3;
		}
		
		public void setTargetPort(int i, int j, int k) {
			this.targetPort1 = i;
			this.Port1 = i;
			
			this.targetPort2 = j;
			this.Port2 = j;
			
			this.targetPort3 = k;
			this.Port3 = k;
		}
		
		
		public void setNodePort(int i, int j, int k) {
			this.nodePort1 = i;
			this.nodePort2 = j;
			this.nodePort3 = k;
		}
	   
	    
	   
}
