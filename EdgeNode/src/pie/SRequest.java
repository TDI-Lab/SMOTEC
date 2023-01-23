package pie;

import java.util.ArrayList;
import java.util.List;

public class SRequest {
	int time;
	List<Request> requests = new ArrayList<>();
	
	
	public SRequest(int time) {
		super();
		this.time = time;
	}


	public List<Request> getRequests() {
		return requests;
	}


	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}
	
	
}
