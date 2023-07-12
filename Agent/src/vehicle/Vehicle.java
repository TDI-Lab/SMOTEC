package vehicle;

import java.util.ArrayList;



/**
 * @author zeinab
 * This class offers an abstraction of a mobile node with its computation resources, mobility, and connected edge nodes
 */
public class Vehicle {
		private int id;
		private String name;
		private int CPU;
		private int Memory;
		private int Storage;

		private int direction; 
		private int speed; 
		protected Mobility mob;

		protected ArrayList<String[]> path;
		protected int startTravelTime;
		protected int travelTimeId;

		private EdgeNode connectedEdge;
		private EdgeNode hostEdge;
		
		private boolean status;
		private double containerSize;
		
		
		public Vehicle(String args1) {
			setId(Integer.parseInt(args1));
			setName(args1);
			CPU = Constants.cpu;
			Memory = Constants.mem;
			Storage = Constants.storage;
			
			setPath(new ArrayList<String[]>());
			
			this.mob = new Mobility();
			setStatus(true);
			
			}
		
		@Override
		public String toString() {
			return this.getName() + "[coordX=" + this.getCoord().getCoordX() + ", coordY="
				+ this.getCoord().getCoordY() + ", direction=" + direction + ", speed=" + speed
				+ ", connectedEdgeNode=" + connectedEdge.id +", serviceHostNode=" + hostEdge.id+"]";
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		public int getDirection() {
			return direction;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}

		public int getSpeed() {
			return speed;
		}

		public void setSpeed(int speed) {
			this.speed = speed;
		}
		public ArrayList<String[]> getPath() {
			return path;
		}

		public void setPath(ArrayList<String[]> path) {
			this.path = path;
		}
		public int getStartTravelTime() {
			return startTravelTime;
		}

		public void setStartTravelTime(int startTravelTime) {
			this.startTravelTime = startTravelTime;
		}

		public int getTravelTime() {
			return travelTimeId;
		}

		public void setTravelTime(int travelTimeId) {
			this.travelTimeId = travelTimeId;
		}

		public Mobility getCoord() {
			return mob;
		}

		public EdgeNode getHostNode() {
			return hostEdge;
		}

		public void setHostNode(EdgeNode host) {
			this.hostEdge = host;
			System.out.println("VehicleAgent service "+name+" hosted on Edge Server: "+host.getMyId());
			}

		
		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		

		public void setCoord(int coordX, int coordY) {
			this.mob.setCoordX(coordX);
			this.mob.setCoordY(coordY);
		}

		public void setId(int id) {
			this.id = id;
		}
		
		public int getMyId() {
			return this.id;
		}
		public EdgeNode getHostServerCloudlet() {
			return hostEdge;
		}

		public void setHostServerCloudlet(EdgeNode hostServerCloudlet) {
			this.hostEdge = hostServerCloudlet;
		}

		public double getContainerSize() {
			return containerSize;
		}

		public void setContainerSize(double containerSize) {
			this.containerSize = containerSize;
		}

		
		public void setCoord(Mobility coord) {
			this.mob = coord;
		}

		public int getCPU() {
			return CPU;
		}

		public int getMemory() {
			return Memory;
		}
		public int getStorage() {
			return Storage;
		}
		public EdgeNode getConnectedEdge() {
			return connectedEdge;
		}
		public void setConnectedEdge(EdgeNode connectedEdge2) {
			this.connectedEdge = connectedEdge2;
		}


	}