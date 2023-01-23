package vehicle;
import java.util.StringTokenizer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.concurrent.CountDownLatch;


public class MqttSubscriber implements MqttCallback  {

	/** The broker url. */
	//private static final String brokerUrl ="tcp://localhost:1883";
	private static final String brokerUrl ="tcp://localhost:1883";
	private static String clientId ;
	private int hostId = -1;
	MqttClient sampleClient;
	

	/** The topic. 
	 * @param waituntil */

	private boolean isHosted = false;
	

	public MqttSubscriber(String id) {
		// TODO Auto-generated constructor stub
		clientId = id;
	}

	public void subscribe(int edgeId) {
		//	logger file name and pattern to log
		String topic = "response-"+edgeId;
		//myUser = u;
		
		MemoryPersistence persistence = new MemoryPersistence();
		//System.out.println("-------------------------------");
        System.out.println("Subscriber running");
		try
		{

			 sampleClient = new MqttClient(brokerUrl, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);

			 System.out.println("Connecting to broker: " + brokerUrl);

			sampleClient.connect(connOpts);
			System.out.println("Connected and Waiting for Response from Edge Device "+edgeId);

			sampleClient.setCallback(this);
			
			sampleClient.subscribe(topic);
			//if(waituntil) {
				
			
				//while(!isHosted ) {
					
				//}
				
				//}
				// System.out.println("-------------------------------");
		         
			//}
			
			

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
		//System.out.println("-------------------------------------------------");
		
		StringTokenizer st1 = new StringTokenizer(message.toString(),":");
		
		while (st1.hasMoreTokens()) {
           
			if (st1.nextToken().compareTo(clientId)==0) {
				//System.out.println("message match"+);
				
		        setHostId(Integer.parseInt(st1.nextToken()));
		    
		        isHosted = true;
				sampleClient.disconnect();
	            sampleClient.close();
			}
		}

	}

	
	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
}


