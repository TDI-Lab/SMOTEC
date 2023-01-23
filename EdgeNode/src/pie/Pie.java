package pie;

import java.util.ArrayList;
import java.util.List;

public class Pie {

	String name;
	int id;
	int x;
	int y;
	int CPU;
	int Memory;
	int Storage;
	
	private static List<Integer> connected = new ArrayList<>();//connected agents
	private static List<Integer> hosted = new ArrayList<>();//hosted services from agents 
	
	
	public Pie(int id, int x, int y, int cPU, int memory, int storage) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		CPU = cPU;
		Memory = memory;
		Storage = storage;
	}

	public Pie(String name) {
		
		this.name = name;
		this.id = Integer.parseInt(name);
	}
	
	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	public void setResources(int cPU, int memory, int storage) {
		CPU = cPU;
		Memory = memory;
		Storage = storage;
	}
	public void setCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	
}
