package V2I;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author zeinab
 *
 */
public class TestMap {
	
		private static String name;
	    public static boolean found = false;
		
	    //@SuppressWarnings("unchecked")
	    /**
		 * get the info of a city from json input file that includes number of corners of the city rectangle, vehicleagents, and edgenodes with their coverage range
		 * @param cityfile
	     * @param agentid 
	     * @param edgeDevices 
		 * @return boolean true
		 */
	    public static int[] initializeCity(String cityfile, Map<Integer, Integer> agentid, List<EdgeNode> edgeDevices){
	    	
	    	int[] agId = null;
	    	JSONParser parser = new JSONParser();
			
		    try {
		        Object obj = parser.parse(new FileReader(cityfile));
		        JSONArray jsonObjects =  (JSONArray) obj;

		        for (Object o : jsonObjects) {
		            JSONObject jsonObject = (JSONObject) o;
		            SetArea((int) (long) jsonObject.get("max_x"), (int) (long) jsonObject.get("max_y"), 
	            		(int) (long) jsonObject.get("min_x"),(int) (long) jsonObject.get("min_y"));
		            
		          Constants.K3sMaster = (String) jsonObject.get("Orchestrator");
		          Constants.numEdgeNodes = (int) (long) jsonObject.get("NumEdgeNodes");
	              Constants.Edge_COVERAGE =  (int) (long) jsonObject.get("AP_COVERAGE");
	              
	              Constants.Secret = (String) jsonObject.get("Secret");
	              Constants.ContainerImagesPath = (String) jsonObject.get("ContainerImagesPath");
	              Constants.ServiceDistributorImage = (String) jsonObject.get("ServiceDistributorImage");
	              Constants.EdgeAgentImage = (String) jsonObject.get("EdgeAgentImage");
	              Constants.MobileAgentImage = (String) jsonObject.get("MobileAgentImage");
	              Constants.configMap = (String) jsonObject.get("ConfigMap");
	              
	              Constants.SrvDisListen = (String) jsonObject.get("SrvDisLi");
	              Constants.SrvDisRes = (String) jsonObject.get("SrvDisRe");
	              Constants.EPOSNumPlans = (int) (long) jsonObject.get("EPOSNumPlans");
	            
	              Constants.numVehicleAgents = (int) (long) jsonObject.get("NumMobileAgents");
	              Constants.MobilityDataset = (String) jsonObject.get("AgentMobilProfile");
	              
	              agId = new int[Constants.numVehicleAgents];
	              JSONArray jsonArray= (JSONArray) jsonObject.get("AgentId");
		            
		          for(int i=0; i<Constants.numVehicleAgents; i++){
		            	agId[i] = (int) (long)(jsonArray.get(i));
		            	agentid.put(agId[i], i);
		            }
		          
		        JSONArray jsonArrayid= (JSONArray) jsonObject.get("EdgeId");
		        JSONArray jsonArrayEdgeLabel= (JSONArray) jsonObject.get("NodeLabel");
	            JSONArray jsonArrayX= (JSONArray) jsonObject.get("Xpoints");
	            JSONArray jsonArrayY= (JSONArray) jsonObject.get("Ypoints");
	            JSONArray jsonArrayCPU= (JSONArray) jsonObject.get("CpuCap");
	            JSONArray jsonArrayMem= (JSONArray) jsonObject.get("MemCap");
	            JSONArray jsonArrayStorage= (JSONArray) jsonObject.get("StorageCap");
	            
	            for(int i=0; i<Constants.numEdgeNodes ; i++){
	            	
	            	edgeDevices.add(i, new EdgeNode(i, "Edge" + (int) (long)(jsonArrayid.get(i)),(int) (long)(jsonArrayX.get(i)),
	            			(int) (long)(jsonArrayY.get(i)),(int) (long)(jsonArrayCPU.get(i)),
	            			(int) (long)(jsonArrayMem.get(i)),(int) (long)(jsonArrayStorage.get(i))
	            			, (String) jsonArrayEdgeLabel.get(i)));
	           
	    		}
		          System.out.println("A test area with "+Constants.numEdgeNodes+" edge devices"+" is configured!");
	              found = true;
		        
		    }
		    }
		    catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (ParseException e) {
					e.printStackTrace();
				}
			
			return agId;
	    }
	    
	    
	    public static void SetArea(int max_x1, int max_y1, int min_x1, int min_y1) {
			Constants.MAX_X = max_x1 ;
			Constants.MIN_X = min_x1;
			Constants.MIN_Y = min_y1;
			Constants.MAX_Y = max_y1;
		}
			
		/**
		 * @param agents
		 * @param cityconfigfile
		 * gets the resource demands for service requests of vehicleagents 
		 * @return
		 */
		public static boolean getAgentDemands(List<MobileDevice> agents, String cityconfigfile) {
			
			JSONParser parser = new JSONParser();
			
		    try {
		        Object obj = parser.parse(new FileReader(cityconfigfile));
		        JSONArray jsonObjects =  (JSONArray) obj;

		        for (Object o : jsonObjects) {
		            JSONObject jsonObject = (JSONObject) o;
		          
		            JSONArray jsonArraycpu= (JSONArray) jsonObject.get("CPUResourceDemand");
		            JSONArray jsonArraymem= (JSONArray) jsonObject.get("MemResourceDemand");
		            JSONArray jsonArraysto= (JSONArray) jsonObject.get("StoResourceDemand");
		            
		            for(int i=0; i<Constants.numVehicleAgents; i++){
		            	agents.get(i).setCpu((int) (long)(jsonArraycpu.get(i)));
		            	agents.get(i).setMemory((int) (long)(jsonArraymem.get(i)));
		            	agents.get(i).setStorage((int) (long)(jsonArraysto.get(i)));
		            }
		         
		        }
	    	       
	    	        found = true;
		        
		    }
	        catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (ParseException e) {
				e.printStackTrace();
			}
		return found;
	}
		
	public static String getName() {
        return name;
    }

}