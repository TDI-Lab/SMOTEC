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


		protected List<String> activeApplications;
		EdgeNodeCharacteristics ENCharacteristics;
		protected Map<String, Application> applicationMap;
		
		protected double uplinkBandwidth;
		protected double downlinkBandwidth;
		protected double uplinkLatency;
		protected double energyConsumption;
		protected double ratePerMips;
		protected double totalCost;

		
		protected Coordinate coord;
		protected Set<User> connectedSmartThings;
		protected Set<User> smartThingsWithVm;
		protected boolean available;
		
		protected int myId;

				
		public EdgeNode(String name, double storage,	double schedulingInterval, double uplinkBandwidth, double downlinkBandwidth,
			double uplinkLatency, double ratePerMips, int coordX, int coordY, int id, Service service)
		{

			this.coord = new Coordinate();
			this.setCoord(coordX, coordY);
			this.setMyId(id);
			smartThings = new HashSet<>();
			smartThingsWithVm = new HashSet<>();
			this.setAvailable(true);
			this.setService(service);

			setStorageList(storageList);
			setUplinkBandwidth(uplinkBandwidth);
			setDownlinkBandwidth(downlinkBandwidth);
			setUplinkLatency(uplinkLatency);
			setRatePerMips(ratePerMips);
			
			setActiveApplications(new ArrayList<String>());
			
			applicationMap = new HashMap<String, Application>();
			
			this.energyConsumption = 0;
			this.lastUtilization = 0;
			setTotalCost(0);
			String arch = Constants.FOG_DEVICE_ARCH;
			String os = Constants.FOG_DEVICE_OS;
			String vmm = Constants.FOG_DEVICE_VMM;
			double time_zone = Constants.FOG_DEVICE_TIMEZONE;
			double cost = Constants.FOG_DEVICE_COST;
			double costPerMem = Constants.FOG_DEVICE_COST_PER_MEMORY;
			double costPerStorage = Constants.FOG_DEVICE_COST_PER_STORAGE;
			double costPerBw = Constants.FOG_DEVICE_COST_PER_BW;

			EdgeNodeCharacteristics characteristics = new EdgeNodeCharacteristics(
				arch, os, vmm, host, time_zone, cost, costPerMem, costPerStorage, costPerBw);

			setCharacteristics(characteristics);

		}

		
			

		
		private void setCharacteristics(EdgeNodeCharacteristics characteristics) {
			// TODO Auto-generated method stub
			ENCharacteristics = characteristics;
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

		public Coordinate getCoord() {
			return coord;
		}

		public void setCoord(int coordX, int coordY) {
			this.coord.setCoordX(coordX);
			this.coord.setCoordY(coordY);
		}

		

		private void desconnectServerCloudletSmartThing(SimEvent ev) {
			User smartThing = (User) ev.getData();
			desconnectServerCloudletSmartThing(smartThing);
			
		}

		private void connectServerCloudletSmartThing(SimEvent ev) {
			User smartThing = (User) ev.getData();
			connectServerCloudletSmartThing(smartThing);
			if (smartThing.getTimeFinishDeliveryVm() == -1) {
			}
			else {
				
				}
			if (!smartThing.getSourceServerCloudlet().equals(smartThing.getVmLocalServerCloudlet())) {
				smartThing.getSourceServerCloudlet().desconnectServerCloudletSmartThing(smartThing);
				smartThing.getVmLocalServerCloudlet().connectServerCloudletSmartThing(smartThing);
				
			}
		}

		

		public boolean connectServerCloudletSmartThing(User st) {

			st.setSourceServerCloudlet(this);

			setSmartThings(st, Constants.ADD);
			double latency = st.getUplinkLatency();

			setUplinkLatency(getUplinkLatency() + 0.123812950236);//
			
			return true;
		}

		public boolean desconnectServerCloudletSmartThing(MobileDevice st) {
			setSmartThings(st, Policies.REMOVE); // it'll remove the smartThing from serverCloudlets-smartThing's set
			st.setSourceServerCloudlet(null);
			// NetworkTopology.addLink(this.getId(), st.getId(), 0.0, 0.0);
			setUplinkLatency(getUplinkLatency() - 0.123812950236);
			removeChild(st.getId());
			return true;

		}

		
	
		protected void updateActiveApplications(SimEvent ev) {
			Application app = (Application) ev.getData();
			if (!getActiveApplications().contains(app.getAppId()))
				getActiveApplications().add(app.getAppId());
			System.out.println(" Apps " + getActiveApplications());
		}

		
		protected void updateAllocatedMips(String incomingOperator) {
		
		}

		private void updateEnergyConsumption() {
			double totalMipsAllocated = 0;
			for (final Vm vm : getHost().getVmList()) {
				totalMipsAllocated += getHost().getTotalAllocatedMipsForVm(vm);
			}

			double timeNow = CloudSim.clock();
			double currentEnergyConsumption = getEnergyConsumption();
			double newEnergyConsumption = currentEnergyConsumption + (timeNow - lastUtilizationUpdateTime)
				* getHost().getPowerModel().getPower(lastUtilization);
			setEnergyConsumption(newEnergyConsumption);
			double currentCost = getTotalCost();
			double newcost = currentCost + (timeNow - lastUtilizationUpdateTime) * getRatePerMips()
				* lastUtilization * getHost().getTotalMips();
			setTotalCost(newcost);

			lastUtilization = Math.min(1, totalMipsAllocated / getHost().getTotalMips());
			lastUtilizationUpdateTime = timeNow;
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

		

		public Map<String, Application> getApplicationMap() {
			return applicationMap;
		}

		public void setApplicationMap(Map<String, Application> applicationMap) {
			this.applicationMap = applicationMap;
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

		public Set<EdgeNode> getServerCloudlets() {
			return serverCloudlets;
		}

		public void setServerCloudlets(EdgeNode sc, int action) {// myiFogSim
			if (action == Constants.ADD) {
				this.serverCloudlets.add(sc);
			}
			else {
				this.serverCloudlets.remove(sc);
			}
		}

		public Set<User> getSmartThingsWithVm() {
			return smartThingsWithVm;
		}

		public void setSmartThingsWithVm(User st, int action) {// myiFogSim
			if (action == Constants.ADD) {
				this.smartThingsWithVm.add(st);
			}
			else {
				this.smartThingsWithVm.remove(st);
			}
		}

		
	}
