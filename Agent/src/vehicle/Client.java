package vehicle;

import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class Client {

	String myId;
	Vehicle v;
	
	Context context ;
	Socket socket1 ;
	Socket socket2 ;
	Socket socket3 ;
	Socket socket4 ;

	public Client(Vehicle v, Context context2) {
		this.myId = v.getName();
		this.v = v;
		context = context2;
		socket1 = context.socket(ZMQ.REQ);
		socket2 = context.socket(ZMQ.REQ);
		socket3 = context.socket(ZMQ.REQ);
		socket4 = context.socket(ZMQ.REQ);
		
	}

	public int sendRequest(String add, int connDest, int t) {
		
		if (t == Constants.CONNREQ) {
			int connectedEdge = connectToEdge(makeReq(t, connDest), add, connDest);
			if (connectedEdge != -1)
				return host(connectedEdge);
			// close both
			else
				return -1;
		}
		return -1;
		
	}

	private int host(int connectededge) {
		String clientAdd = "tcp://localhost:" + (5800 + connectededge);// myId add:port

		Consumer co = new Consumer();
		co.open(clientAdd);
		int host = co.waitAndReceive(myId);
		co.close();

		return host;
	}

	private String makeReq(int type, int dest) {
		// TODO Auto-generated method stub
		if (type == Constants.CONNREQ)
			return "conn:" + dest + ":" + v.getName() + ":" + v.getCPU() + ":" + v.getMemory() + ":" + v.getStorage() + ":"+ v.travelTimeId;
		else if (type == Constants.RECOREQ)
			return "reco:" + dest + ":"+ v.getName() + ":" + v.getCPU() + ":" + v.getMemory() + ":"
				+ v.getStorage() + ":" + v.travelTimeId + ":" + v.getHostNode().id;
		else if (type == Constants.DISCREQ)
			return "disc:" + dest + ":"+ v.getName() + ":" + v.getCPU() + ":" + v.getMemory()
				+ ":" + v.getStorage() + ":" + v.travelTimeId + ":" + v.getHostNode().id;
		else if (type == Constants.DOWNREQ)
			return "down:" + dest + ":"+ v.getName() + ":" + v.getCPU() + ":" + v.getMemory()
			+ ":" + v.getStorage() + ":" + v.travelTimeId + ":" + v.getHostNode().id;
		return null;

	}

	public int down(String edgeServerAdd, int msgdest, int downreq) {
		System.out.println("Client shutting down gracefully");
		socket4.connect(edgeServerAdd);
        socket4.setReceiveTimeOut(2000);
		String replyValue;

		for (int request_nbr = 0; request_nbr < 3; request_nbr++) {
			socket4.send(makeReq(downreq, msgdest).getBytes(), 0);
			byte[] reply = socket4.recv();
			 //type+":"+myId+":"+vehAgent;
			if (reply != null) {
				replyValue = new String(reply);
				System.out.println("Received reply " + request_nbr + " [" + replyValue + "]");
				StringTokenizer str = new StringTokenizer(replyValue, ":");
				str.nextToken();// down
				str.nextToken();// edgeAgent
				if (str.nextToken().compareTo(myId) == 0) {// vehAgent
					System.out.println("Received ack: " + " [" + replyValue + "]");
					break;
				}

			}

		}
		return 0;

	}

	public int handover(String add1, String add2, int newEdgeAgent, int oldEdgeAgent, int type) throws InterruptedException {
		// type == Constants.RECOREQ
		/*
		 * public void stop() { socket.close(); context.term(); }?????
		 */
	

		System.out.println("Client launch and doing handover");
		boolean flag = false;
		socket2.connect(add2);
        socket2.setReceiveTimeOut(2000);
        socket2.setSendTimeOut(2000);
		String replyValue;
		int responder;
		System.out.println("hiiii "+makeReq(Constants.RECOREQ, newEdgeAgent)+" "+add2+" "+makeReq(Constants.DISCREQ, oldEdgeAgent)+" "+add1);
		//Thread.sleep(2000);
		String myReq = makeReq(Constants.RECOREQ, newEdgeAgent);
		for (int request_nbr = 0; request_nbr < 3; request_nbr++) {
			socket2.send(myReq.getBytes(), 0);
			byte[] reply = socket2.recv();
			 //type+":"+myId+":"+vehAgent;
			if (reply != null) {
				replyValue = new String(reply);
				System.out.println("Reco Received reply " + request_nbr + " [" + replyValue + "]");
				StringTokenizer str = new StringTokenizer(replyValue, ":");
				str.nextToken();// reco
				// edgeAgent
				responder = Integer.parseInt(str.nextToken());
				if ((str.nextToken().compareTo(myId) == 0)&&(newEdgeAgent == responder)) {// vehAgent
					System.out.println("Received ack: " + " [" + replyValue + "]");
					break;
				}

			}

		}

		myReq = makeReq(Constants.DISCREQ, oldEdgeAgent);
		socket3.connect(add1);
		for (int request_nbr = 0; request_nbr < 3; request_nbr++) {
			socket3.send(myReq.getBytes(), 0);
			byte[] reply = socket3.recv();

			if (reply != null) {
				replyValue = new String(reply);
				System.out.println("Disc Received reply " + request_nbr + " [" + replyValue + "]");
				StringTokenizer str = new StringTokenizer(replyValue, ":");
				str.nextToken();// disc
				responder = Integer.parseInt(str.nextToken());// edgeAgent
				if ((str.nextToken().compareTo(myId) == 0)&&(v.getConnectedEdge().id  == responder)) {// vehAgent
					System.out.println("Received ack: " + " [" + replyValue + "], handover done!");
					flag = true;
					break;
				}

			}

		}
		if (flag == true) 
			return newEdgeAgent;
		else 
			return -1;
	

	}

	/**
	 * @param serverAdd 
	 * @param connDest 
	 * @param newEdge  candidate ap to connect to: the access point in the coverage
	 *                 range
	 * @param veh      vehicle
	 * @param handover
	 * @return selected host index for the vehicle service the method sends
	 *         connection request to ap, waits for ack and a host id from ap
	 * @throws Exception
	 */
	/*
	 * private static int Handover(int newEdge, Vehicle veh) throws Exception {
	 * System.out.println(
	 * "------------------------------------------------------------");
	 * 
	 * subscribe for the message with the topic ap:agent as the ack
	 * 
	 * String ReqToAck = "reco-"+newEdge+"-"+veh.getName();
	 * 
	 * wait on ack message reception
	 * 
	 * CountDownLatch receivedSignal = new CountDownLatch(1);
	 * mqttClient.subscribe(ReqToAck, (topic, msg) -> {
	 * System.out.println("reconnect request ack received: topic="+ ReqToAck+
	 * " content=" +new String(msg.getPayload())); receivedSignal.countDown(); });
	 * 
	 * 
	 * publish connection request messages: TOPIC = "reconnect-"+edgeId; content =
	 * u.getName()+":"+u.getCPU()+":"+u.getMemory()+":"+u.getStorage()+":"+u.
	 * travelTimeId;
	 * 
	 * Callable<Void> target = new MqttPublisher(mqttClient, newEdge, veh, true);
	 * ExecutorService executor = Executors.newSingleThreadExecutor(); try {
	 * target.call(); } catch(Exception ex) { throw new RuntimeException(ex); }
	 * 
	 * target = new MqttPublisher(mqttClient, newEdge, veh, false); executor =
	 * Executors.newSingleThreadExecutor(); try { target.call(); } catch(Exception
	 * ex) { throw new RuntimeException(ex); }
	 * 
	 * 
	 * receivedSignal.await(30,TimeUnit.SECONDS);//await();// executor.shutdown();
	 * 
	 * if connected with the ap then wait for the selected host from the ap
	 * 
	 * if (receivedSignal.getCount() == 0) {
	 * 
	 * System.out.println("VehAgent "+veh.getName()+" received an ack from "
	 * +newEdge+" and handover is done."); return newEdge; }
	 * 
	 * else { return -1; } }
	 */

	private int connectToEdge(String message, String serverAdd, int connDest) {
		// TODO Auto-generated method stub
		// "conn-"+edgeagentId+"-"+vAgent.getName()
		socket1.connect(serverAdd);
//	        requester.setReceiveTimeOut(2000);
		int edge = -1;
		System.out.println("launch and connect to server " + serverAdd);
		String replyValue;

		for (int request_nbr = 0; request_nbr < 3; request_nbr++) {
			socket1.send((message).getBytes(), 0);
			byte[] reply = socket1.recv();

			if (reply != null) {
				replyValue = new String(reply);
				System.out.println("Received reply " + request_nbr + ": [" + replyValue + "]");
				StringTokenizer str = new StringTokenizer(replyValue, ":");
				str.nextToken();// conn
				String testToken = str.nextToken();// edgeAgent
				if ((str.nextToken().compareTo(myId) == 0)&&(testToken.compareTo(connDest+"") == 0)) {// vehAgent
					edge = Integer.parseInt(testToken);// edgeAgent
					System.out.println("Received ack: " + " [" + replyValue + "], waiting for host");
					break;

				}

			}

		}

		return edge;

	}

	public void stop() {
		socket1.close();
		socket2.close();
		socket3.close();
		socket4.close();
		
		//context.term();
	}

}
