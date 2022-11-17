package V2I;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import experiment.IEPOSExperiment;
//import experiment.IEPOSExperiment;





public class Example_scenario1 {
//topology????
	
	private static List<User> agents = new ArrayList<User>();
	//private List<FogDevice> serverCloudlets = new ArrayList<>();
	private static List<EdgeNode> edgeDevices = new ArrayList<>();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		boolean win =true;
		String cityconfigfile;
		
		if (!Map.initializeCity(Constants.cityconfigfile))
	    	System.exit(0);
		
		String Mobility_Dataset = Constants.molitypath;
		
		create_agents(Constants.numAgents);
		//System.out.println("num of agents: "+agents.size());
		read_mobility();
		
		//from a json file
		//create_infrastructure(Constants.numEdgeNodes);
		
		//create_connections(agents , edgeDevices);
		
		//IEPOSExperiment iepos = new IEPOSExperiment();
	    //iepos.main(args);

		
	}

	private static void create_connections(List<User> agents2, List<EdgeNode> edgeDevices2) {
		// TODO Auto-generated method stub
		
	}

	private static void create_infrastructure(int numEdgeNodes) {
		// TODO Auto-generated method stub
		
	}
//create agent container using k3s: the container should include mobility info and infrastructure info
	private static void create_agents(int numOFUsers) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		for (int i = 0 ; i<numOFUsers ; i++) {
			User u = new User(i, "agent"+i);
			agents.add(u);
			//https://stackoverflow.com/questions/6856028/difference-between-processbuilder-and-runtime-exec
//			Process p = new ProcessBuilder("myCommand", "myArg").start();
//			
//			
//			ProcessBuilder pb = new ProcessBuilder("myshellScript.sh", "myArg1", "myArg2");
//			 Map<String, String> env = pb.environment();
//			 env.put("VAR1", "myValue");
//			 env.remove("OTHERVAR");
//			 env.put("VAR2", env.get("VAR1") + "suffix");
//			 pb.directory(new File("myDir"));
//			 Process p = pb.start();
//			 
			
			String ShCommand = "sh "+Constants.agentScriptfile;
			    Process p = Runtime.getRuntime().exec(ShCommand);
			    p.waitFor();

			    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));


			    String line = "";
			    while ((line = reader.readLine()) != null) {
			        System.out.println(line);
			    }

			    line = "";
			    while ((line = errorReader.readLine()) != null) {
			        System.out.println(line);
			    }
			}
		
	}
	
	//read mobility files in the Mobility_Dataset directory:
	public static void read_mobility(){
		System.out.println("Reading mobility dataset...");
		File folder = new File("Mobility_Dataset");//env_param
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		//int users_count = listOfFiles.length;
		for (int i = 0; i < Constants.numAgents; i++) {
			
			readUserPath(getAgents().get(i), "Mobility_Dataset/" + listOfFiles[i].getName());
		}
		
		
		
	}
	

	private static void readUserPath(User st, String filename) {
		System.out.println("reading mobility path for agent "+st.getMyId());
			int id = st.getMyId();
			String line = "";
			String csvSplitBy = ",";

			try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

				while ((line = br.readLine()) != null) {
					String[] position = line.split(csvSplitBy);
					st.getPath().add(position);
					//Constants.VehiclesToAP [id][(int)Double.parseDouble(position[0])] = Short.parseShort(position[6]);
				}

				Coordinate coordinate = new Coordinate();
				coordinate.setInitialCoordinate(st);
				saveMobility(st);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	private static void saveMobility(User st) {
		// TODO Auto-generated method stub
		
	}
	
	private static List<User> getAgents() {
		// TODO Auto-generated method stub
		return agents;
	}
}
