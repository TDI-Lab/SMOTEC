package pubsub;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
public class MqttSubscriber implements MqttCallback  {

	/** The broker url. */
	//private static final String brokerUrl ="tcp://localhost:1883";
	private static final String brokerUrl ="mosquitto:1883";

	/** The client id. */
	private static final String clientId = "clientId";

	User myUser;
	/** The topic. 
	 * @param waituntil */

	private boolean isHosted = false;
	

	public void subscribe(int edgeId, User u, boolean waituntil) {
		//	logger file name and pattern to log
		String topic = "response-"+edgeId;
		myUser = u;
		
		MemoryPersistence persistence = new MemoryPersistence();
		System.out.println("Subscriber running");
		try
		{

			MqttClient sampleClient = new MqttClient(brokerUrl, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);

			System.out.println("checking");
			System.out.println("Mqtt Connecting to broker: " + brokerUrl);

			sampleClient.connect(connOpts);
			System.out.println("Mqtt Connected");

			sampleClient.setCallback(this);
			
			sampleClient.subscribe(topic);
			if(waituntil) {
				
			
				while(isHosted ) {
				
				}
				
			}
			System.out.println("Subscribed");
			System.out.println("Listening");

		} catch (MqttException me) {
			System.out.println(me);
		}
	}

	 //Called when the client lost the connection to the broker
	public void connectionLost(Throwable arg0) {
		
	}

	//Called when a outgoing publish is complete
	public void deliveryComplete(IMqttDeliveryToken arg0) {

	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {

		System.out.println("| Topic:" + topic);
		System.out.println("| Message: " +message.toString());//extract target edge node to communicate
		System.out.println("-------------------------------------------------");
		int hid = 0;//extract info;
		if (true) {
			
			myUser.setHostServerCloudlet(hid);
			isHosted = true;
		
		}
		

	}

	public void subscribe(int connectedEdge, int myId) {
		// TODO Auto-generated method stub
		
	}

}


