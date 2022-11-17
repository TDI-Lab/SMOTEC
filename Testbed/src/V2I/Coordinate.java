package V2I;



import java.util.ArrayList;
import java.util.List;


	public class Coordinate { // extends Map {

		private int coordX;
		private int coordY;

		public Coordinate() {
			
		}

		public boolean isWithinLimitPosition(Coordinate c) {
			if (c.getCoordX() >= Constants.MAX_X ||
				c.getCoordX() < 0 ||
				c.getCoordY() >= Constants.MAX_Y ||
				c.getCoordY() < 0)
				return false;
			else
				return true;
		}

		public void disableSmartThing(User smartThing) {
			smartThing.setCoord(-1, -1);
		}

		public static double radiansToDegree(Double direction) {

			double degree = direction * (180 / Math.PI);

			if (degree < 0)
				degree += 360;

			return degree;
		}

		public static int convertDirection(Double direction) {

			double degree = direction;//radiansToDegree(direction);//
			//System.out.println("deg: " + degree);
			
			if (degree > 337.5 || degree <= 22.5)
				return Directions.EAST;
			else if (degree > 22.5 && degree <= 67.5)
				return Directions.NORTHEAST;
			else if (degree > 67.5 && degree <= 112.5)
				return Directions.NORTH;
			else if (degree > 112.5 && degree <= 157.5)
				return Directions.NORTHWEST;
			else if (degree > 157.5 && degree <= 202.5)
				return Directions.WEST;
			else if (degree > 202.5 && degree <= 247.5)
				return Directions.SOUTHWEST;
			else if (degree > 247.5 && degree <= 292.5)
				return Directions.SOUTH;
			else
				return Directions.SOUTHEAST;
		}

		public void newCoordinate(User smartThing) {

			ArrayList<String[]> path = smartThing.getPath();
			if (smartThing.getTravelTimeId() < path.size()) {
				String[] coodinates = path.get(smartThing.getTravelTimeId());

				smartThing.setTravelTimeId(smartThing.getTravelTimeId() + 1);

				int direction = convertDirection(Double.parseDouble(coodinates[1]));
				//System.out.println("new deg: "+coodinates[1]+" dir "+direction);
				
				int x = (int) Double.parseDouble(coodinates[2]);
				int y = (int) Double.parseDouble(coodinates[3]);
				int speed = (int) Double.parseDouble(coodinates[4]);

				// It checks the CoordDevices limits.
				if (x < 0 || y < 0 || x >= Constants.MAX_X || y >= Constants.MAX_Y) {//zeinab
					disableSmartThing(smartThing);
				}
				else {
					smartThing.setDirection(direction);
					smartThing.getCoord().setCoordX(x);
					smartThing.getCoord().setCoordY(y);
					smartThing.setSpeed(speed);
				}
			}
			else {
				disableSmartThing(smartThing);
			}
		}
	//zeinab
		public void setInitialCoordinate(User smartThing) {

			ArrayList<String[]> path = smartThing.getPath();
			if (!path.isEmpty()) {
				String[] coodinates = path.get(0);

				smartThing.setTravelTimeId(-1);

				int time = (int) Double.parseDouble(coodinates[0]);
				int direction = convertDirection(Double.parseDouble(coodinates[1]));
				
				int x = (int) Double.parseDouble(coodinates[2]);
				int y = (int) Double.parseDouble(coodinates[3]);
				int speed = (int) Double.parseDouble(coodinates[4]);
				
				// It checks the CoordDevices limits.
				if (x < Constants.MIN_X || y < Constants.MIN_Y || x >= Constants.MAX_X || y >= Constants.MAX_Y) {
					disableSmartThing(smartThing);
					System.out.println("out of borders");
				}
				else {
					//System.out.println("inside borders");
					smartThing.setStartTravelTime(time);
					smartThing.setDirection(direction);
					smartThing.getCoord().setCoordX(x);
					smartThing.getCoord().setCoordY(y);
					smartThing.setSpeed(speed);
				}
			}
			else {
				disableSmartThing(smartThing);
			}
			//System.out.println("init position: x "+smartThing.getId()+" "+smartThing.getDirection()+" "+smartThing.getCoord().coordX+" "+smartThing.getCoord().coordY+" "+smartThing.getSpeed()+" "+smartThing.getStartTravelTime());
			
		}
	
		public void newCoordinate(User smartThing, int add, Coordinate coordDevices) {
			if (smartThing.getSpeed() != 0) {
				int increaseX = (smartThing.getCoord().getCoordX() + (smartThing.getSpeed() * add));
				int increaseY = (smartThing.getCoord().getCoordY() + (smartThing.getSpeed() * add));
				int decreaseX = (smartThing.getCoord().getCoordX() - (smartThing.getSpeed() * add));
				int decreaseY = (smartThing.getCoord().getCoordY() - (smartThing.getSpeed() * add));
				int direction = smartThing.getDirection();

				if (decreaseX < 0 || decreaseY < 0 || increaseX >= Constants.MAX_X || increaseY >= Constants.MAX_Y) {
					// It checks the CoordDevices limits.
					disableSmartThing(smartThing);
					return;
				}

				if (direction == Directions.EAST) {
					/* same Y, increase X */
					smartThing.getCoord().setCoordX(increaseX);
				}
				else if (direction == Directions.WEST) {
					/* same Y, decrease X */
					// next position in the same direction
					smartThing.getCoord().setCoordX(decreaseX);
				}
				else if (direction == Directions.SOUTH) {// Directions.NORTH){
					/* same X, increase Y */
					// next position in the same direction
					smartThing.getCoord().setCoordY(increaseY);
				}
				else if (direction == Directions.NORTH) {// Directions.SOUTH){
					/* same X, decrease Y */
					smartThing.getCoord().setCoordY(decreaseY);
				}
				else if (direction == Directions.SOUTHEAST) {// Directions.NORTHEAST){
					/* increase X and Y */
					smartThing.getCoord().setCoordX(increaseX);
					smartThing.getCoord().setCoordY(increaseY);
				}
				else if (direction == Directions.NORTHWEST) {// Directions.SOUTHWEST){
					/* decrease X and Y */
					smartThing.getCoord().setCoordX(decreaseX);
					smartThing.getCoord().setCoordY(decreaseY);
				}
				else if (direction == Directions.SOUTHWEST) {// Directions.NORTHWEST){
					/* decrease X increase Y */
					smartThing.getCoord().setCoordX(decreaseX);
					smartThing.getCoord().setCoordY(increaseY);
				}
				else if (direction == Directions.NORTHEAST) {// Directions.SOUTHEAST){
					/* increase X decrease Y */
					smartThing.getCoord().setCoordX(increaseX);
					smartThing.getCoord().setCoordY(decreaseY);
				}
			}
		}

		public static Coordinate newCoordinateWithError(Coordinate coord, int mobilityPredictionError, int direction) {

			int x = coord.getCoordX(), y = coord.getCoordY();

			if (direction == Directions.EAST) {
				x += mobilityPredictionError;
			}
			else if (direction == Directions.NORTHEAST) {
				x += mobilityPredictionError;
				y -= mobilityPredictionError;
			}
			else if (direction == Directions.NORTH) {
				y -= mobilityPredictionError;
			}
			else if (direction == Directions.NORTHWEST) {
				x -= mobilityPredictionError;
				y -= mobilityPredictionError;
			}
			else if (direction == Directions.WEST) {
				x -= mobilityPredictionError;
			}
			else if (direction == Directions.SOUTHWEST) {
				x -= mobilityPredictionError;
				y += mobilityPredictionError;
			}
			else if (direction == Directions.SOUTH) {
				y += mobilityPredictionError;
			}
			else {
				x += mobilityPredictionError;
				y += mobilityPredictionError;
			}

			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
			if (x >= Constants.MAX_X)
				x = Constants.MAX_X;
			if (y >= Constants.MAX_Y)
				y = Constants.MAX_Y;

			Coordinate coord_result = new Coordinate();
			coord_result.setCoordX(x);
			coord_result.setCoordY(y);
			return coord_result;
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

		//zeinab check: it must not forward coordinates
		public static String[] getNextCoordinate(User smartThing, int periodNum) {
			
			
			ArrayList<String[]> path = smartThing.getPath();
			String [] coodinates = path.get(periodNum);
						
			return coodinates;
			
		}
		}
