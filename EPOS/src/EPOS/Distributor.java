package EPOS;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import experiment.IEPOSExperiment;

/**
 * @author Zeinab
 * SMOTEC uses this service distributor program as a wrapper for its placement policy: EPOS 
 * EPOS can be replaces with other resource allocation policies.
 * for more information about EPOS refer to the following papers:
 * Nezami, Z., Zamanifar, K., Djemame, K., & Pournaras, E. (2021). Decentralized edge-to-cloud load balancing: Service placement for the Internet of Things. IEEE Access, 9, 64983-65000.
 * Pournaras, E., Pilgerstorfer, P., & Asikis, T. (2018). Decentralized collective learning for self-managed sharing economies. ACM Transactions on Autonomous and Adaptive Systems (TAAS), 13(2), 1-33.
 */
public class Distributor {


	static String brokerUrl;
	static boolean []flag;
	static HashSet<String> planSet;
	static String [] Uplans;
	static String[] Bplans;
	static Context context = ZMQ.context(1);
	static Map<Integer, Integer> EdgeTimeSet = new HashMap<>();//keeps the record of received service placement plans from EdgeAgents 
	
	//@SuppressWarnings("static-access")
	/**
	 * @param args
	 * @throws IOException
	 * This program is always listening to the port 32500 for service placement plans from EdgeAgent
	 * After receiving the plans EPOS simulation runs to find the globally optimized plans (plans with the lowest variance for load-balancing )
	 * Then the index of selected plans is sent back to EdgeAgent on port 32501.
	 * A service placement plan includes one binary plan and one associated utilization plan
	 *  
	 */
	public static void main(String [] args) throws IOException {
		    
		System.out.println("Service distributor (EPOS) started and running...");
	    String conf = new File("").getAbsolutePath()+"/conf/TestbedConfig.json";
		Constants.numMsg = 0;//saves the number of control messages are exchanged with this program as a measure of communication cost
		Utility.read_conf(conf);

		//receives placement plans:
		Consumer co = new Consumer(context);
	    co.open();

	    //returns selected plans:
	    Producer planProd = new Producer(context);
		planProd.open();
		
		while(true) {
		
			Uplans = new String[3];
			Bplans = new String[3];
		    flag = new boolean[3];
			int[] map = new int [Constants.numofEdgeAgents];
			
			int numReceivedPlans = co.waitAndReceive(Uplans, Bplans, flag, EdgeTimeSet);//listens for service placement plans from EdgeAgents
			
			
            if(numReceivedPlans >= 1) {
				System.out.println("EPOS writing plans....");
				writePlans(Uplans, Bplans);
				
				//************************************************************
				//The EPOS module can be replaced with another service distributor here:
				System.out.println("Running EPOS ....");
				IEPOSExperiment iepos = new IEPOSExperiment();
				iepos.main(args);
				//************************************************************
				
				Utility.ReadMapping(map);//extracts the output results from EPOS
				
				for (int sendCounter = 0; sendCounter<3; sendCounter++) {
					planProd.sendSelectedPlans(map);//sends selected plans back to the EdgeAgents
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			    System.out.println("EPOS results are on the way....");
			    Utility.writeOutput();
			    
            }
            
            updateSettings();
            System.out.println("Num of control Msg exchanged: "+Constants.numMsg);
            
			}
	}
	
	private static void updateSettings() {
		
		planSet = new HashSet<String>() ;
		Uplans = new String[3];
	    flag[0] = flag[1] = flag[2] =false;
	    
	}

	/**
	 * @param uplans utilization plans received form EdgeAgents 
	 * @param bplans Binary plans received from EdgeAgents
	 * writes plans in the input directory of EPOS
	 */
	private static void writePlans(String[] uplans, String[] bplans) {
		
		String udatasets = new File("").getAbsolutePath()+("/datasets/Utilization");
		String bdatasets = new File("").getAbsolutePath()+("/datasets/Binary");
		
		for (int i = 0; i<2; i++) {
			if (flag[i]) {
				System.out.println("Writing plans for EdgeAgent "+i);
				try {
					
				PrintWriter uout = new PrintWriter(udatasets+"/agent_"+i+".plans");
				PrintWriter bout = new PrintWriter(bdatasets+"/agent_"+i+".plans");
				
					uout.println(uplans[i]);
					bout.println(bplans[i]);
					uout.close();
					bout.close();
				}
				catch (IOException e) 
			    {
			        e.printStackTrace();
			    }  
			}
			
		}
		
		
		
	}
}
