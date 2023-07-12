package EPOS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Utility {

	static int[] selectedPlans = new int [Constants.numofEdgeAgents];
	
	/**
	 * @param edgeAgentSelPlans returns the output of I-EPOS which represents the index of selected plans
	 */
	public static void ReadMapping(int[] edgeAgentSelPlans) {
		
		System.out.print("\nService distributor reading output of EPOS....");
	   
	    File dir = new File(new File("").getAbsolutePath()+"/output");
	    File[] files = dir.listFiles();
	    
	    //check the last output folder of I-EPOS which contains the output of last recent run:
	    File lastModified = Arrays.stream(files).filter(File::isDirectory).max(Comparator.comparing(File::lastModified)).orElse(null);
	    System.out.println("from address "+ lastModified);
	    /*
	    * In addition to the selected plans, the global-cost (utilization variance) of the selected plans and corresponding local-cost 
	    * are stored for further comparison with EPOS results: 
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
	        
	        for (i = 0; i < Constants.iteration; i++)
	            br.readLine();
	        line = br.readLine();//go to the last line
	        String[] input = line.split(cvsSplitBy);
	        for (i = 0; i < Constants.simulation; i++)
	            costs[i] = Double.parseDouble(input[3+i]);//read costs of different EPOS simulations
	
	        minRun = findMinRun(costs, Constants.simulation);//find the index of minimum value of global cost
	        index = Constants.iteration*minRun + Constants.iteration;
	       
	            
	    }
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }  
	    //read the corresponding local-cost with the minimum global-cost
	    try (BufferedReader br = new BufferedReader(new FileReader(local_cost_File))) 
	    {
	        
	        for (i = 0; i < Constants.iteration; i++)
	            br.readLine();
	        line = br.readLine();
	        String[] input = line.split(cvsSplitBy);
	        loc_costs = Double.parseDouble(input[3+minRun]);
	
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    //find the selected plan index for each agent/edge node:
	    try (BufferedReader br = new BufferedReader(new FileReader(selected_plans_File))) 
	    {
	      
	        for (i = 0; i < index; i++)
	            br.readLine();
	        
	        line = br.readLine();//index+1
	        String[] input = line.split(cvsSplitBy);
	        for (int k = 0; k<Constants.numofEdgeAgents; k++)
	        	edgeAgentSelPlans[k] = Integer.parseInt(input[2+k]);
	        
	    }
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	
	selectedPlans = edgeAgentSelPlans;
}
	
	public static void writeOutput(){
		String comma = ",";
		boolean append = false;
		
		
		try {		
		
			FileWriter fw = new FileWriter(creatFile(), append);
			
			for (int k = 0; k<Constants.numofEdgeAgents; k++) {
	
					fw.append(k+"").append(comma);
					fw.append(selectedPlans[k]+"");
					fw.append(System.lineSeparator());
				}
			
			fw.flush();
			fw.close();
		
		}
		catch(Exception e){
			System.out.println();
			e.printStackTrace();
			
		}
	}
	
	private static String creatFile() {
		File theDir = (new File(new File("").getAbsolutePath()+"/S_plans/"+Constants.curTime));
		if (!theDir.exists()) {
			theDir.mkdirs();
		}
		
		System.out.println(theDir);
		File filePath = new File (theDir+"/selPlans.csv");
		try {
			if(filePath.createNewFile()) {
				
			}
			else {
				
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Constants.curTime++;
		
		String out_file = theDir+"/selPlans.csv";
	return out_file;
		
	}

	/**
	 * @param globalCosts array of global-cost values of different EPOS simulations
	 * @param index the index of minimum global cost
	 * @return
	 */
	public static int findMinRun(double[] globalCosts, int index){
	    int mini = 0;
	    double min = globalCosts[0];
	    for (int j = 0; j<index; j++)
	        if (globalCosts[j] < min){ //if values are equal then keep the lower index
	            min = globalCosts[j];
	            mini = j;
	        }
	    System.out.println("Global cost="+min);
	    return mini;
	}

	/**
	 * @param conf configuration file for SMOTEC 
	 * reads the configuration values from input and set the EPOS parameters based on them
	 */
	public static void read_conf(String conf) {
		String confPath = new File("").getAbsolutePath()+"/conf/";
		
		JSONParser parser = new JSONParser();
		
	    try {
	        Object obj = parser.parse(new FileReader(conf));
			
	        JSONArray jsonObjects =  (JSONArray) obj;

	        int numPlans = 4;
			int planDim = 4 ;
			for (Object o : jsonObjects) {
	            JSONObject jsonObject = (JSONObject) o;
	            
	            Constants.numofEdgeAgents = ((int) (long) jsonObject.get("NumEdgeNodes")>2?(int) (long) jsonObject.get("NumEdgeNodes"):3);
	    		numPlans = (int) (long) jsonObject.get("EPOSNumPlans");
	    		Constants.simulation = (int) (long) jsonObject.get("EPOSnumSimulations");
   				Constants.iteration = (int) (long) jsonObject.get("EPOSnumIterations");
   				planDim = (int) (long) jsonObject.get("EPOSplanDim");
	        }  
			
			setConfPro(confPath+"epos.properties", numPlans, planDim);
		      
			System.out.println("Config values updated: Num of edge agents="+Constants.numofEdgeAgents+", Num of plans per agent="+numPlans+
					" Num of iterations="+Constants.iteration+" Num of Simulations="+Constants.simulation);
                
	        
	    
			}catch (FileNotFoundException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
		
	}

	/**
	 * @param filePath
	 * @param numPlans
	 * @param planDim
	 * sets EPOS parameters
	 */
	private static void setConfPro(String filePath, int numPlans, int planDim) {
		
	      //Instantiating the Scanner class to read the file
	      Scanner sc;
	      try {
	    	  	 sc = new Scanner(new File(filePath));
		
			      //instantiating the StringBuffer class
			      StringBuffer buffer = new StringBuffer();
			      //Reading lines of the file and appending them to StringBuffer
			      while (sc.hasNextLine()) {
			         buffer.append(sc.nextLine()+System.lineSeparator());
			      }
			      
			      String fileContents = buffer.toString();
			      sc.close();
			      
			      String oldLine = "numSimulations=2";
			      String newLine = "numSimulations="+Constants.simulation;
			      //Replacing the old line with new line
			      fileContents = fileContents.replaceAll(oldLine, newLine);
			      
			      String oldLine1 = "numIterations=3";
			      String newLine1 = "numIterations="+Constants.iteration;
			      //Replacing the old line with new line
			      fileContents = fileContents.replaceAll(oldLine1, newLine1);
			      
			      String oldLine2 = "numAgents=3";
			      String newLine2 = "numAgents="+Constants.numofEdgeAgents;
			      //Replacing the old line with new line
			      fileContents = fileContents.replaceAll(oldLine2, newLine2);
			      
			      String oldLine3 = "numPlans=4";
			      String newLine3 = "numPlans="+numPlans;
			      //Replacing the old line with new line
			      fileContents = fileContents.replaceAll(oldLine3, newLine3);
			      
			      String oldLine4 = "planDim=4";
			      String newLine4 = "planDim="+planDim;
			      //Replacing the old line with new line
			      fileContents = fileContents.replaceAll(oldLine4, newLine4);
			      
			      //instantiating the FileWriter class
			      FileWriter writer = new FileWriter(filePath);
			      writer.append(fileContents);
			      writer.flush();
				
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	}


}
