package pi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import experiment.IEPOSExperiment;


public class ResourceMgm {
	List<Request> requests ;
	List<Neighbor> neighbors ;
	HashSet<String> connected;
	List <Integer> hosted;
	public Plan [] Plans;
	Map<Integer, Integer> SrvToHostSet;
	int edgeAgentIndex;
	int planIndex = -1;
	Context context ;
	Producer planProd;
	//host == -1:
	public ResourceMgm(Context context2, String agent, List<Neighbor> neighbors2) {
		edgeAgentIndex = Integer.parseInt(agent);
		context = context2;
		neighbors = neighbors2;
		
	}
			
	public void initialize(List<Request> requests2, HashSet<String> connectedSet, List<Integer> hostedList, Map<Integer, Integer> srvToHostSet) {
		// TODO Auto-generated constructor stub
		requests = requests2;
		connected = connectedSet;
		hosted = hostedList;
		SrvToHostSet = srvToHostSet;
		System.out.println("edgeAgent "+edgeAgentIndex+" starts managing requests...");
		
		
	}
	
	public void manageRequests(List<Request> recorequests, List<Request> disrequests, List<Request> downrequests)  {
		// TODO Auto-generated method stub
		
		if(requests.size() == 0) {
			System.out.println("No request to be managed!");
			return;
		}
		ProDownReq(downrequests);
		ProDisReq(disrequests);
		
		//updateConnections();
		removeReq(Constants.DISREQ);
		removeReq(Constants.DOWN);
		removeReq(Constants.RECREQ);
		
		if(requests.size() == 0) {
			System.out.println("No request for placement!");
			return;
		}
		//handle connection requests:
		planProd = new Producer(context);
		
		String msg = makeMsg(ProConReq());
		planExchang(msg);
		//receiveSelectedPlan();
		//return msg;
		
		System.out.println("0 here");
		allocateResources();
		System.out.println("1 here");
		
		removeReq(Constants.CONREQ);
		System.out.println("Management of requests finished!");
		
		//UpdateRequests();
	}
	
	

	private void ProDisReq(List<Request> disrequests) {
		// TODO Auto-generated method stub
		for (int i = 0;i<disrequests.size(); i++) {
			
			if (disrequests.get(i).type == Constants.DISREQ)
				connected.remove(disrequests.get(i).getReqIdStr());
		}
	}

	
	private void updateConnections() {
		// TODO Auto-generated method stub
		for (int i = 0 ; i<requests.size() ; i++) {  
			if ((requests.get(i).type == Constants.DISREQ)||(requests.get(i).type == Constants.DOWN))
				connected.remove(requests.get(i).getReqIdStr());
		}
	}

	private void removeReq(int type) {
		for (int i = 0 ; i<requests.size() ; i++) {  
			if (requests.get(i).type == type)
				requests.remove(i);
		}	
		System.out.println("request queue is empty now");
		
		
	}

	private Plan[] ProConReq() {
		// TODO Auto-generated method stub
		SrvMapper srvDisrtibutor = new SrvMapper(edgeAgentIndex, requests, neighbors);
		Plans = srvDisrtibutor.generatePlans();
		return Plans;
		
	}

	private int planExchang(String candPlanMsg) {
		
		System.out.println("Sending plans to service distributor");
		
		planProd.open(Constants.SRVDISPRO, true);
		planProd.send_message(candPlanMsg);
		//planProd.close();
	   
		System.out.println("Waiting for response from service distributor");
	    
		Consumer co = new Consumer(context);
	    co.open();
	    planIndex = co.waitAndReceive();
		co.close();
		
		/*
	      * if connected with the ap then wait for the selected host from the ap   
	      */
        if (planIndex != -1) {
        	System.out.println("Selected placement plan index: "+planIndex);
				return planIndex;	
				}
			else {
			System.out.println("Service distributor not responding, DEBUG!");
				
			}
			return -1;
	}

	private String makeMsg(Plan[] myplans) {
		// TODO Auto-generated method stub
		String plans = edgeAgentIndex+"!";
            for(int col =0; col < Constants.EPOS_NUM_PLANS; col++){
            	plans += String.valueOf(myplans[col].getCosts());
                plans += ":";
                int size = myplans[col].utilPlan.length;
                for (int i =0; i<size ; i++){//both of CPU and Mem will be printed
                	plans += String.format("%.12f", myplans[col].utilPlan[i]);//preventing wrong written of values as negative values
                     if (i != size-1){
                    	 plans += ",";
                     }
                 }
                plans += "\r\n";
            } 
    
    return plans;
	}

	
	
	/*
	/**
	 * @param planId
	 * @param selectedPlan
	 * publish messages to the agents who received requests from: message contains the indices of the selected hosts for executing their services
	 */
	private void allocateResources() {
		String selectedHostMSg; 
		//Producer planProd = new Producer(context);
		String connectedEdgeAddr = "tcp://localhost:"+(5800+edgeAgentIndex);
		planProd.open(connectedEdgeAddr, false);
			
		for (int i = 0; i <Plans[planIndex].y.length; i++) {
			
			Migrate(i);			
			
			requests.get(i).hostId = Plans[planIndex].y[i];//set selected host to the agent
	        
			if (Plans[planIndex].y[i] == edgeAgentIndex)
	        	hosted.add(requests.get(i).vehAgent);
			
			SrvToHostSet.put(requests.get(i).vehAgent, requests.get(i).hostId);
			
			//response message to vehicle agent with their hosts: host-agent-hostindex
			selectedHostMSg = "host-"+requests.get(i).vehAgent+":"+Plans[planIndex].y[i];
			planProd.send_message(selectedHostMSg);
		}
		System.out.println("service migration done");
		
			planProd.close();//already done???
		   
		
	}
	
	/**
	 * @param sPlan selected plan by IEPOS
	 * @param i 
	 */
	private void Migrate(int i) {
		System.out.println("Starting the release of resources");
		releaseResources(i);
		System.out.println("Start service deployment");
		deployPlan(i);
		
	}
	
	/**
	 * create deployment .yaml file for every request
	 * run a shell script to ask k3s to deploy the files 
	 * @param i2 
	 */
	private void deployPlan(int i2) {
		
		int selectedHost;
		java.util.Map<String, String> env = new HashMap<String, String>();
		//write deployments for agent's service with resource demands using json:
		if((SrvToHostSet.get(requests.get(i2).vehAgent) == null) ||(SrvToHostSet.get(requests.get(i2).vehAgent) != Plans[planIndex].y[i2])) {
				selectedHost = Plans[planIndex].y[i2];
				Utility.creatYaml(i2, requests.get(i2).vehAgent, requests.get(i2), selectedHost);
					
			
			//System.out.println("deploy "+i2+" "+requests.get(i2).vehAgent+" "+ requests.get(i2).hostId+" "+Plans[planIndex].y[i2]+" "+SrvToHostSet.size()+" "+neighbors.size()+" "+SrvToHostSet.get(requests.get(i2).vehAgent));
			
			
			
			try {
				String ShCommand = "bash "+Constants.srvDeployScript+" "+ Constants.deployDir+" "+requests.get(i2).vehAgent;
			    Process p = Runtime.getRuntime().exec(ShCommand);
			    p.waitFor();
			    
			    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			   // bw.write("2012");
			    
			    String line = "";
			    
			    while ((line = reader.readLine()) != null) {
			        System.out.println(line);
			    }

			    line = "";
			    while ((line = errorReader.readLine()) != null) {
			        System.out.println(line);
			    }
			
			    } catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			neighbors.get(selectedHost).CPU -= requests.get(i2).getCPU();
			neighbors.get(selectedHost).Memory -= requests.get(i2).getMemory(); 
			neighbors.get(selectedHost).Storage -= requests.get(i2).getStorage(); 
			neighbors.get(selectedHost).IncRunningSrv();

			
			}
		
		
	}
	
	private void releaseResources(int i2) {
			//allocate/release resources based on migration
			if(SrvToHostSet.get(requests.get(i2).vehAgent) != null) {
				
					if(SrvToHostSet.get(requests.get(i2).vehAgent) != Plans[planIndex].y[i2]) {//check what happens when its null
					
					//	System.out.println("i "+i2+" "+requests.get(i2).vehAgent+" "+ Plans[planIndex].y[i2]+" "+SrvToHostSet.size()+" "+neighbors.size()+" "+SrvToHostSet.get(requests.get(i2).vehAgent));
					
						// Run a shell command to release resources
					try {
						String ShCommand = "bash "+Constants.srvReleaseScript+ " srv"+requests.get(i2).vehAgent;
					    Process p = Runtime.getRuntime().exec(ShCommand);
					    p.waitFor();
					    
					    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		
					    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
					    
					    String line = "";
					    
					    while ((line = reader.readLine()) != null) {
					        System.out.println(line);
					    }
		
					    line = "";
					    while ((line = errorReader.readLine()) != null) {
					        System.out.println(line);
					    }
					
					    } catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					neighbors.get(SrvToHostSet.get(requests.get(i2).vehAgent)).CPU += requests.get(i2).getCPU();
					neighbors.get(SrvToHostSet.get(requests.get(i2).vehAgent)).Memory += requests.get(i2).getMemory();
					neighbors.get(SrvToHostSet.get(requests.get(i2).vehAgent)).Storage += requests.get(i2).getStorage();
					neighbors.get(Plans[planIndex].y[i2]).DecRunningSrv();
				
					}
					else {
						
					}
				
				
			}
	}

	

	private void ProDownReq(List<Request> downrequests) {
		// TODO Auto-generated method stub
		for (int i = 0;i<downrequests.size(); i++) {
			
				if ((downrequests.get(i).type == Constants.DOWN)&&(downrequests.get(i).getHostId() != -1)){
					try {
						if(SrvToHostSet.get(downrequests.get(i).hostId) != null) {
							neighbors.get(downrequests.get(i).hostId).CPU += downrequests.get(i).getCPU();
							neighbors.get(downrequests.get(i).hostId).Memory += downrequests.get(i).getMemory(); 
							neighbors.get(downrequests.get(i).hostId).Storage += downrequests.get(i).getStorage(); 
							SrvToHostSet.remove(downrequests.get(i).vehAgent);
						}
						if (downrequests.get(i).hostId == edgeAgentIndex)
				        	hosted.remove(downrequests.get(i).vehAgent);
							
							String ShCommand = "bash "+Constants.srvReleaseScript+ " srv"+downrequests.get(i).vehAgent;
						    Process p = Runtime.getRuntime().exec(ShCommand);
						    p.waitFor();
						    
						    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				
						    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
						   // bw.write("2012");
						    
						    String line = "";
						    
						    while ((line = reader.readLine()) != null) {
						        System.out.println(line);
						    }
				
						    line = "";
						    while ((line = errorReader.readLine()) != null) {
						        System.out.println(line);
						    }
						
					    } 
					catch (IOException e) {
							e.printStackTrace();
						} 
					catch (InterruptedException e) {
							e.printStackTrace();
						}
				
					
			}
				connected.remove(downrequests.get(i).getReqIdStr());
					
		}

	}
	}
	