package pi;

/**
 * @author zeinab
 * defines the edge node for deploying a service based on selected plan
 *
 */
public class HostNodDef {


	private String nodeSelector;
	
	public String getNodeSelector() {
		return nodeSelector;
	}

	public void setNodeSelector(String nodeSelector) {
		this.nodeSelector = nodeSelector;
	}
	
	public String toStringAgent() {
    	return  "      nodeSelector:\n"
    		    +"        nn: "+nodeSelector;
    }
    public String toStringSrv() {
    	return  "      nodeSelector:\n"
    		    +"        nn: "+nodeSelector+"\n";
    }
}
