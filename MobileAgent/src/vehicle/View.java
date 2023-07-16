package vehicle;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import java.util.concurrent.Callable;

/**
 * @author zeinab
 * This class is in charge of communicating with traffic monitoring service (collector) and receiving the traffic updates from it.
 */
public class View implements Callable<Void> {
	
		Context context;
		Socket subscriber;
		private int host;
	
		public View(Context context2, String connection, int hostEdgeAgent) {
			context = context2;
			this.host = hostEdgeAgent;
			subscriber = context.socket(ZMQ.SUB);
			subscriber.connect(connection);
			System.out.println("View connected to Collector!");
			open();
			
		}
	    
		/**
		 * receives messages from collector
		 */
		@Override
		public Void call() throws Exception {
			//subscriber.setReceiveTimeOut(1000);
			String content = subscriber.recvStr();
			System.out.println("Message received from Collector (TrafficMonitor): "+content);
	
			return null;  
		}
		
		/**
		 * subscribes to the collector traffic updates
		 */
		private void open() {
			subscriber.subscribe(("traffic:"+host).getBytes());
			System.out.println("Subscribed to the topic: "+"\"traffic:"+host+"\"");
			
		}

		void close() {
			subscriber.close();
			context.close();
		}
	}
