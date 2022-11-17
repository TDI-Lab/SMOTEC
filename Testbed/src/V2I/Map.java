package V2I;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;



/**
 * @author rooyesh
 *
 */
public class Map {
	private static Scanner in;
		private static String name;
	    //public static int MAX_X, MAX_Y, MIN_X, MIN_Y, BACKBONE_ROUTERS, EDGE_ROUTERS;//coordinates with respect to netedit
	    private static double area;
	    public static int EdgeRouterDistanceModifier; 
		public static boolean found;
		private static ArrayList<Double> trafficTrace; // array list that has the traffic trace
	    public static double averageTrafficTrace;

	    //@SuppressWarnings("unchecked")
	    /**
		 * get the info of a city from Json input file that includes the general info for all cities
		 * @param city name of the city
		 * @return number of vertices of the city
		 */
	    public static boolean initializeCity(String cityfile) 
	    {
	       
		    
			try {
				
				File inputFile = new File(cityfile);
	            in = new Scanner(inputFile);
	            
	            //double input;
	            //averageTrafficTrace = in.nextString();
	            
	            //while (in.hasNext()) {
	                //String input = in.nextLine();
	                Constants.MAX_X = in.nextInt();
	    			Constants.MIN_X = in.nextInt();
	    			Constants.MIN_Y = in.nextInt();
	    			Constants.MAX_Y = in.nextInt();
	    			Constants.numEdgeNodes = in.nextInt();
	    	 		Constants.numAgents = in.nextInt();
	    	        Constants.Edge_COVERAGE = in.nextInt();
	    	        Constants.CLOUDLET_COVERAGE = in.nextInt();
	    	 		System.out.println("Map is configured with "+Constants.numAgents+" agents and "+Constants.numEdgeNodes+" edge devices");
	                found = true;
		        
		    }catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			
			
			
		    return found;
	    }
	    
	    
				
		public static String getName() {
	        return name;
	    }

		/*
		 * public static int getMax_x() { return MAX_X; }
		 * 
		 * public static int getMax_y() { return MAX_Y; }
		 * 
		 * public static int getMin_x() { return MIN_X; }
		 * 
		 * public static int getMin_y() { return MIN_Y; }
		 * 
		 * public static int getBackbone_routers() { return BACKBONE_ROUTERS; }
		 * 
		 * public static int getEdge_routers() { return EDGE_ROUTERS; }
		 */


		   
	}
