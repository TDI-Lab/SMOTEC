package vehicle;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

//https://bytesofgigabytes.com/mqtt/java-as-mqtt-publisher-and-subscriber-client/
//first run your MqttSubscriber.java code and then run MqttPublisher.javas
public class MqttPublisher2 {
	
	static String clientId ;
	static int qos             = 0;
    /*hostname is localhost as mqtt publisher and broker are
      running on the same computer*/ 
    static String broker       = "tcp://localhost:1883";
    
    
    public MqttPublisher2(String id) {
    	clientId = id;
    }
    
    public void publish(int edgeId, int agentId) {

    	//Topic name 
        String topic = "request-"+edgeId;
        //data to be send
        String content = "id:"+agentId;
        MemoryPersistence persistence = new MemoryPersistence();
        boolean a = true;
        try {
	            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
	            MqttConnectOptions connOpts = new MqttConnectOptions();
	            connOpts.setCleanSession(true);
	            System.out.println("-------------------------------");
	            System.out.println("Publisher running");
	    		System.out.println("Connecting to broker: "+broker);
	            sampleClient.connect(connOpts);
	            System.out.println("Connected to broker");
	            
				//while(a == true) {
	            
	            	sendMessage(sampleClient, topic, content);
	            //}
	            sampleClient.disconnect();
	            sampleClient.close();
	           // System.exit(0);
            } 
        catch(MqttException me) {
	            System.out.println("reason "+me.getReasonCode());
	            System.out.println("msg "+me.getMessage());
	            System.out.println("loc "+me.getLocalizedMessage());
	            System.out.println("cause "+me.getCause());
	            System.out.println("excep "+me);
	            me.printStackTrace();
        }
        
        
    }

	private void sendMessage(MqttClient sampleClient, String topic, String content) throws MqttPersistenceException, MqttException {
		// TODO Auto-generated method stub
		System.out.println("Publishing message "+topic+" "+content);
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        sampleClient.publish(topic, message);
        System.out.println("Message published");
        System.out.println("--------------------------------");
	}
}