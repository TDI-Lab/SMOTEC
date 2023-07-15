package V2I;


/**
 * An abstraction of a mobile device with its available resources
 */
public class MobileDevice {
		private int id;
		private String name;
		private int cpu;
		private int memory;
		private int storage;
		
		public MobileDevice(String name, int agid) {
			setId(agid);
			setName(name);
			
		}
		
		public int getCpu() {
			return cpu;
		}

		public void setCpu(int cpu) {
			this.cpu = cpu;
		}

		public int getMemory() {
			return memory;
		}

		public void setMemory(int memory) {
			this.memory = memory;
		}

		public int getStorage() {
			return storage;
		}

		public void setStorage(int storage) {
			this.storage = storage;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		

	}