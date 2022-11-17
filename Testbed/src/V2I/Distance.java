package V2I;


import java.lang.Math;
import java.util.List;

public class Distance {

	public Distance() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @author Marcio Moraes Lopes
	 */
	private static int coordX;
	private static int coordY;
	private static double first;
	private static double second;
	private static double distance;
	private static double theta;

	// verify what return type is better (int or ApDevice)
	public static int theClosestAp(List<EdgeNode> apDevices, User smartThing) {
		int choose = apDevices.get(0).getMyId();// Id of the first ap in the list
		// the first Ap
		double min = checkDistance(apDevices.get(0).getCoord(), smartThing.getCoord());
		double dis;
		for (EdgeNode en : apDevices) {
			//Zeinab
			if(Constants.MaxSmartThingPerAP != 0) {
				dis = checkDistance(en.getCoord(), smartThing.getCoord());
				setDistance(dis);
				if (getDistance() < min) {
					choose = en.getMyId();
					min = getDistance();
				}
			}
		}

		if (min <= Constants.Edge_COVERAGE)// the user should be inside Access Point coverage 
			return choose;// id
		else {

			return -1;// flag error
		}
	}

	//zeinab : to check the closest access points to the future estimated/planned places of smart things
	public static int theClosestAp(List<EdgeNode> apDevices, Coordinate co) {
		int choose = apDevices.get(0).getMyId();// Id of the first ap in the list
		// the first Ap
		double min = checkDistance(apDevices.get(0).getCoord(), co);
		for (EdgeNode ap : apDevices) {
			setDistance(checkDistance(ap.getCoord(), co));

			if (getDistance() < min) {
				choose = ap.getMyId();
				min = getDistance();
			}
		}

		if (min <= Constants.Edge_COVERAGE)// the user should be inside Access Point coverage 
			return choose;// id
		else {

			return -1;// flag error
		}
	}

	public static int theClosestServerCloudlet(List<EdgeNode> serverCloudlets,
		User smartThing) {
		int choose = serverCloudlets.get(0).getMyId();
		double min = checkDistance(serverCloudlets.get(0).getCoord(), smartThing.getFutureCoord());

		for (EdgeNode sc : serverCloudlets) {
			setDistance(checkDistance(sc.getCoord(), smartThing.getFutureCoord()));
			if (getDistance() < min) {
				choose = sc.getMyId();// id
				min = getDistance();
			}
		}

		return choose;// id
	}

	public static int theClosestServerCloudletToAp(List<EdgeNode> serverCloudlets,
			EdgeNode apDevice) {

		int choose = 0;
		double min = checkDistance(serverCloudlets.get(0).getCoord(), apDevice.getCoord());

		for (EdgeNode sc : serverCloudlets) {
			setDistance(checkDistance(sc.getCoord(), apDevice.getCoord()));
			// if(getDistance() != 0)
			if (getDistance() < min) {
				choose = sc.getMyId();// id
				min = getDistance();
			}

		}

		return choose;// id

	}

	public static double findTheta(int coordX, int coordY) {
		setCoordX(coordX);
		setCoordY(coordY);
		if (coordX != 0)// verify if the point is on Y axis and must not do the y/0
			setTheta(Math.atan(((double) coordY) / ((double) coordX)) * (180 / Math.PI));
		else
			setTheta(0);// the point is
		// Theta is negative, but it is first quadrant and it needs to be in second quadrant
		if (coordX < 0 && coordY >= 0)
			return 180 + getTheta();
		// Theta is positive and it needs to be in third quadrant
		else if (coordX < 0 && coordY < 0)
			return 180 + getTheta();
		// Theta is negative and it needs to be in fourth quadrant
		else if (coordX > 0 && coordY < 0)
			return 360 + getTheta();
		// Theta is zero and it needs to be on Y (positive)
		else if (coordX == 0 && coordY > 0)
			return 90 + getTheta();
		// Theta is zero and it needs to be on y (negative)
		else if (coordX == 0 && coordY < 0)
			return 270 + getTheta();
		else
			return getTheta(); /* theta is ok */
	}

	public static int findPosition(double th) {
		setTheta(th);
		/* find the position in 45 degree for each */
		if ((getTheta() >= 0 && getTheta() <= 22.5) || (getTheta() > 337.5 && getTheta() <= 360))
			return Directions.EAST;
		else if (getTheta() > 22.5 && getTheta() <= 67.5)
			return Directions.SOUTHEAST;// Directions.NORTHEAST;
		else if (getTheta() > 67.5 && getTheta() <= 112.5)
			return Directions.SOUTH;// Directions.NORTH;
		else if (getTheta() > 112.5 && getTheta() <= 157.5)
			return Directions.SOUTHWEST;// Directions.NORTHWEST;
		else if (getTheta() > 157.5 && getTheta() <= 202.5)
			return Directions.WEST;
		else if (getTheta() > 202.5 && getTheta() <= 247.5)
			return Directions.NORTHWEST;// Directions.SOUTHWEST;
		else if (getTheta() > 247.5 && getTheta() <= 292.5)
			return Directions.NORTH;// Directions.SOUTH;
		else if (getTheta() > 292.5 && getTheta() <= 337.5)
			return Directions.NORTHEAST;// Directions.SOUTHEAST;
		return Directions.NONE;

	}

	public static double checkDistance(Coordinate firstCoord, Coordinate secondCoord) {
		//Distance between two points formula
		setFirst((double) Math.pow(firstCoord.getCoordX() - secondCoord.getCoordX(), 2));
		setSecond((double) Math.pow(firstCoord.getCoordY() - secondCoord.getCoordY(), 2));
		setDistance(Math.sqrt(getFirst() + getSecond()));
		return getDistance();
	}

	public static int getCoordX() {
		return coordX;
	}

	public static void setCoordX(int coordX) {
		Distance.coordX = coordX;
	}

	public static int getCoordY() {
		return coordY;
	}

	public static void setCoordY(int coordY) {
		Distance.coordY = coordY;
	}

	public static double getFirst() {
		return first;
	}

	public static void setFirst(double first) {
		Distance.first = first;
	}

	public static double getSecond() {
		return second;
	}

	public static void setSecond(double second) {
		Distance.second = second;
	}

	public static double getDistance() {
		return distance;
	}

	public static void setDistance(double distance) {
		Distance.distance = distance;
	}

	public static double getTheta() {
		return theta;
	}

	public static void setTheta(double theta) {
		Distance.theta = theta;
	}

}
