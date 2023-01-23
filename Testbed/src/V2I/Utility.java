package V2I;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Utility {

	
	/**
	 * @param i
	 * write one deployment yaml file for every agent
	 */
	public static void creatAgentYaml(int id, int index) {
		// TODO Auto-generated method stub
		
		//File file = new File("src/main/resources/application.yaml");
        //ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        //ApplicationConfig config = objectMapper.readValue(file, ApplicationConfig.class);
        //System.out.println("Application config info " + config.toString());
        
        PodDefinition podConfig = new PodDefinition();
        podConfig.setPodname("agent"+id);
        podConfig.setAppname("agent"+id); 
        podConfig.setNumReplica(1); 
        podConfig.setMyregistrykey("my-registry-key") ;
        podConfig.setContainerName("agent"+id);
        podConfig.setImageName("zeinabne/edge-testbed:pub");
        podConfig.setContainerPort(8080+index);
        
        PodServiceDef srvConfig = new PodServiceDef();
        srvConfig.setSrvname("agent"+id+"-service");
        srvConfig.setAppname("agent"+id);
        srvConfig.setPort(8083);
        srvConfig.setTargetPort(8080+index);
        srvConfig.setNodePort(30000+index);
        //srvConfig.setUsername("appuser");
        //srvConfig.setPassword("apppassword");
        
       
        try {
        	
        	//ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            //objectMapper.writeValue(new File("/home/spring/Documents/Testbed/src/deployments/application1.yaml"), podConfig.toString());
            //objectMapper.writeValueAsString(new File("src/main/resources/application1.yaml"), srvConfig);
        	
            
            File file = new File(Constants.yamlPath+index+".yaml");
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

	public static void creatEdgYaml(int i) {
		// TODO Auto-generated method stub
		
	}
}
