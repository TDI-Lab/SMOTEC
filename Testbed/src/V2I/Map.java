package V2I;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author rooyesh
 *
 */
public class Map {
	private static Scanner in;
		private static String name;
	    public static boolean found = false;
		
	    //@SuppressWarnings("unchecked")
	    /**
		 * get the info of a city from Json input file that includes number of vertices of the city, agents, and edge nodes with their coverage range
		 * @param cityfile
		 * @return boolean true
		 */
	    public static int[] initializeCity(String cityfile){
	    	
	    	int[] agId;
	    	JSONParser parser = new JSONParser();
			
		    try {
		        Object obj = parser.parse(new FileReader(cityfile));
		        JSONArray jsonObjects =  (JSONArray) obj;

		        for (Object o : jsonObjects) {
		            JSONObject jsonObject = (JSONObject) o;
		          SetAtt((int) (long) jsonObject.get("max_x"), (int) (long) jsonObject.get("max_y"), 
		            		(int) (long) jsonObject.get("min_x"),(int) (long) jsonObject.get("min_y"), 
		            		(int) (long) jsonObject.get("AP_COVERAGE"));
		            
		          Constants.numEdgeNodes = (int) (long) jsonObject.get("edge_nodes");
	              Constants.numAgents = (int) (long) jsonObject.get("numAgents");
	              Constants.Edge_COVERAGE =  (int) (long) jsonObject.get("AP_COVERAGE");
	            
	              agId = new int[Constants.numAgents];
	              JSONArray jsonArray= (JSONArray) jsonObject.get("AgentId");
		            
		            for(int i=0; i<Constants.numAgents; i++){
		            	agId[i] = (int) (long)(jsonArraycpu.get(i));
		            	
		            }
		          System.out.println("Map is configured with "+Constants.numEdgeNodes+" edge devices");
	              found = true;
		        
		    }
		    }
		    catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			return agId;
	    }
	    
	    public static void SetAtt(int max_x1, int max_y1, int min_x1, int min_y1, int coverage) {
			Constants.MAX_X = max_x1 ;
			Constants.MIN_X = min_x1;
			Constants.MIN_Y = min_y1;
			Constants.MAX_Y = max_y1;
			
		    
		}
			
		public static String getName() {
	        return name;
	    }

		public static boolean getAgentDemands(List<User> agents, String cityconfigfile) {
			
			JSONParser parser = new JSONParser();
			
		    try {
		        Object obj = parser.parse(new FileReader(cityconfigfile));
		        JSONArray jsonObjects =  (JSONArray) obj;

		        for (Object o : jsonObjects) {
		            JSONObject jsonObject = (JSONObject) o;
		          
		            JSONArray jsonArraycpu= (JSONArray) jsonObject.get("CPUResourceDemand");
		            JSONArray jsonArraymem= (JSONArray) jsonObject.get("MemResourceDemand");
		            
		            for(int i=0; i<Constants.numAgents; i++){
		            	agents.get(i).setCpu((int) (long)(jsonArraycpu.get(i)));
		            	agents.get(i).setMemory((int) (long)(jsonArraymem.get(i)));
		            	
		            }
		         
		        }
	    	       
	    	        found = true;
		        
		    }
	        catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return found;
	}
}