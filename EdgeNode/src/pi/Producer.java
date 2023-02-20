package pi;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class Producer {

	Context context ;
    Socket producer ;
	boolean pushType;
	Producer(Context context2) {
		context = context2;
		producer = context.socket(ZMQ.PUSH);
	}
    void send_message(String msg){
    	
    		//producer.connect(Constants.SRVDISPRO); 
    		producer.setSendTimeOut(2000);//not sure
    		int update_nbr; 
    		for (update_nbr = 0; update_nbr <3; update_nbr ++) {
    			producer.send(msg.getBytes());
    			
    		}
    		if (pushType)
    		System.out.println("Plans sent to service distributor: "+ msg);
    		else
    		System.out.println("Selected Host sent to VehicleAgent: "+ msg);
    			
	}
    
    public void close() {
    	System.out.println("closing 1");
    	producer.close (); 
		//context.term ();
		System.out.println("closing 2");
    	
	}
    public void open(String add, boolean dest){
    	pushType = dest;
    	if (dest) {
    		
    		producer.connect(add);
    	}
    	else {
    		producer.bind(add);
    	}
    		
		
    }

}
