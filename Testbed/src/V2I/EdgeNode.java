package V2I;


import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class EdgeNode {

		public String name;
		protected List<String> activeApplications;
		
		protected boolean available;
		
		protected int myId;

		public EdgeNode(int id, String name) {
			this.setMyId(id);
			this.setName(name);
			
		}
		private void setName(String name2) {
			// TODO Auto-generated method stub
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
