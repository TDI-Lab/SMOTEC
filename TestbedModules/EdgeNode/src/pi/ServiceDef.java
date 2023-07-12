package pi;

/**
 * @author zeinab
 * defines a collector service for a service request
 *
 */
public class ServiceDef {

	private String version;
    private Integer port;
    private String srvname;
    private String username;
	private String password;
	private String appname;
	private int targetPort;
	private int nodePort;
	private int portTopUp = 22400;
    
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
		this.targetPort = i+portTopUp;
	}
	public int getNodePort() {
		return nodePort;
	}
	public void setNodePort(int i) {
		this.nodePort = i+portTopUp;
	}

    public Integer getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port+portTopUp;
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
        return "---" + "\n"
        		+"apiVersion: v1\n"
        		+"kind: Service\n" 
                +"metadata:\n" 
                +"  name: " + srvname + "\n"
                +"spec:\n" 
                +"  selector:\n"
                +"    app: "+ appname + "\n"
                +"  type: NodePort" + "\n"
                +"  ports:\n" 
                +"    - protocol: TCP\n"
                +"      port: " + getPort() + "\n"
                +"      targetPort: " + getTargetPort() + "\n"
        		+"      nodePort: "+ getNodePort();
    }
}
