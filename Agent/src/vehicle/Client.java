package vehicle;

import java.util.StringTokenizer;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;


/**
 * @author zeinab
 * This class introduces a ZeroMQ client which is responsible for creating/destroying communication links with edge network
 */
public class Client {

	String myId;
	Vehicle v;
	
	Context context ;
	
	Socket socket1 ;
	Socket socket2 ;
	Socket socket3 ;
	Socket socket4 ;
	private int megSendNum = 10;

	public Client(Vehicle v, Context context2) {
		this.myId = v.getName();
		this.v = v;
		context = context2;
		socket1 = context.socket(ZMQ.REQ);
		socket2 = context.socket(ZMQ.REQ);
		socket3 = context.socket(ZMQ.REQ);
		socket4 = context.socket(ZMQ.REQ);
		
	}

	/**
	 * @param address
	 * @param connectionDest
	 * @param reqtype
	 * @return sends connection requests to connectionDest node with the address specified in oreder to connect to the edge network
	 * @throws InterruptedException
	 */
	public int sendRequest(String address, int connectionDest, int reqtype) throws InterruptedException {
		
		if (reqtype == Constants.CONNREQ) {
			int connectedEdge = connectToEdge(makeReq(reqtype, connectionDest), address, connectionDest);
			if (connectedEdge != -1) {
				return serviceHostReq(connectedEdge);
			}
	
			else
				return -1;
		}
		return -1;
		
	}

	/**
	 * @param connectededge the edge node with which the vehicleagent is now connected
	 * waits until receives a host for its traffic monitoring service
	 * @return
	 */
	private int serviceHostReq(int connectededge) {
		
		String clientAdd = "tcp://edge" +connectededge+"-response:"+ (Constants.ResPortTopUp + connectededge);
		Consumer co = new Consumer(context);
		co.open(clientAdd);
		int host = co.waitAndReceive(myId);
	
		return host;
	}

	/**
	 * @param type
	 * @param dest
	 * makes messages to send to dest based on the request type
	 * @return
	 */
	private String makeReq(int type, int dest) {
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

	/**
	 * @param edgeServerAdd
	 * @param msgdest
	 * @param downreq
	 * handles closing communication links with the edge network 
	 */
	public void down(String edgeServerAdd, int msgdest, int downreq) {
		
		System.out.println("Client shutting down gracefully");
		socket4.connect(edgeServerAdd);
        //socket4.setReceiveTimeOut(2000);
		String replyValue;

		for (int up_nbr = 0; up_nbr < megSendNum; up_nbr++) {
			  
			  socket4.send(makeReq(downreq, msgdest).getBytes(), 0);
			  System.out.println("Closing connections with edge network, request sent...");
			  byte[] reply = socket4.recv();
			  System.out.println("Close response received...");
			  if (reply != null) {
				replyValue = new String(reply);
				System.out.println("Received reply " + up_nbr + " [" + replyValue + "]");
				StringTokenizer str = new StringTokenizer(replyValue, ":");
				str.nextToken();// down
				str.nextToken();// EdgeAgent
				if (str.nextToken().compareTo(myId) == 0) {// VehicleAgent
					System.out.println("Received ack: " + " [" + replyValue + "]");
					break;
				}
			/*try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			*/
			}
		}
		Constants.numMsg += megSendNum;

	}

	/**
	 * @param add1
	 * @param add2
	 * @param newEdgeAgent candidate EdgeAgent to connect to: the access point in the coverage range
	 * @param oldEdgeAgent current connected EdgeEAgent
	 * @param type
	 * @return the selected host id for the vehicleagent service
	 * @throws InterruptedException
	 */
	public int handover(String add1, String add2, int newEdgeAgent, int oldEdgeAgent, int type) throws InterruptedException {
		
		System.out.println("Request for handover");
		boolean flag = false;
		socket2.connect(add2);
        socket2.setReceiveTimeOut(2000);
        socket2.setSendTimeOut(2000);
		String replyValue;
		int responder;
		
		String myReq = makeReq(Constants.RECOREQ, newEdgeAgent);
		
		//sends connection request to the next connected EdgeAgent
		for (int request_nbr = 0; request_nbr < 3; request_nbr++) {
			System.out.println("hii3");
			socket2.send((myReq).getBytes(), 0);
			Constants.numMsg ++;
			byte[] reply = socket2.recv();
			
			//type+":"+myId+":"+vehAgent;
			if (reply != null) {
				replyValue = new String(reply);
				System.out.println("Reco Received reply " + request_nbr + " [" + replyValue + "]");
				StringTokenizer str = new StringTokenizer(replyValue, ":");
				str.nextToken();// reco
				responder = Integer.parseInt(str.nextToken());// edgeAgent
				
				if ((str.nextToken().compareTo(myId) == 0)&&(newEdgeAgent == responder)) {// vehAgent
					System.out.println("Received ack match: " + " [" + replyValue + "]");
					break;
				}

			}

		
		}
		//sends disconnect request to the current connected EdgeAgent 
		myReq = makeReq(Constants.DISCREQ, oldEdgeAgent);
		socket3.connect(add1);
		for (int request_nbr = 0; request_nbr < 3; request_nbr++) {
			socket3.send(myReq.getBytes(), 0);
			byte[] reply = socket3.recv();
			Constants.numMsg ++;
			
			if (reply != null) {
				replyValue = new String(reply);
				System.out.println("Disc Received reply " + request_nbr + " [" + replyValue + "]");
				StringTokenizer str = new StringTokenizer(replyValue, ":");
				str.nextToken();// disc
				responder = Integer.parseInt(str.nextToken());// edgeAgent
				if ((str.nextToken().compareTo(myId) == 0)&&(v.getConnectedEdge().id  == responder)) {// vehAgent
					System.out.println("Received ack match: " + " [" + replyValue + "]");
				
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
	 * @param message
	 * @param serverAdd
	 * @param connDest
	 * @return
	 * @throws InterruptedException
	 * Send connection message to its closest EdgeAgent connDest
	 * Waits to receive an ack from it
	 */
	private int connectToEdge(String message, String serverAdd, int connDest) throws InterruptedException {
		socket1.connect(serverAdd);
		//socket1.setReceiveTimeOut(1000);
		int edge = -1;
		System.out.println("Start connecting to the EdgeAgent" + serverAdd);
		String replyValue;

		for (int request_nbr = 0; request_nbr < 5; request_nbr++) {
			socket1.send((message).getBytes(), 0);
			Constants.numMsg ++;
			System.out.println("Request to EdgeAgent : [" + serverAdd + "]");
			
			//Thread.sleep(2000);
			byte[] reply = socket1.recv();
			//System.out.println("Reply from EdgeAgent : [" + serverAdd + "]");
			
			if (reply != null) {
				replyValue = new String(reply);
				System.out.println("Received reply from EdgeAgent : [" + replyValue + "]");
				StringTokenizer str = new StringTokenizer(replyValue, ":");
				str.nextToken();// conn
				String testToken = str.nextToken();// edgeAgent
				if ((str.nextToken().compareTo(myId) == 0)&&(testToken.compareTo(connDest+"") == 0)) {// vehAgent
					edge = Integer.parseInt(testToken);// edgeAgent
					System.out.println("Received ack: " + " [" + replyValue + "], waiting for receiving selected host from EdgeAgent");
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

	}

}
