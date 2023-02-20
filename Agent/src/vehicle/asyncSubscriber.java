package vehicle;
	import org.zeromq.ZMQ;
	import org.zeromq.ZMQ.Context;
	import org.zeromq.ZMQ.Socket;

	import java.util.HashSet;
	import java.util.concurrent.Callable;
	
	public class asyncSubscriber implements Callable<Void> {
		
		private Context context;
		private Socket subscriber;
		private int host;
	
		public asyncSubscriber(Context context2, String connection, int hostEdgeAgent) {
			context = context2;
			this.host = hostEdgeAgent;
			subscriber = context.socket(ZMQ.SUB);
			subscriber.connect(connection);
			
			open();
			
		}
	    
		@Override
		public Void call() throws Exception {
			subscriber.setReceiveTimeOut(2000);
			String content = subscriber.recvStr();
			System.out.println("Update received from Collector: "+content);
	
			    return null;  
		}
		
		
		private void open() {
			//subscriber.subscribe(("").getBytes());
			subscriber.subscribe(("traffic:"+host).getBytes());
			System.out.println("VehicleAgent subscribed to topic: "+"traffic:"+host+" and waiting for updates");
			
		}

		void close() {
			subscriber.close();
			context.close();
		}
	}
