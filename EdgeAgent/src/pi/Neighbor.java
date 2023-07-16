package pi;

/**
 * @author zeinab
 * Defines the edge nodes in the network with their resources and locations
 *
 */
public class Neighbor {
	
	int id;
	int x;
	int y;
	//current available resources:
	int CPU;
	int Memory;
	int Storage;
	//Max capacity:
	int CPUMax;
	int MemMax;
	int StorageMax;
	
	String nodelabel; 
	
	int numServices = 0;
	
	public int IncRunningSrv() {
		return numServices++;
	}
	public int DecRunningSrv() {
		return numServices--;
	}
	public int gettNumServices(int numServices) {
		return numServices;
	}
	public Neighbor(int id, String label, int x, int y, int cPU, int memory, int storage) {
		
		this.id = id;
		this.nodelabel = label;
		this.x = x;
		this.y = y;
		CPU = cPU;
		Memory = memory;
		Storage = storage;
		
		CPUMax = cPU;
		MemMax = memory;
		StorageMax = storage;
		
		numServices = 0;
	}
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getCPU() {
		return CPU;
	}

	public int getMemory() {
		return Memory;
	}

	public int getStorage() {
		return Storage;
	}
}
