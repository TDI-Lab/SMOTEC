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
        podConfig.setMyregistrykey(Constants.Secret) ;
        podConfig.setContainerName("agent"+id);
        podConfig.setImageName(Constants.ContainerImagesPath+"agents");
        podConfig.setContainerPort(8090+index);
        podConfig.setId(id);
        
        VolumDef volDef = new VolumDef();
        volDef.setNodeSelector("master");
        
        ServiceDef srvConfig = new ServiceDef();
        srvConfig.setSrvname("agent"+id+"-service");
        srvConfig.setAppname("agent"+id);
        srvConfig.setPort(8090+index);
        srvConfig.setTargetPort(8090+index);
        srvConfig.setNodePort(30010+index);
        //srvConfig.setUsername("appuser");
        //srvConfig.setPassword("apppassword");
        
       
        try {
        	
        	//ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            //objectMapper.writeValue(new File("/home/spring/Documents/Testbed/src/deployments/application1.yaml"), podConfig.toString());
            //objectMapper.writeValueAsString(new File("src/main/resources/application1.yaml"), srvConfig);
        	
            
            File file = new File(Constants.yamlPath+id+".yaml");
            FileWriter fr = null;

		    fr = new FileWriter(file);
		    fr.write(podConfig.toString());
		    fr.write(volDef.toStringAgent());
		    fr.write(srvConfig.toString());            
		    fr.close();
                    
        } 
        catch (IOException e) {
                e.printStackTrace();
        }

	}

	public static void creatEdgYaml(int i) {
		PodDefinition podConfig = new PodDefinition();
        podConfig.setPodname("edge"+i);
        podConfig.setAppname("edge"+i); 
        podConfig.setNumReplica(1); 
        podConfig.setMyregistrykey(Constants.Secret) ;
        podConfig.setContainerName("edge"+i);
        podConfig.setImageName(Constants.ContainerImagesPath+"edges");
        podConfig.setContainerPort(8100+i);
        podConfig.setId(i);
        
        VolumDef volDef = new VolumDef();
        volDef.setNodeSelector("master");
        
        
        ServiceDef srvConfig = new ServiceDef();
        srvConfig.setSrvname("edge"+i+"-service");
        srvConfig.setAppname("edge"+i);
        srvConfig.setPort(8100+i);
        srvConfig.setTargetPort(8100+i);
        srvConfig.setNodePort(30030+i);
        
        try {
        	
        	//ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            //objectMapper.writeValue(new File("/home/spring/Documents/Testbed/src/deployments/application1.yaml"), podConfig.toString());
            //objectMapper.writeValueAsString(new File("src/main/resources/application1.yaml"), srvConfig);
        	
            
            File file = new File(Constants.yamlPath+i+".yaml");
            FileWriter fr = null;

		    fr = new FileWriter(file);
		    fr.write(podConfig.toString());
		    fr.write(volDef.toStringAgent());
		    fr.write(srvConfig.toString()); 
		    
		    fr.close();
                    
        } 
        catch (IOException e) {
                e.printStackTrace();
        }
	}
}
