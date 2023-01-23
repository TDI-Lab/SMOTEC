package pie;

public class Request {
	int reqId;
	int hostId;
	int travelTime;
	
	private int CPU;
	private int Memory;
	private int Storage;
	
	public Request(String agentId, String cpu, String mem, String storage, int time) {
		this.reqId = Integer.parseInt(agentId);
		this.hostId = -1;
		
			CPU = Integer.parseInt(cpu);
			Memory = Integer.parseInt(mem);
			Storage = Integer.parseInt(storage);
			travelTime = time;
			
			toString();
	}
		
	
	@Override
	public String toString() {
		return "Request [reqId=" + reqId + ", CPU=" + CPU + ", Memory=" + Memory + ", Storage=" + Storage + "] is created";
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



	
}
