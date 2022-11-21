package pubsub;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Run {
	private static List<EdgeNode> edgeDevices = new ArrayList<>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		User u = new User(args[0]);
		read_mobility(u, args[0]);
		read_infrastructure();
		int newAP;
		int ConnectedEdge = Distance.theClosestAp(edgeDevices, u);
		u.setSourceServerCloudlet(edgeDevices.get(ConnectedEdge));
		
		MqttPublisher mqPub = new MqttPublisher();
		MqttSubscriber mqSub = new MqttSubscriber();
		boolean waitUntil = true;
		mqPub.publish(ConnectedEdge, u.getMyId());
		mqSub.subscribe(ConnectedEdge, u, waitUntil);
		
		while(true) {
			Coordinate.newCoordinate(u);
			newAP = Distance.theClosestAp(edgeDevices, u);
			if (ConnectedEdge == newAP) {
				mqPub.publish(ConnectedEdge, u.getMyId());
				mqSub.subscribe(ConnectedEdge, u.getMyId());
			}
			else {
				u.setSourceServerCloudlet(edgeDevices.get(ConnectedEdge));
				mqPub.publish(ConnectedEdge, u.getMyId());
				mqSub.subscribe(ConnectedEdge, u, waitUntil);
			}
			
			
		}
		
		
	}

	private static void read_infrastructure() {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		int nodeNum = 0;
		String path2net = "";
	    String infraFile = "infrastructure.json";
		try {
	        Object obj = parser.parse(new FileReader("/home/spring/Documents/MQTT/src/pubsub/infrastructure.json"));
			
	        JSONArray jsonObjects =  (JSONArray) obj;

	        for (Object o : jsonObjects) {
	            JSONObject jsonObject = (JSONObject) o;
	          SetAtt((int) (long) jsonObject.get("max_x"), (int) (long) jsonObject.get("max_y"), 
	            		(int) (long) jsonObject.get("min_x"),(int) (long) jsonObject.get("min_y"), 
	            		(int) (long) jsonObject.get("AP_COVERAGE"));
	            
	            Constants.numEdgeNodes = (int) (long) jsonObject.get("edge_nodes");
            
                JSONArray jsonArrayX= (JSONArray) jsonObject.get("Xpoints");
	            JSONArray jsonArrayY= (JSONArray) jsonObject.get("Ypoints");
	            
	            for(int i=0; i<jsonArrayY.size(); i++){
	            	edgeDevices.add(i, new EdgeNode(i, (int) (long)(jsonArrayX.get(i)),(int) (long)(jsonArrayY.get(i))));
	            }
	        }  
		      System.out.println(nodeNum+" EdgeNodes are configured");
                
	        
	    
	}catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ParseException e) {
        e.printStackTrace();
    }
		
	}
	
public static void SetAtt(int max_x1, int max_y1, int min_x1, int min_y1, int coverage) {
	
	
	
	Constants.MAX_X = max_x1 ;
	Constants.MIN_X = min_x1;
	Constants.MIN_Y = min_y1;
	Constants.MAX_Y = max_y1;
	
	Constants.AP_COVERAGE = coverage;
    
}

	public static void read_mobility(User u, String args){	//args is the agentid = 1000
		System.out.println("Reading mobility dataset...");
		String mobilityfile = "/home/spring/Documents/MQTT/src/Mobility_Dataset/" + args+".csv";
		
			String line = "";
			String csvSplitBy = ",";

			try (BufferedReader br = new BufferedReader(new FileReader(mobilityfile))) {

				while ((line = br.readLine()) != null) {
					String[] position = line.split(csvSplitBy);
					u.getPath().add(position);
					//Constants.VehiclesToAP [id][(int)Double.parseDouble(position[0])] = Short.parseShort(position[6]);
				}

				Coordinate coordinate = new Coordinate();
				coordinate.setInitialCoordinate(u);
				//saveMobility(u);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	private static void saveMobility(User st) {
		// TODO Auto-generated method stub
		
	}

}
