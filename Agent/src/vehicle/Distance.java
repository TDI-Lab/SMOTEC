package vehicle;


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
	

	//verify what return type is better (int or ApDevice)
	public static int theClosestAp(List<EdgeNode> apDevices, Vehicle smartThing) {
		int choose = apDevices.get(0).getMyId();
		// the first Ap
		double min = checkDistance(apDevices.get(0).getCoord(), smartThing.getCoord());
		double dis;
		for (EdgeNode en : apDevices) {
				dis = checkDistance(en.getCoord(), smartThing.getCoord());
				
				if (dis < min) {
					choose = en.getMyId();
					min = dis;
				}
			
		}

		if (min <= Constants.AP_COVERAGE)// the user should be inside Access Point coverage 
			return choose;// id
		else {
			System.out.println("Out of coverage range of all APs");
			return -1;// flag error
		}
	}

	public static int nextClosestAp(List<EdgeNode> apDevices, Vehicle smartThing, int[] closAP) {

		int first, second, third;
		double min = Integer.MAX_VALUE;
		double firstmin = min;
        double secmin = min;
        double thirdmin = min;
        //int[] closAP = new int[]{0,0,0};
		double dis;
		for (EdgeNode en : apDevices) {
			 /* Check if current element is less than
            firstmin, then update first, second and
            third */
            dis = checkDistance(en.getCoord(), smartThing.getCoord());
            if (dis <Constants.AP_COVERAGE) {
            	
	          
				if (dis < firstmin)
	            {
					thirdmin = secmin;
	                secmin = firstmin;
	                firstmin = dis;
	                closAP[2] = closAP[1];
	                closAP[1] = closAP[0];
	                closAP[0] = en.getMyId();
	                
	            }
				
				/* Check if current element is less than
	            secmin then update second and third */
	            else if (dis < secmin)
	            {
	                thirdmin = secmin;
	                secmin = dis;
	                closAP[2] = closAP[1];
	                closAP[1] = en.getMyId();
	                
	            }
				
				/* Check if current element is less than
	            then update third */
	            else if (dis < thirdmin) {
	                thirdmin = dis;
					closAP[2] = en.getMyId();
	            
	            }
	         }	
		}
		
       // System.out.println("First min = " + firstmin + ",id "+ closAP[0]);
       // System.out.println("Second min = " + secmin + ",id "+ closAP[1]);
       // System.out.println("Third min = " + thirdmin + ",id "+closAP[2]);
		
		if(closAP[0] == -1)
			return 0;
		else if(closAP[1] == -1)
			return 1;
		else if(closAP[2] == -1)
			return 2;
		return 3;
	
	
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
