package vehicle;



import java.util.ArrayList;
import java.util.List;


	public class Coordinate { 

		private int coordX;
		private int coordY;

		public Coordinate() {
			
		}

		public static void setVehicleDown(Vehicle veh) {
			veh.setCoord(-1, -1);
			veh.setStatus(false);
			
		}

		
		public static void newCoordinate(Vehicle veh) {

			ArrayList<String[]> path = veh.getPath();
			if (veh.getTravelTime() < path.size()) {
				String[] coodinates = path.get(veh.getTravelTime());

				veh.setTravelTime(veh.getTravelTime() + 1);

				int x = (int) Double.parseDouble(coodinates[2]);
				int y = (int) Double.parseDouble(coodinates[3]);
								if (x < Constants.MIN_X || y < Constants.MIN_Y || x >= Constants.MAX_X || y >= Constants.MAX_Y) {//zeinab
					setVehicleDown(veh);
					System.out.println("VehAgent "+veh.getMyId()+" out of map.");
				}
				else {
					veh.getCoord().setCoordX(x);
					veh.getCoord().setCoordY(y);
					System.out.println("New coordinate : x="+ x+" "+"y="+ y+" time="+veh.getTravelTime());
				}
			}
			else {
				System.out.println("VehAgent "+veh.getMyId()+" end of mobility path.");
				setVehicleDown(veh);
			}
		}
	
		public static void setInitialCoordinate(Vehicle veh) {

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
					System.out.println("VehAgent coordinate initializing: x="+ x+" "+"y="+ y+" time="+time);
					
				}
			}
			else {
				System.out.println("path is empty");
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
