package service.placement;
import experiment.IEPOSExperiment;

public class ServiceDistributor {

	
	@SuppressWarnings("static-access")
	public static void main(String [] args) {
		
			IEPOSExperiment iepos = new IEPOSExperiment();
		    iepos.main(args);
		    while (true) {
		    	System.out.println("running");
		    }
		 
	}
}
