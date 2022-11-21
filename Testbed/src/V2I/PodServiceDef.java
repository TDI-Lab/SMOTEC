package V2I;

public class PodServiceDef {

	private String version;
    private Integer port;
    private String srvname;
    private String username;
	private String password;
	private String appname;
	private int targetPort;
	private int nodePort;
    
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
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public int getTargetPort() {
		return targetPort;
	}
	public void setTargetPort(int i) {
		this.targetPort = i;
	}
	public int getNodePort() {
		return nodePort;
	}
	public void setNodePort(int i) {
		this.nodePort = i;
	}

    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
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
    
    /*
    apiVersion: v1
    kind: Service
    metadata:
      name: python-pub-service1
    spec:
      selector:
        app: python-sub1
      type: LoadBalancer  
      ports:
        - protocol: TCP
          port: 8083
          targetPort: 8083
          nodePort: 30003
      */    
    @Override      
    public String toString() {
        return "\n---" + "\n"
        		+"apiVersion: v1\n"
        		+"kind: Service\n" 
                +"metadate:\n" 
                +"  name: " + srvname + "\n"
                +"spec:\n" 
                +"  selector:\n"
                +"    app: "+ appname + "\n"
                +"  type: LoadBalancer" + "\n"
                +"  ports:\n" 
                +"    - protocol: TCP\n"
                +"    port: " + port + "\n"
                +"    targetPort: " + targetPort + "\n"
                +"    nodePort: " + nodePort;
    }
}
