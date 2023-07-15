package V2I;

import java.util.List;


/**
 * defines an edge node with its characteristics
 */
public class EdgeNode {

		public String name;
		protected int myId;
		public String label;
		protected boolean available;
		//location
		int x;
		int y;
		
		//current available resources:
		int CPU;
		int Memory;
		int Storage;
		
		//current load
		int cload, mload, sload;
		
		public EdgeNode(int id, String name, int x, int y, int cPU, int memory, int storage, String elabel) {
			
			this.setMyId(id);
			this.setName(name);
			this.label = elabel;
			
			this.x = x;
			this.y = y;
			CPU = cPU;
			Memory = memory;
			Storage = storage;
			cload = 0;
			mload = 0;
			sload = 0;
			
		}
		private void setName(String name2) {
			name = name2;
		}
		
		public int getMyId() {
			return myId;
		}

		public void setMyId(int myId) {
			this.myId = myId;
		}

		
		public boolean isAvailable() {
			return available;
		}

		public void setAvailable(boolean available) {
			this.available = available;
		}

				
		
	}
