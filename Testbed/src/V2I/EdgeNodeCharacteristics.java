package V2I;


import java.util.ArrayList;
import java.util.List;

public class EdgeNodeCharacteristics {

		/** The resource id -- setup when Resource is created. */
		private int id;

		/** The architecture. */
		private String architecture;

		/** The os. */
		private String os;

		
		/** Price/CPU-unit if unit = sec., then G$/CPU-sec. */
		private double costPerSecond;

		
		/** The cost per mem. */
		private double costPerMem;

		/** The cost per storage. */
		private double costPerStorage;

		/** The cost per bw. */
		private double costPerBw;

		private int geoCoverage;

		public EdgeNodeCharacteristics(
			String architecture,
			String os,
			String vmm,
			double timeZone,
			double costPerSec,
			double costPerMem,
			double costPerStorage,
			double costPerBw) {
			setId(-1);
			setArchitecture(architecture);
			setOs(os);
			setCostPerSecond(costPerSec);
			setCostPerMem(costPerMem);
			setCostPerStorage(costPerStorage);
			setCostPerBw(costPerBw);

		}

		public int getMips() {
			int mips = 0;
			return mips;
		}

		
		public double getCostPerMi() {
			return getCostPerSecond() / getMips();
		}

		/**
		 * Checks whether all machines of this resource are working properly or not.
		 * 
		 * @return if all machines are working, otherwise
		 */
		public boolean isWorking() {
			boolean result = false;
		
			return result;
		}

		/**
		 * Get the cost to use memory in this resource.
		 * 
		 * @return the cost to use memory
		 */
		public double getCostPerMem() {
			return costPerMem;
		}

		/**
		 * Sets cost to use memory.
		 * 
		 * @param costPerMem
		 *        cost to use memory
		 * @pre costPerMem >= 0
		 * @post $none
		 */
		public void setCostPerMem(double costPerMem) {
			this.costPerMem = costPerMem;
		}

		/**
		 * Get the cost to use storage in this resource.
		 * 
		 * @return the cost to use storage
		 */
		public double getCostPerStorage() {
			return costPerStorage;
		}

		/**
		 * Sets cost to use storage.
		 * 
		 * @param costPerStorage
		 *        cost to use storage
		 * @pre costPerStorage >= 0
		 * @post $none
		 */
		public void setCostPerStorage(double costPerStorage) {
			this.costPerStorage = costPerStorage;
		}

		/**
		 * Get the cost to use bandwidth in this resource.
		 * 
		 * @return the cost to use bw
		 */
		public double getCostPerBw() {
			return costPerBw;
		}

		/**
		 * Sets cost to use bw cost to use bw.
		 * 
		 * @param costPerBw
		 *        the cost per bw
		 * @pre costPerBw >= 0
		 * @post $none
		 */
		public void setCostPerBw(double costPerBw) {
			this.costPerBw = costPerBw;
		}

		
		/**
		 * Gets the id.
		 * 
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Sets the id.
		 * 
		 * @param id
		 *        the new id
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * Gets the architecture.
		 * 
		 * @return the architecture
		 */
		protected String getArchitecture() {
			return architecture;
		}

		/**
		 * Sets the architecture.
		 * 
		 * @param architecture
		 *        the new architecture
		 */
		protected void setArchitecture(String architecture) {
			this.architecture = architecture;
		}

		/**
		 * Gets the os.
		 * 
		 * @return the os
		 */
		protected String getOs() {
			return os;
		}

		/**
		 * Sets the os.
		 * 
		 * @param os
		 *        the new os
		 */
		protected void setOs(String os) {
			this.os = os;
		}

		
		/**
		 * Gets the cost per second.
		 * 
		 * @return the cost per second
		 */
		public double getCostPerSecond() {
			return costPerSecond;
		}

		/**
		 * Sets the cost per second.
		 * 
		 * @param costPerSecond
		 *        the new cost per second
		 */
		protected void setCostPerSecond(double costPerSecond) {
			this.costPerSecond = costPerSecond;
		}

		
		public int getGeoCoverage() {
			return geoCoverage;
		}

		public void setGeoCoverage(int geoCoverage) {
			this.geoCoverage = geoCoverage;
		}
	}

