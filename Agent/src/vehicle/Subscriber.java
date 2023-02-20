package vehicle;

import org.zeromq.ZMQ;
import org.zeromq.ZThread;


public class Subscriber implements ZThread.IDetachedRunnable {
	private String connection;
	private ZMQ.Context context;
	private ZMQ.Socket subscriber;
	private int host;


	public Subscriber(String connection, int hostEdgeAgent) {
		this.connection = connection;
		this.host = hostEdgeAgent;
	}


	@Override
	public void run(Object[] args) {
		open();
		System.out.println("Waiting for updates from Collector: "+connection);
		subscriber.setReceiveTimeOut(2000);
		//ZMQ.Poller poller = new ZMQ.Poller(1);
		//poller.register(subscriber, ZMQ.Poller.POLLIN);
		while (!Thread.currentThread().isInterrupted()) {
			//poller.poll(100);
			//if (poller.pollin(0)) {
				String content = subscriber.recvStr();
				System.out.println("Update received from Collector: "+content);//where is the complete msg???
			//}
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		close();
	}


	private void open() {
		context = ZMQ.context(1);
		subscriber = context.socket(ZMQ.SUB);
		subscriber.connect(connection);
		//subscriber.subscribe(("").getBytes());
		subscriber.subscribe(("traffic:"+host).getBytes());
		System.out.println("VehicleAgent subscribed to topic: "+"traffic:"+host);
		
	}


	void close() {
		subscriber.close();
		context.close();
	}

}
