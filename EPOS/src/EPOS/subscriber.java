package EPOS;

import java.util.HashSet;
import java.util.StringTokenizer;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class subscriber {

	
	long messagesReceived = 0;
	int receiveCount = 2;
	Context context = ZMQ.context(1);
	Socket Sub = context.socket(ZMQ.SUB);
	
    void recv(String[] plans, boolean[] flag) {
    	Sub.bind(Constants.SRVDISCON);
    	System.out.println("Distributor waiting for plans!");
    	
    	HashSet<Integer> planSet = new HashSet<Integer>(); 
    	int messageNum = 0; 
    	System.out.println("Distributor waiting");
    	while (messageNum < receiveCount) {
                // Use trim to remove the tailing '0' character
                //messages.add(subscriber.recvStr(0).trim());
                byte[] message = Sub.recv();
                String triggerMsg = new String(message);
                StringTokenizer msg = new StringTokenizer(triggerMsg,"!");
                int index = Integer.parseInt(msg.nextToken());//edgeAgent id
                System.out.println("Received msg "+messageNum+": ["+triggerMsg+"]");
                if (!planSet.contains(index)){ //check if the request is already recorded     
                	System.out.println("Received msg matched");
                    
	                plans[index] = msg.nextToken();
					flag[index] = true;
					planSet.add(index);
					messageNum++;
                }
                
            
        }
        //return messages;

}

void open() {
	Sub.bind(Constants.SRVDISCON);
	System.out.println("Distributor waiting for plans!");
	
}


void close() {
	Sub.close();
	context.close();
}
}
