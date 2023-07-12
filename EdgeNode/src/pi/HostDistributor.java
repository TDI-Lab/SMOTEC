package pi;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * @author zeinab
 * This class sends the selected hosts for service requests from mobile agents to the agents.
 */
public class HostDistributor {

	Context context ;
    Socket producer ;
	boolean pushType;
	private int megSendNum = 10;
	
	HostDistributor(Context context2) {
		context = context2;
		producer = context.socket(ZMQ.PUSH);
	}
    void send_message(String msg){
    	
    		producer.setSendTimeOut(2000);
    		int update_nbr; 
    		for (update_nbr = 0; update_nbr < megSendNum ; update_nbr ++) {
    			producer.send(msg.getBytes());
    			
    		}
    		Constants.numMsg += megSendNum;
    		System.out.println("Selected host sent to VehicleAgent: \""+ msg+"\"");
    			
	}
    
    public void close() {
    	producer.close (); 
		
	}
    public void open(String add){
    	producer.bind(add);
    
    		
		
    }

}
