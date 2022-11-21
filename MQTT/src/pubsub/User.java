package pubsub;


import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;



public class User {
		private int id;
		private String name;
		

		private int direction; // NONE, NORTH, SOUTH, ...
		private int speed; // in m/s
		protected Coordinate coord;
		protected Coordinate futureCoord;// = new Coordinate();//myiFogSim
		private EdgeNode connectedEdge;
		private EdgeNode hostEdge;
		protected ArrayList<String[]> path;
		
		private Service container;
		private double containerSize;
		
		protected int startTravelTime;
		protected int travelTimeId;
		private boolean status;
		
		public User(String args) {
			setId(Integer.parseInt(args));
			setName(args);
			setPath(new ArrayList<String[]>());
			this.coord = new Coordinate();
			this.futureCoord = new Coordinate();
			setFutureCoord(-1, -1);
			}
		
		

		@Override
		public String toString() {
			return this.getName() + "[coordX=" + this.getCoord().getCoordX() + ", coordY="
				+ this.getCoord().getCoordY() + ", direction=" + direction + ", speed=" + speed
				+ ", sourceCloudletServer=" + connectedEdge +"]";
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

		public int getTravelTimeId() {
			return travelTimeId;
		}

		public void setTravelTimeId(int travelTimeId) {
			this.travelTimeId = travelTimeId;
		}
		public Coordinate getFutureCoord() {// myiFogSim
			return futureCoord;
		}

		public void setFutureCoord(int coordX, int coordY) {
			this.futureCoord.setCoordX(coordX);
			this.futureCoord.setCoordY(coordY);
		}
		public Coordinate getCoord() {
			return coord;
		}

		public EdgeNode getSourceServerCloudlet() {
			return connectedEdge;
		}

		public void setSourceServerCloudlet(EdgeNode sourceServerCloudlet) {
			this.connectedEdge = sourceServerCloudlet;
		}

		
		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		

		public void setCoord(int coordX, int coordY) {
			this.coord.setCoordX(coordX);
			this.coord.setCoordY(coordY);
		}

		public void setId(int id) {
			this.id = id;
		}
		
		public int getMyId() {
			// TODO Auto-generated method stub
			return 0;
		}
		public EdgeNode getHostServerCloudlet() {
			return hostEdge;
		}

		public void setHostServerCloudlet(EdgeNode hostServerCloudlet) {
			this.hostEdge = hostServerCloudlet;
		}

		public Service getContainer() {
			return container;
		}

		public void setContainer(Service container) {
			this.container = container;
		}

		public double getContainerSize() {
			return containerSize;
		}

		public void setContainerSize(double containerSize) {
			this.containerSize = containerSize;
		}

		
		public void setCoord(Coordinate coord) {
			this.coord = coord;
		}

		public void setFutureCoord(Coordinate futureCoord) {
			this.futureCoord = futureCoord;
		}

		public void setHostServerCloudlet(int hid) {
			// TODO Auto-generated method stub
			
			
		}

		


	}