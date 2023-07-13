package V2I;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author zeinab 
 * Traffic monitoring application on the edge computing testbed SMOTEC
 * This program implements a scenario with several EdgeAgents running on Edge Nodes, 
 * VehicleAgent and ServiceDistributor running on Orchestrator (K3s master node) node.
 * It firstly creates the edge infrastructure via asking K3s for deploying one EdgeAgent container image on every Edge Node
 * and secondly deploys Service Distributor and VehicleAgent containers on Orchestrator.
 * After deploying the mentioned containers, they start talking to each other and generating some logs.
 * The program downloads the logs and based on them calls K3s API for deploying/releasing traffic monitoring services for VehicleAgents. 
 * Traffic Monitoring service: keeps track of number of existing vehicles in the coverage range of its host node.
 * Users can change the configuration file TestbedConfig.json which is available in ~/Documents/Testbed/Conf directory.
 * Note that for the services we use NodePorts. Hence, the permissioned port range is [30000-32767]
 * For more information refer to the Github repo at: https://github.com/DISC-Systems-Lab/SMOTEC
 * 
 *
 */
public class Example_scenario1 {

	private static List<MobileDevice> vehicleagents = new ArrayList<MobileDevice>();
	private static List<EdgeNode> edgeDevices = new ArrayList<>();
	private static ArrayList<Load> experiment = new ArrayList<Load>();
	private static Map<Integer, Integer> vehagentid = new HashMap<>();//saves the mapping from vehicleagent ids to vehicleagent indices in the list of vehicleagents; 10001 -> 1
	private static HashSet<String> deployed = new HashSet<String>(); // records the traffic monitoring services deployed on EdgeAgents/hosts for VehicleAgents 
	private static HashSet<String> released = new HashSet<String>(); // records the traffic monitoring services deployed on EdgeAgents/hosts for VehicleAgents 															

	public static void main(String[] args) throws IOException, InterruptedException {
		
		System.out.println("SMOTEC started!");
		System.out.println("************************************************************");
		System.out.println("Stage1: preparation...");
		int run = 0;
		long startTime = System.currentTimeMillis();
		long elapsedTime ; 
		
		Constants.initialize();
		if(args.length == 0)
			Constants.cityconfigfile = Constants.filePath + "/Conf/TestbedConfig.json";//SMOTEC configuration file
		else
			Constants.cityconfigfile = args[0];//SMOTEC configuration file
		
		Utility.create_configmap();
		
		int[] agids = TestMap.initializeCity(Constants.cityconfigfile, vehagentid, edgeDevices);

		System.out.println("------------------------------------------------------------");
		
		Utility.releaseResources();//check and remove leftovers from previous runs
		
		System.out.println("------------------------------------------------------------");

		
		//reads edge nodes characteristics from input config file
		create_infrastructure(Constants.numEdgeNodes);
		System.out.println("Number of EdgeAgent containers created: " + edgeDevices.size());
		System.out.println("------------------------------------------------------------");

		create_vehicleagents(agids);
				
		System.out.println("Number of Vehicle Agent containers created: " + vehicleagents.size());
		System.out.println("------------------------------------------------------------");

		Thread.sleep(10000);//sleep until the containers are up and running
		
		long baseTime = System.currentTimeMillis()/1000;
		long newTime;
		
		System.out.println("************************************************************");
		
		System.out.println("Stage2: Execution...");
		
		//download log files of containers and deploy/release services
		while (true) {
			
			elapsedTime = System.currentTimeMillis() - startTime;
			newTime = System.currentTimeMillis()/1000-baseTime;
			System.out.println("Run: "+run+", Time: "+newTime);
			
			Load l = new Load(newTime, edgeDevices);
			experiment.add(run, l);
			
	        if (elapsedTime > Constants.ExperimentTime) {
	        	System.out.println("************************************************************");
	    		System.out.println("End of experiment!");
	        	break;
	        }

			if (run == 0) {
				Utility.cleanup();
			}
			 	
			Utility.downloadRelDep();
			
			Thread.sleep(6000);
			
			Utility.DeployRelease(run, experiment, edgeDevices, deployed, released, vehicleagents, vehagentid);
		
		
		run ++;
		
		}
		
		Utility.writeLoad(experiment, edgeDevices);
		Utility.releaseResources();
		
	}

	/**
	 * @param numEdgeNodes
	 * creates configmap and edge infrastructure using config file
	 */
	private static void create_infrastructure(int numEdgeNodes) {
		int i;
		System.out.println("Service distributor and configmap deployment");
		Utility.creatSDYaml();
		
		try {
			//runs SrvDisBash to deploy servicedistributor container and configmap using K3s
			String ShCommand = "bash " + Constants.SrvDisBash + Constants.filePath;
			
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
		System.out.println("------------------------------------------------------------");
		System.out.println("EdgeAgents container creation...");
		java.util.Map<String, String> env = new HashMap<String, String>();
		
		for (i = 0; i < numEdgeNodes; i++) {
			Utility.creatEdgYaml(i, edgeDevices.get(i).label);
			
			try {
				    // run deployment files to deploy edgeagent containers using K3s
					String ShCommand = "bash " + Constants.edgeBash + edgeDevices.get(i).myId + " " + Constants.filePath;
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

		}//end for

	}

	/**
	 * @param numOFAgents number of agents/vehicles
	 * @param agid
	 * @throws IOException
	 * @throws InterruptedException 
	 * creates deployment file for every agent and then call K3s for deploying vehicleagents on master node
	 *
	 */
	private static void create_vehicleagents(int[] agid) throws IOException, InterruptedException {
		
		System.out.println("Agents container creation...");
		
		java.util.Map<String, String> env = new HashMap<String, String>();

		for (int i = 0; i < Constants.numVehicleAgents; i++) {
			MobileDevice u = new MobileDevice("agent" + agid[i], agid[i]);
			vehicleagents.add(u);
		}
		TestMap.getAgentDemands(vehicleagents, Constants.cityconfigfile);

		for (int i = 0; i < vehicleagents.size(); i++) {
			
			Utility.creatAgentYaml(vehicleagents.get(i), i);
			try {
				//run deployment files to deploy agents using K3s
				String ShCommand = "bash " + Constants.agentBash + vehicleagents.get(i).getId() + "  " + Constants.filePath;
				
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
			}

		} //end for

	}

}
