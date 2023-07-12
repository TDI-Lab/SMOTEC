package pi;

/**
 * @author zeinab
 * this class defines a request received from a mobile agent
 *
 */
public class Request {
	int vehAgentId;//agentId
	int hostId = -1;
	int travelTime;
	int type;
	
	//resource demands:
	private int CPU;
	private int Memory;
	private int Storage;
	
	public Request(String agent, String cpu, String mem, String storage, int t, int time, int host) {
		this.vehAgentId = Integer.parseInt(agent);
		
		CPU = Integer.parseInt(cpu);
		Memory = Integer.parseInt(mem);
		Storage = Integer.parseInt(storage);
		travelTime = time;
		type = t;
		hostId = host;

	}

	public Request(int vehAgentId2, int cpu2, int memory2, int storage2, int conreq, int travelTime2, int host) {
		
		this.vehAgentId = vehAgentId2;
		
		CPU = cpu2;
		Memory = memory2;
		Storage = storage2;
		travelTime = travelTime2;
		type = conreq;
		hostId = host;
	}

	@Override
	public String toString() {
		return "Request [reqId=" + vehAgentId + ", CPU=" + CPU + ", Memory=" + Memory + ", Storage=" + Storage + "] is created";
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
		return vehAgentId+"";
	}



	
}
