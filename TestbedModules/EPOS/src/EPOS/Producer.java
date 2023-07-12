package EPOS;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class Producer {

	Context context;
    Socket producer;
	int megSendNum = 10;
	
    public Producer(Context context2) {
		context = context2;
        producer = context.socket(ZMQ.PUSH);
	}
    
    /**
     * @param map1 array including the index of selected plans
     * sends the selected plans indices back to the EdgeAgents
     */
    public void sendSelectedPlans(int[] map1) {
		String selectPlans = "EPOS!";
		System.out.println("Sending selected plans to EdgeAgents:");
		
		for (int k = 0; k<Constants.numofEdgeAgents; k++) {
			
			System.out.println("	EdgeAgent: "+k+" selected plan index: "+map1[k]);
			selectPlans += k+":"+map1[k]+":";
				  
			}
		send_message(selectPlans);  	  
				
	}
	
    public void send_message(String msg){
		
    	producer.setSendTimeOut(2000);
    		int update_nbr; 
    		for (update_nbr = 0; update_nbr <megSendNum ; update_nbr ++) {
    			producer.send(msg.getBytes());
    		}
    		System.out.println("Message sent to EdgeAgents: \""+ msg+"\"");
    		Constants.numMsg += megSendNum;
    		
	}
    
    public void close() {
    	producer.close (); 
		context.term ();
	}
    public void open() {
    	producer.bind(Constants.SRVDISPRO); 
		
    }

}
