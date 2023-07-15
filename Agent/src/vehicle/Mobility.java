package vehicle;



import java.util.ArrayList;
import java.util.List;


	/**
	 * @author zeinab
	 * This class reads the mobility profile of a mobile agent and keeps updating it over time
	 */
	public class Mobility { 

		private int coordX;
		private int coordY;

		public Mobility() {
			
		}

		public static void setVehicleDown(Vehicle veh) {
			veh.setCoord(-1, -1);
			veh.setStatus(false);
			
		}

		
		/**
		 * @param veh
		 * updates VehicleAgent location with its next profile value
		 */
		public static void newlocation(Vehicle veh) {

			ArrayList<String[]> path = veh.getPath();
			if (veh.getTravelTime() < path.size()) {
				String[] coodinates = path.get(veh.getTravelTime());

				veh.setTravelTime(veh.getTravelTime() + 1);

				int x = (int) Double.parseDouble(coodinates[2]);
				int y = (int) Double.parseDouble(coodinates[3]);
				
				if (x < Constants.MIN_X || y < Constants.MIN_Y || x >= Constants.MAX_X || y >= Constants.MAX_Y) {
					setVehicleDown(veh);
					System.out.println("VehicleAgent "+veh.getMyId()+" out of map.");
				}
				else {
					veh.getCoord().setCoordX(x);
					veh.getCoord().setCoordY(y);
					System.out.println("New location : x="+ x+" "+"y="+ y+" time="+veh.getTravelTime());
				}
			}
			else {
				System.out.println("VehicleAgent "+veh.getMyId()+" end of mobility path.");
				setVehicleDown(veh);
			}
		}
	
		/**
		 * @param veh
		 * initializes the location of VehicleAgent using its mobilityprofile
		 */
		public static void setInitialProfile(Vehicle veh) {

			ArrayList<String[]> path = veh.getPath();
			if (!path.isEmpty()) {
				String[] coodinates = path.get(0);

				veh.setTravelTime(0);

				int time = (int) Double.parseDouble(coodinates[0]);
				
				int x = (int) Double.parseDouble(coodinates[2]);
				int y = (int) Double.parseDouble(coodinates[3]);
				if (x < Constants.MIN_X || y < Constants.MIN_Y || x >= Constants.MAX_X || y >= Constants.MAX_Y) {
					System.out.println("out of borders");
					System.out.println(x+" "+ Constants.MIN_X +" "+Constants.MAX_X +" y "+ Constants.MIN_Y + " "+ Constants.MAX_Y);
					setVehicleDown(veh);
				
				}
				else {
					veh.setStartTravelTime(time);
					veh.getCoord().setCoordX(x);
					veh.getCoord().setCoordY(y);
					System.out.println("VehicleAgent location initializing: x="+ x+" "+"y="+ y+" time="+time);
					
				}
			}
			else {
				System.out.println("no mobility data");
				setVehicleDown(veh);
			}
			
		}
	
		
		public int getCoordX() {
			return coordX;
		}

		public void setCoordX(int coordX) {
			this.coordX = coordX;
		}

		public int getCoordY() {
			return coordY;
		}

		public void setCoordY(int coordY) {
			this.coordY = coordY;
		}

		
		}
