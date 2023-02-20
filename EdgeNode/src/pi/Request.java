package pi;

public class Request {
	int vehAgent;
	int hostId = -1;
	int travelTime;
	int type;
	//resource demands:
	private int CPU;
	private int Memory;
	private int Storage;
	
	public Request(String agent, String cpu, String mem, String storage, int t, int time, int host) {
		this.vehAgent = Integer.parseInt(agent);
		
		CPU = Integer.parseInt(cpu);
		Memory = Integer.parseInt(mem);
		Storage = Integer.parseInt(storage);
		travelTime = time;
		type = t;
		hostId = host;

	}

	@Override
	public String toString() {
		return "Request [reqId=" + vehAgent + ", CPU=" + CPU + ", Memory=" + Memory + ", Storage=" + Storage + "] is created";
	}

	public int getHostId() {
		return hostId;
	}


	public void setHostId(int hostId) {
		this.hostId = hostId;
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

	public int getTravelTime() {
		return travelTime;
	}


	public String getReqIdStr() {
		// TODO Auto-generated method stub
		return vehAgent+"";
	}



	
}
