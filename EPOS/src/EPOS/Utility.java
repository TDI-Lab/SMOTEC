package EPOS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Utility {

		
	static void sendSelectedPlans() {
		// TODO Auto-generated method stub
		int[] map = new int [Constants.numofEdgeAgents];
		Utility.ReadMapping(map);
		String selectPlans = "EPOS!";
		
		System.out.println("Sending selected plans...");
		for (int k = 0; k<Constants.numofEdgeAgents; k++) {
			System.out.println("EdgeAgent: "+k+", selected plan index: "+map[k]);
				
			  selectPlans += k+":"+map[k]+":";
				  
			}
		
			Producer planProd = new Producer();
			planProd.open();
			planProd.send_message(selectPlans);
			planProd.close();
		   
			
		   	  
				
	}
	public static void ReadMapping(int[] edgeAgentSelPlans) {
		
		System.out.println("reading input from EPOS......\n");
	    //int selectedPlans = 0;// output of I-EPOS; index of selected plans
	    File dir = new File(new File("").getAbsolutePath()+"/output");
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
	        for (int k = 0; k<Constants.numofEdgeAgents; k++)
	        	edgeAgentSelPlans[k] = Integer.parseInt(input[2+k]);
	        
	    }
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	
	//writeGC(costs[minRun],loc_costs); 
	
	//System.out.println("global cost: "+costs[minRun]+" local cost: "+loc_costs+" minRun "+minRun+" index: "+index);
	//return piIndex;

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

}
