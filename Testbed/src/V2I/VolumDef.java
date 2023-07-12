package V2I;

/**
 * This class specifies the host nodes for container deployment and
 *  defines the volumes for containers to access the configuration values and 
 */
public class VolumDef {


	private String nodeSelector;
	private String configMap;
	private String mountPath;
	private String subPath;
	
	public void setConfigMap(String configMap) {
		this.configMap = configMap;
	}

	public void setMountPath(String edgeMountPath) {
		this.mountPath = edgeMountPath;
	}

	public void setSubPath(String edgeSubPath) {
		this.subPath = edgeSubPath;
	}
	
	public String getNodeSelector() {
		return nodeSelector;
	}

	public void setNodeSelector(String nodeSelector) {
		this.nodeSelector = nodeSelector;
	}

    public String toStringVol() {
    	return  "\n        volumeMounts:\n"
    		    +"        - name: "+configMap+"\n"
    		    +"          mountPath: "+mountPath+"\n"
    		    +"          subPath: "+subPath+"\n"
    		    +"      volumes:\n"
    		    +"      - name: "+configMap+"\n"
    		    +"        configMap:\n"
    		    +"          name: "+configMap;
    	
    }

    public String toStringNodeSel() {
    	return  "\n      nodeSelector:\n"
    		    +"        nn: "+nodeSelector;
    }
}
