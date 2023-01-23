package pie;
	import java.util.Random;
	import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttClient;
	import org.eclipse.paho.client.mqttv3.MqttMessage;
	
public class Pub implements Callable<Void> {
	 
		public static String APid;
		
	    public static final String TOPIC = "response-"+APid;
	    public static final String TOPIC1 = APid+":10000";
	    
	    private IMqttClient client;
	    private MqttMessage msg;

	    public Pub(IMqttClient client, String id, MqttMessage msg1) {
	        this.client = client;
	        APid = id;
	        msg =msg1;
	       
	        
	    }

	    @Override
	    public Void call() throws Exception {
	        
	        if (!client.isConnected()) {
	           // log.info("[I31] Client not connected.");
	            return null;
	        }
	            
	        //MqttMessage msg = readEngineTemp();
	        msg.setQos(0);
	        msg.setRetained(false);
	        client.publish(TOPIC1,msg);        
	        
	        System.out.println("Message sent "+msg);
	        return null;        
	    }
	    
	    /**
	     * This method simulates reading the engine temperature
	     * @return
	     */
	    private MqttMessage readEngineTemp() {             
	        //double temp =  80 + rnd.nextDouble() * 20.0;        
	        //byte[] payload = String.format("T:%04.2f",temp).getBytes();  
	    	String host = "2";
	        String str = "10000:"+"2";
	    	byte[] payload = str.getBytes();        
		    MqttMessage msg = new MqttMessage(payload); 
	        return msg;
	    }
	}