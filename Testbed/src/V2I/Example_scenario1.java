package V2I;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * @author spring
 * in this scenario edge nodes are going to count the number of connected agents (traffic monitoring)
 *  and number of hosted services (IoT service monitoring) on themselves and give this info back to the connected agents
 *
 */
public class Example_scenario1 {

	private static List<User> agents = new ArrayList<User>();
	private static List<EdgeNode> edgeDevices = new ArrayList<>();
	private static String mobFileDir;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Constants.filePath = new File("").getAbsolutePath();
		Constants.cityconfigfile = Constants.filePath + "/Conf/TestbedConfig.json";//put the config file in /usr/testbed/conf. /tmp/src/CityConfig.json
		Constants.yamlPath = Constants.filePath+"/src/deployments/deployment";
		Constants.agentBash = Constants.filePath+"/src/agent.sh ";
		Constants.edgeBash = Constants.filePath+"/src/edge.sh ";
		System.out.println(Constants.filePath+"  "+Constants.agentBash+"  "+Constants.edgeBash+"  "+Constants.yamlPath);
		
				
		int [] agid = Map.initializeCity(Constants.cityconfigfile);//address of input file
	    	
		System.out.println("Constants.numAgents: "+Constants.numAgents);
		//read edge nodes characteristics from input json file
		create_infrastructure(Constants.numEdgeNodes);
		System.out.println("Number of Edge node containers created: "+edgeDevices.size());
		System.out.println("------------------------------------------------------------");
		
		create_agents(Constants.numAgents, agid);
		System.out.println("Number of Agent containers created: "+agents.size());
		System.out.println("------------------------------------------------------------");
		//visualize results
		
		while(true) {
			
		}
		
	}

	private static void create_infrastructure(int numEdgeNodes) {
		System.out.println("Edge nodes container creation...");
		System.out.println("--------------------------------");
		java.util.Map<String, String> env = new HashMap<String, String>();
		int i;
		for (i = 0 ; i<numEdgeNodes ; i++) { 
			EdgeNode e = new EdgeNode(i, "Edge"+i);
			edgeDevices.add(e); 
			
		}
		
		for (i = 0 ; i<numEdgeNodes ; i++) { 
			Utility.creatEdgYaml(i);
		
		
			try {
				String ShCommand = "bash "+Constants.edgeBash+ edgeDevices.get(i).myId+" "+Constants.filePath+" "+Constants.ContainerImagesPath;
				//String ShCommand = "bash sudo "+Constants.edgeBash+ edgeDevices.get(i).myId+" "+Constants.filePath+" "+Constants.ContainerImagesPath;//address of file containing location and resources
			    //run deployment files to deploy edge containers using K3S
				Process p = Runtime.getRuntime().exec(ShCommand);
			    p.waitFor();
			    
			    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			    bw.write("2012");
			    
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		}//end for

			
		
	}

	/**
	 * @param numOFAgents number of agents/vehicles
	 * @param agid 
	 * @throws IOException
	 * @throws InterruptedException
	 * write deployment file for every agent and then run shell script for that agent while 
	 * passing required arguments including resource demands for its service
	 *
	 */
	private static void create_agents(int numOFAgents, int[] agid) throws IOException, InterruptedException {
		System.out.println("Agents container creation...");
		System.out.println("--------------------------------");
		
		java.util.Map<String, String> env = new HashMap<String, String>();
		
		for (int i = 0 ; i<numOFAgents ; i++) { 
			User u = new User("agent"+agid[i], agid[i]);
			agents.add(u); 
					}
		System.out.println("size: "+agents.size());
		Map.getAgentDemands(agents, Constants.cityconfigfile);
		
		for (int i = 0 ; i<agents.size() ; i++) { 
			Utility.creatAgentYaml(agents.get(i).getId(), i);//String Mobility_Dataset = Constants.mobilitypath;
		
			try {
				 
				String ShCommand = "bash "+Constants.agentBash+agents.get(i).getId()+" "+agents.get(i).getCpu()+" "+agents.get(i).getMemory()+" "+mobFileDir+"/"+agents.get(i).getId()+".csv"+" "+Constants.filePath;//address of mobility files and resource demands
			    //run deployment files to deploy agents using K3S
				Process p = Runtime.getRuntime().exec(ShCommand);
			    p.waitFor();
			    
			    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			    bw.write("2012");
			    
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
				
		}//end for

	}
	
	
}
