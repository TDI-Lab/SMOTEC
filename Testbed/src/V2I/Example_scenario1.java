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

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import experiment.IEPOSExperiment;
//import experiment.IEPOSExperiment;





public class Example_scenario1 {
//topology????
	
	private static List<User> agents = new ArrayList<User>();
	//private List<FogDevice> serverCloudlets = new ArrayList<>();
	private static List<EdgeNode> edgeDevices = new ArrayList<>();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		//boolean win =true;
		//String cityconfigfile;
		
		if (!Map.initializeCity(Constants.cityconfigfile))
	    	System.exit(0);
		
		//String Mobility_Dataset = Constants.mobilitypath;
		
		create_agents(Constants.numAgents);
		//System.out.println("num of agents: "+agents.size());
		//read_mobility();
		
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

	private static void create_agents(int numOFUsers) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		java.util.Map<String, String> env = new HashMap<String, String>();
		//the shell script must be executable and readable for this user
		//https://stackoverflow.com/questions/52132008/java-permission-denied-when-attempting-to-execute-shell-script
		
		//Map mMap = new HashMap();
		/*
		 * for (int i = 0 ; i<numOFUsers ; i++) { User u = new User(i, "agent"+i);
		 * agents.add(u); }
		 */
//			Process p = new ProcessBuilder("myCommand", "myArg").start();
//			
//			
		/*
			ProcessBuilder pb = new ProcessBuilder(Constants.agentScriptfile, "myArg1", "myArg2");
			 env = pb.environment();
			 env.put("VAR1", "myValue");
			 env.remove("OTHERVAR");
			 env.put("VAR2", env.get("VAR1") + "suffix");
			 //pb.directory(new File("myDir"));
			 Process p = pb.start();


		ProcessBuilder pb = new ProcessBuilder("agent.sh", "2", "hello");
		pb.directory(Constants.agentScriptfile);
		Process proc = pb.start();
		*/	
		//https://stackoverflow.com/questions/11198678/processbuilder-environment-variable-in-java
		//sudo apt install --reinstall coreutils for chmod on file/folders
		
		//write deployments using json:
		int i = 0;
		for (i = 0 ; i<numOFUsers ; i++) { 
			User u = new User(i, "agent"+i);
			agents.add(u); 
			creatYaml(i);
		
		
		try {
			//while true; do curl -s https://some.site.com/someImage.jpg > /dev/null & echo blah ; done
	
				String ShCommand = "bash /home/spring/Documents/Testbed/src/agent.sh "+ numOFUsers+ " hello";
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
				
		}

	}
	
	private static void creatYaml(int i) {
		// TODO Auto-generated method stub
		
		//File file = new File("src/main/resources/application.yaml");
        //ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        //ApplicationConfig config = objectMapper.readValue(file, ApplicationConfig.class);
        //System.out.println("Application config info " + config.toString());
        
        PodDefinition podConfig = new PodDefinition();
        podConfig.setPodname("agent"+i);
        podConfig.setAppname("agent"+i); 
        podConfig.setNumReplica(1); 
        podConfig.setMyregistrykey("my-registry-key") ;
        podConfig.setContainerName("agent"+i);
        podConfig.setImageName("zeinabne/edge-testbed:pub");
        podConfig.setContainerPort(8080+i);
        
        PodServiceDef srvConfig = new PodServiceDef();
        srvConfig.setSrvname("agent"+i+"-service");
        srvConfig.setAppname("agent"+i);
        srvConfig.setPort(8083);
        srvConfig.setTargetPort(8080+i);
        srvConfig.setNodePort(30000+i);
        //srvConfig.setUsername("appuser");
        //srvConfig.setPassword("apppassword");
        
       
        try {
        	
        	//ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            //objectMapper.writeValue(new File("/home/spring/Documents/Testbed/src/deployments/application1.yaml"), podConfig.toString());
            //objectMapper.writeValueAsString(new File("src/main/resources/application1.yaml"), srvConfig);
        	
            
            File file = new File("/home/spring/Documents/Testbed/src/deployments/deployment"+i+".yaml");
            FileWriter fr = null;

		    fr = new FileWriter(file);
		    fr.write(podConfig.toString());
		    fr.write(srvConfig.toString());            
		    fr.close();
                    
        } 
        catch (IOException e) {
                e.printStackTrace();
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
