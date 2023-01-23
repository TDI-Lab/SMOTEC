package V2I;


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
		private EdgeNode sourceServerCloudlet;
		private EdgeNode hostServerCloudlet;
		protected ArrayList<String[]> path;
		
		private double containerSize;
		
		protected int startTravelTime;
		protected int travelTimeId;
		private boolean status;
		private int cpu;
		private int memory;
		
		public int getCpu() {
			return cpu;
		}

		public void setCpu(int cpu) {
			this.cpu = cpu;
		}

		public int getMemory() {
			return memory;
		}

		public void setMemory(int memory) {
			this.memory = memory;
		}

		public User(String name, int agid) {
			setId(agid);
			setName(name);
			setPath(new ArrayList<String[]>());
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
		
		public EdgeNode getSourceServerCloudlet() {
			return sourceServerCloudlet;
		}

		public void setSourceServerCloudlet(EdgeNode sourceServerCloudlet) {
			this.sourceServerCloudlet = sourceServerCloudlet;
		}

		
		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		public void setId(int id) {
			this.id = id;
		}
		
		public int getId() {
			// TODO Auto-generated method stub
			return id;
		}
		public EdgeNode getHostServerCloudlet() {
			return hostServerCloudlet;
		}

		public void setHostServerCloudlet(EdgeNode hostServerCloudlet) {
			this.hostServerCloudlet = hostServerCloudlet;
		}

		
		public double getContainerSize() {
			return containerSize;
		}

		public void setContainerSize(double containerSize) {
			this.containerSize = containerSize;
		}

		
		

		


	}