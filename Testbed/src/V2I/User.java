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
import java.util.Set;



public class User {
		private int id;
		private String name;
		private int cpu;
		private int memory;
		
		public User(String name, int agid) {
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
			// TODO Auto-generated method stub
			return id;
		}
		

	}