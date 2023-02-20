package vehicle;

public class EdgeNode {
	
	protected Coordinate coord;
	
	public int id;
	
	public EdgeNode(int i, int Coor1, int Coor2) {
		id = i;
		this.coord = new Coordinate();
		setCoord(Coor1, Coor2);
		
	}

	public void setCoord(int coordX, int coordY) {
		this.coord.setCoordX(coordX);
		this.coord.setCoordY(coordY);
	}

	/**
	 * @return index of edge node in list which is equal to its id
	 */
	public int getMyId() {
		// TODO Auto-generated method stub
		return id;
	}

	public void setCoord(Coordinate coord) {
		this.coord = coord;
	}

	public Coordinate getCoord() {
		// TODO Auto-generated method stub
		return coord;
	}


}
