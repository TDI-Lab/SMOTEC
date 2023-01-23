package pie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author rooyesh
 *
 */
public class Utility {
	
	
    //static String planDatasetPath = Constants.base_Path; 
    static String line = "";
    static String cvsSplitBy = ",";
    
    public static void initialize() {
		
	}
	public static void writePlans(Plan[] agentPlans, int agIndex){
		
		//System.out.println("Writing Plans...");
	    //desSort(agentPlans);
        writeUtilizationToFile(agentPlans, agIndex);
        writeBinaryToFile(agentPlans, agIndex);
        
	    }
 
	
    public static void writeUtilizationToFile(Plan[] plans, int id) {
		boolean append_value = false;
		//System.out.println("Writing Utilization Plans with the dimension: "+plans[0].utilPlan.length);
	    	
        try { 
                String file_path_EPOS = Constants.PlanUtilDataset+"agent_"+id+".plans";
                FileWriter writer_EPOS = new FileWriter (file_path_EPOS, append_value);
                
                for(int col =0; col < Constants.EPOS_NUM_PLANS; col++){
                    writer_EPOS.append(String.valueOf(plans[col].getTotalLocalCost()));
                    writer_EPOS.append(":");
                    int size = plans[col].utilPlan.length;
                    for (int i =0; i<size ; i++){//both of CPU and Mem will be printed
                         writer_EPOS.write(String.format("%.12f", plans[col].utilPlan[i]));//preventing wrong written of values as negative values
                         if (i != size-1){
                             writer_EPOS.append(",");
                         }
                     }
                    writer_EPOS.append("\r\n");
                } 
            writer_EPOS.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
      
    }
	private static void writeBinaryToFile(Plan[] plans, int id) {
		// TODO Auto-generated method stub
		boolean append_value = false;
		//System.out.println("Writing Binary Plans...");
	    
		 try { 
             String file_path_EPOS = Constants.PlanBinaryDataset+"agent_"+id+".plans";
             FileWriter writer_EPOS = new FileWriter (file_path_EPOS, append_value);
            
				/*
				 * writer_EPOS.append("- "); for(int snum = 0; snum < plans[0].y.length;
				 * snum++){ writer_EPOS.append(String.valueOf(snum)+" "); }
				 */
           
             for(int col = 0; col < Constants.EPOS_NUM_PLANS; col++){
            	 if(plans[col].empty_node == true) {
            		 writer_EPOS.write(String.format("node without any received request"));
            		 break;
            	 }
            	 
            	 	int size = plans[col].y.length;
            		//System.out.println("Writing Binary Plans with the dimension: "+plans[col].y.length);
            	 	for (int i = 0; i<size ; i++){//both of CPU and Mem will be printed
	                     writer_EPOS.write(String.format(String.valueOf(plans[col].y[i])));//preventing wrong written of values as negative values
	                     if (i != plans[col].y.length-1){
	                         writer_EPOS.append(",");
	                     }
            	 	}
             
                 writer_EPOS.append("\r\n");
             } 
         writer_EPOS.close();
                      
     }
     catch(Exception ex)
     {
         ex.printStackTrace();
     }
   
		
	}
	public static int ReadMapping(int pieIndex) {
		
			System.out.println("reading input from EPOS......\n");
		    int selectedPlans = 0;// output of I-EPOS; index of selected plans
		    File dir = new File(Constants.outpath+"/output");
		    File[] files = dir.listFiles();
		    //to simply check the last output folder of I-EPOS which contains the output of last recent run:
		    File lastModified = Arrays.stream(files).filter(File::isDirectory).max(Comparator.comparing(File::lastModified)).orElse(null);
		    System.out.println(lastModified);
		    /*
		    * In addition to the selected plans, the global-cost (utilization variance) of the selected plans and corresponding local-cost 
		    *is stored for further comparison with EPOS Fog results: 
		    *the realized variance and the predicted one
		    **/
		    String global_cost_File =lastModified+"/global-cost.csv";
		    String local_cost_File =lastModified+"/local-cost.csv";
		    String selected_plans_File =lastModified+"/selected-plans.csv";
		    String line = "";
		    String cvsSplitBy = ",";
		    int i = 1;
		    int minRun=0;
		    int index=0;
		    double loc_costs=0.0;
		    double[] costs= new double[Constants.simulation];
		    //extract the iteration and run number with the minimum global-cost
		    try (BufferedReader br = new BufferedReader(new FileReader(global_cost_File))) 
		    {
		        // br.readHeaders();
		        for (i = 0; i < Constants.iteration; i++)
		            br.readLine();
		        line = br.readLine();//go to the last line
		        String[] input = line.split(cvsSplitBy);
		        for (i = 0; i < Constants.simulation; i++)
		            costs[i] = Double.parseDouble(input[3+i]);//read costs for different simulations
		
		        minRun = findMinRun(costs, Constants.simulation);//the index of min global cost
		        index = Constants.iteration*minRun + Constants.iteration;
		        //System.out.println(" minRun0: "+minRun);
		            
		    }
		    catch (IOException e) 
		    {
		        e.printStackTrace();
		    }  
		    //read the corresponding local-cost with the glocal-cost
		    try (BufferedReader br = new BufferedReader(new FileReader(local_cost_File))) 
		    {
		        //System.out.println(" minRun1: "+minRun);
		        for (i = 0; i < Constants.iteration; i++)
		            br.readLine();
		        line = br.readLine();
		        String[] input = line.split(cvsSplitBy);
		        loc_costs = Double.parseDouble(input[3+minRun]);
		
		    }
		    catch (IOException e) {
		        e.printStackTrace();
		    }
		    //find the selected plan index for each agent/node:
		    try (BufferedReader br = new BufferedReader(new FileReader(selected_plans_File))) 
		    {
		        // br.readHeaders();
		        for (i = 0; i < index; i++)
		            br.readLine();
		        line = br.readLine();//index+1
		        String[] input = line.split(cvsSplitBy);
		        //System.out.println(input[0]+" "+input[1]+" "+input[2]+" "+input[3]+" "+input[4]);
		        selectedPlans = Integer.parseInt(input[2+pieIndex]);
		        
		    }
		    catch (IOException e) 
		    {
		        e.printStackTrace();
		    }
		
		//writeGC(costs[minRun],loc_costs); 
		
		//System.out.println("global cost: "+costs[minRun]+" local cost: "+loc_costs+" minRun "+minRun+" index: "+index);
		return selectedPlans;

}
	public static int findMinRun(double[] globalCosts, int index){
        int mini = 0;
        double min = globalCosts[0];
        for (int j = 0; j<index; j++)
            if (globalCosts[j] < min){//if equal keep the lower index
                min = globalCosts[j];
                mini = j;
            }
        return mini;
    }
   
	public static void creatYaml(int i, Request request) {
		// TODO Auto-generated method stub
		
		//File file = new File("src/main/resources/application.yaml");
        //ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        //ApplicationConfig config = objectMapper.readValue(file, ApplicationConfig.class);
        //System.out.println("Application config info " + config.toString());
        
        PodDefinition podConfig = new PodDefinition();
        podConfig.setPodname("srv"+i);
        podConfig.setAppname("srv"+i); 
        podConfig.setNumReplica(1); 
        podConfig.setMyregistrykey("my-registry-key") ;
        podConfig.setContainerName("srv"+i);
        podConfig.setImageName("zeinabne/edge-testbed:pub");
        podConfig.setContainerPort(8080+i);//must change???
        podConfig.setCpuDemand(request.getCPU());//set cpu resource demand
        podConfig.setMemDemand(request.getMemory());//set memory resource demand
        PodServiceDef srvConfig = new PodServiceDef();
        srvConfig.setSrvname("srv"+i+"-service");
        srvConfig.setAppname("srv"+i);
        srvConfig.setPort(8083);//must change
        srvConfig.setTargetPort(8080+i);//must change
        srvConfig.setNodePort(30000+i);//must change
        //srvConfig.setUsername("appuser");
        //srvConfig.setPassword("apppassword");
        
       
        try {
        	
        	File file = new File(Constants.deployDir+"deployment"+i+".yaml");
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
}
