package V2I;

public class VolumDef {


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
    public String toStringEdge() {
    	return  "        volumeMounts:\n"
    		    +"        - name: pvc-nfs-pv1\n"
    		    +"          mountPath: /datasets/\n"
    		    +"        - name: pvc-nfs-pv1out\n"
    		    +"          mountPath: /output/\n"
    		    +"      nodeSelector:\n"
    		    +"        nn: "+nodeSelector+"\n"
    		    +"      volumes:\n"
    		    +"      - name: pvc-nfs-pv1\n"
    		    +"        persistentVolumeClaim:\n"
    		    +"          claimName: pvc-nfs-pv1\n"
    		    +"      - name: pvc-nfs-pv1out\n"
    		    +"        persistentVolumeClaim:\n"
    		    +"          claimName: pvc-nfs-pv1out";
    	
    }
    public String toStringAgent() {
    	return  "\n      nodeSelector:\n"
    		    +"        nn: "+nodeSelector;
    }
}
