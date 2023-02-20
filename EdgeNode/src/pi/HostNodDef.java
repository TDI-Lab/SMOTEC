package pi;

public class HostNodDef {


	private String nodeSelector;
	
	public String getNodeSelector() {
		return nodeSelector;
	}

	public void setNodeSelector(String nodeSelector) {
		this.nodeSelector = nodeSelector;
	}

    
    
    
    /*
    volumeMounts:
        - name: pvc-nfs-pv1
          mountPath: /datasets/
        - name: pvc-nfs-pv1out
          mountPath: /output/
      nodeSelector:
        nn: client2
      volumes:
      - name: pvc-nfs-pv1
        persistentVolumeClaim:
          claimName: pvc-nfs-pv1
      - name: pvc-nfs-pv1out
        persistentVolumeClaim:
          claimName: pvc-nfs-pv1out 
      */    
 
    public String toStringAgent() {
    	return  "      nodeSelector:\n"
    		    +"        nn: "+nodeSelector;
    }
    public String toStringSrv() {
    	return  "      nodeSelector:\n"
    		    +"        nn: "+nodeSelector;
    }
}
