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
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class EdgeNode {

		public String name;
		protected List<String> activeApplications;
		
		protected double uplinkBandwidth;
		protected double downlinkBandwidth;
		protected double uplinkLatency;
		protected double energyConsumption;
		protected double ratePerMips;
		protected double totalCost;

		
		protected Set<User> connectedSmartThings;
		protected Set<User> smartThingsWithVm;
		protected boolean available;
		
		protected int myId;

		public EdgeNode(int id, String name) {
			this.setMyId(id);
			this.setName(name);
			
		}
		private void setName(String name2) {
			// TODO Auto-generated method stub
			name = name2;
		}
		public EdgeNode(String name, double storage, double schedulingInterval, double uplinkBandwidth, double downlinkBandwidth,
			double uplinkLatency, double ratePerMips, int coordX, int coordY, int id)
		{

			this.setMyId(id);
			smartThingsWithVm = new HashSet<>();
			this.setAvailable(true);
			setUplinkBandwidth(uplinkBandwidth);
			setDownlinkBandwidth(downlinkBandwidth);
			setUplinkLatency(uplinkLatency);
			setRatePerMips(ratePerMips);
			
			setActiveApplications(new ArrayList<String>());
			
			this.energyConsumption = 0;
			setTotalCost(0);
			String arch = Constants.FOG_DEVICE_ARCH;
			String os = Constants.FOG_DEVICE_OS;
			String vmm = Constants.FOG_DEVICE_VMM;
			double time_zone = Constants.FOG_DEVICE_TIMEZONE;
			double cost = Constants.FOG_DEVICE_COST;
			double costPerMem = Constants.FOG_DEVICE_COST_PER_MEMORY;
			double costPerStorage = Constants.FOG_DEVICE_COST_PER_STORAGE;
			double costPerBw = Constants.FOG_DEVICE_COST_PER_BW;

			
		}

		
		
		public int getMyId() {
			return myId;
		}

		public void setMyId(int myId) {
			this.myId = myId;
		}

		
		public boolean isAvailable() {
			return available;
		}

		public void setAvailable(boolean available) {
			this.available = available;
		}

		public Set<User> getConnectedSmartThings() {
			return connectedSmartThings;
		}

		public void setConnectedSmartThings(User st, int action) {
			if (action == Constants.ADD) {
				this.connectedSmartThings.add(st);
			}
			else {
				this.connectedSmartThings.remove(st);
			}
		}

				public double getUplinkBandwidth() {
			return uplinkBandwidth;
		}

		public void setUplinkBandwidth(double uplinkBandwidth) {
			this.uplinkBandwidth = uplinkBandwidth;
		}

		public double getUplinkLatency() {
			return uplinkLatency;
		}

		public void setUplinkLatency(double uplinkLatency) {
			this.uplinkLatency = uplinkLatency;
		}



		public List<String> getActiveApplications() {
			return activeApplications;
		}

		public void setActiveApplications(List<String> activeApplications) {
			this.activeApplications = activeApplications;
		}

		
		public double getDownlinkBandwidth() {
			return downlinkBandwidth;
		}

		public void setDownlinkBandwidth(double downlinkBandwidth) {
			this.downlinkBandwidth = downlinkBandwidth;
		}

		
		public double getEnergyConsumption() {
			return energyConsumption;
		}

		public void setEnergyConsumption(double energyConsumption) {
			this.energyConsumption = energyConsumption;
		}

		public double getRatePerMips() {
			return ratePerMips;
		}

		public void setRatePerMips(double ratePerMips) {
			this.ratePerMips = ratePerMips;
		}

		public double getTotalCost() {
			return totalCost;
		}

		public void setTotalCost(double totalCost) {
			this.totalCost = totalCost;
		}

		
		
	}
