package EPOS;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class Producer {

	static final int PORT = 5501;
	Context context = ZMQ.context(1);
    Socket producer = context.socket(ZMQ.PUSH);
	
    void send_message(String msg){
    	producer.setSendTimeOut(2000);
    		int update_nbr; 
    		for (update_nbr = 0; update_nbr <3; update_nbr ++) {
    			producer.send(msg.getBytes());
    		}
    		System.out.println("Message sent to EdgeAgents: "+ msg);
            
	}
    
    public void close() {
    	producer.close (); 
		context.term ();
	}
    public void open() {
    	producer.connect(Constants.SRVDISPRO); 
		
    }

}
