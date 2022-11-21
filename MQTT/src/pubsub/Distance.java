package pubsub;


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
	

	// verify what return type is better (int or ApDevice)
	public static int theClosestAp(List<EdgeNode> apDevices, User smartThing) {
		int choose = apDevices.get(0).id;
		// the first Ap
		double min = checkDistance(apDevices.get(0).getCoord(), smartThing.getCoord());
		double dis;
		for (EdgeNode en : apDevices) {
			//Zeinab
				dis = checkDistance(en.getCoord(), smartThing.getCoord());
				setDistance(dis);
				if (getDistance() < min) {
					choose = en.getMyId();
					min = getDistance();
				}
			
		}

		if (min <= Constants.AP_COVERAGE)// the user should be inside Access Point coverage 
			return choose;// id
		else {

			return -1;// flag error
		}
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

	

}
