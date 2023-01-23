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
     * @param us.ge
     * sends a mqtt request; topic=request-edgeId, content=agentId:cpu:mem:storage
     */
    public MqttPublisher(IMqttClient client, int edgeId, Vehicle u) {
        this.client = client;
        
        TOPIC = "request-"+edgeId;
        content = u.getName()+":"+u.getCPU()+":"+u.getMemory()+":"+u.getStorage()+":"+u.travelTimeId;
    }
	
    
	@Override
	public Void call() throws Exception {
		if (!client.isConnected()) {
	            return null;
	        }
	            
	        MqttMessage msg = readEngineTemp();
	        msg.setQos(0);
	        msg.setRetained(false);
	        client.publish(TOPIC,msg);        
	        
	        return null;  
	}
	private MqttMessage readEngineTemp() {             
        //double temp =  80 + rnd.nextDouble() * 20.0;        
        byte[] payload = content.getBytes();        
        MqttMessage msg = new MqttMessage(payload); 
        System.out.println("Connection request published: topic="+TOPIC+" , content="+content);
        return msg;
    }
}