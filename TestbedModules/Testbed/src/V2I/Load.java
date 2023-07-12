package V2I;

import java.util.List;

/**
 * records the load of edge nodes during experimentation time 
 */
public class Load {
	long time;
	public int [] cpuload;
	public int [] storload;
	public int [] memload;
	
	public Load(long t, List<EdgeNode> edgeDevices) {
		
		time = t;
		cpuload = new int [Constants.numEdgeNodes];
		storload = new int [Constants.numEdgeNodes];
		memload = new int [Constants.numEdgeNodes];
		
		for (int i = 0 ; i <Constants.numEdgeNodes; i++) {
			cpuload[i] = edgeDevices.get(i).cload;
			storload[i] = edgeDevices.get(i).mload;
			memload[i] = edgeDevices.get(i).sload;
		}
	}
	
	/**
	 * @param edgeNode
	 * @param vehicle
	 * increase the load of the edgeNode when vehicle agent is deployed on it
	 */
	public void addLoad(EdgeNode edgeNode, MobileDevice vehicle) {
		cpuload[edgeNode.myId] = edgeNode.cload + vehicle.getCpu();
		storload[edgeNode.myId] = edgeNode.mload + vehicle.getStorage();
		memload[edgeNode.myId] = edgeNode.sload + vehicle.getMemory();
		edgeNode.cload += vehicle.getCpu();
		edgeNode.mload += vehicle.getStorage();
		edgeNode.sload += vehicle.getMemory();
				
	}
	/**
	 * @param edgeNode
	 * @param vehicle
	 * decrease the load of edgeNode when vehicle agent is deployed on it
	 */
	public void deducLoad(EdgeNode edgeNode, MobileDevice vehicle) {
		cpuload[edgeNode.myId] = edgeNode.cload - vehicle.getCpu();
		storload[edgeNode.myId] = edgeNode.mload - vehicle.getStorage();
		memload[edgeNode.myId] = edgeNode.sload - vehicle.getMemory();
		edgeNode.cload -= vehicle.getCpu();
		edgeNode.mload -= vehicle.getStorage();
		edgeNode.sload -= vehicle.getMemory();
	}
}
