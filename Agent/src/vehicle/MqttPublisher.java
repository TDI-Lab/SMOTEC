package vehicle;
import java.util.concurrent.Callable;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MqttPublisher implements Callable<Void> {
	
	public static String TOPIC;
	public static String content;
    private IMqttClient client;
   
    /**
     * @param client
     * @param edgeId
     * @param handover 
     * @param us
     * sends a mqtt request to handover; topic=reconnect-edgeId, content=agentId:cpu:mem:storage
     */
    public MqttPublisher(IMqttClient client, int edgeId, Vehicle v, boolean handover) {
    	this.client = client;
    	if (handover == true) {
    		TOPIC = "reco-"+edgeId;//reco-"+edgeAgent
    		content = v.getName()+":"+v.getCPU()+":"+v.getMemory()+":"+v.getStorage()+":"+v.travelTimeId+":"+v.getHostNode().id+":"+v.getConnectedEdge().id;
    	}
    	else {
    		TOPIC = "disc-"+v.getConnectedEdge().id;//disc-"+edgeAgent
    		content = v.getMyId()+v.getCPU()+":"+v.getMemory()+":"+v.getStorage()+":"+v.travelTimeId+":"+v.getHostNode().id;
    	}
       
    }
	
    /**
     * @param vehclient
     * @param edgeagent
     * @param us
     * sends a mqtt request to connect and receive a host; topic=conn-edgeAgent, content=agentId:cpu:mem:storage
     */
	public MqttPublisher(IMqttClient vehclient, int edgeagent, Vehicle vehagent) {
		// TODO Auto-generated constructor stub
		 this.client = vehclient;
		 TOPIC = "conn-"+edgeagent;
         content = vehagent.getName()+":"+vehagent.getCPU()+":"+vehagent.getMemory()+":"+vehagent.getStorage()+":"+vehagent.travelTimeId;
	}

	
	@Override
	public Void call() throws Exception {
		if (!client.isConnected()) {
			System.out.println("not connected");
	            return null;
	        }
	            
	        MqttMessage msg = makeMessage();
	        msg.setQos(0);
	        msg.setRetained(false);
	        client.publish(TOPIC,msg);        
	        
	        return null;  
	}
	private MqttMessage makeMessage() {             
        byte[] payload = content.getBytes();        
        MqttMessage msg = new MqttMessage(payload); 
        System.out.println("Connection request published: topic="+TOPIC+" , content="+content);
        return msg;
    }
}