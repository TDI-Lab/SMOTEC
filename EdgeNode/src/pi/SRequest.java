package pi;

import java.util.ArrayList;
import java.util.List;

public class SRequest {
	int time;
	List<Request> conrequests ;
	List<Request> recrequests ;
	List<Request> disrequests ;
	List<Request> hosted;
	List<Request> down;
	
	public SRequest(int time, int t, Request r) {
		this.time = time;
		conrequests = new ArrayList<>();
		recrequests = new ArrayList<>();
		disrequests = new ArrayList<>();
		hosted = new ArrayList<>();
		down = new ArrayList<>();
		addRequest(r, t);
		
	}


	public void addRequest(Request r, int t) {
		// TODO Auto-generated method stub
		if(t == Constants.CONREQ)
			conrequests.add(r);
		else if (t == Constants.RECREQ) 
			recrequests.add(r);
			
		else if (t == Constants.DISREQ)
			disrequests.add(r);
		else if (t == Constants.HOSREQ)
			hosted.add(r);
		else if (t == Constants.DOWN);
			down.add(r);
	}


	public SRequest(String reqId, String c, String m, String s) {
		// TODO Auto-generated constructor stub
		
		//requests.add(new Request(reqId, c, m, s));
		
	}


	
	
	
}
