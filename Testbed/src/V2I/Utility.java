package V2I;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Utility {

	/**
	 * @param vehagent
	 * @param index
	 * creates and writes one deployment .yaml file for every vehicleagent
	 */
	public static void creatAgentYaml(MobileDevice vehagent, int index) {
		  
		int id = vehagent.getId();
        PodDefinition podConfig = new PodDefinition();
        podConfig.setPodname("agent"+id);
        podConfig.setAppname("agent"+id); 
        podConfig.setNumReplica(1); 
        podConfig.setMyregistrykey(Constants.Secret) ;
        podConfig.setContainerName("agent"+id);
        podConfig.setImageName(Constants.ContainerImagesPath+Constants.MobileAgentImage);
        podConfig.setContainerPort(22000+id);
        podConfig.setArg(id, Constants.Edge_COVERAGE, vehagent.getCpu(), vehagent.getMemory(), vehagent.getStorage(), Constants.MobilityDataset);
        
        VolumDef volDef = new VolumDef();
        volDef.setConfigMap(Constants.configMap);
        volDef.setMountPath(Constants.agentMountPath);
        volDef.setSubPath(Constants.SubPath);
        //deploy .yaml files on orchestrator (master node)
        volDef.setNodeSelector(Constants.K3sMaster);
        
        ServiceDef srvConfig = new ServiceDef();
        srvConfig.setSrvname("agent"+id+"-service");
        srvConfig.setAppname("agent"+id);
        srvConfig.setPort("listener", 22000+id);
       
        try {
        	 
            File file = new File(Constants.yamlPath+id+".yaml");
            FileWriter fr = null;

		    fr = new FileWriter(file);
		    fr.write(podConfig.toString());
		    fr.write(volDef.toStringVol());
		    fr.write(volDef.toStringNodeSel());
		    fr.write(srvConfig.toString());            
		    fr.close();
                    
        } 
        catch (IOException e) {
                e.printStackTrace();
        }

	}

	/**
	 * @param edgeIndex
	 * creates yaml files for edgeagents  which will be deployed on edgenodes
	 * @param label 
	 */
	public static void creatEdgYaml(int edgeIndex, String label) {
		PodDefinition podConfig = new PodDefinition();
        podConfig.setPodname("edge"+edgeIndex);
        podConfig.setAppname("edge"+edgeIndex); 
        podConfig.setNumReplica(1); 
        podConfig.setMyregistrykey(Constants.Secret) ;
        podConfig.setContainerName("edge"+edgeIndex);
        podConfig.setImageName(Constants.ContainerImagesPath+Constants.EdgeAgentImage);
        podConfig.setContainerPort(Constants.edgeListenTopUpPort+edgeIndex, Constants.edgeResponseTopUpPort+edgeIndex, Constants.edgeTrafMonTopUpPort+edgeIndex);
        podConfig.setArg(edgeIndex, Constants.ContainerImagesPath, Constants.SrvDisListen, Constants.SrvDisRes, Constants.EPOSNumPlans);
        
        VolumDef volDef = new VolumDef();
        volDef.setConfigMap(Constants.configMap);
        volDef.setMountPath(Constants.edgeMountPath);
        volDef.setSubPath(Constants.SubPath);
        
        volDef.setNodeSelector(label);

        ServiceDef srvConfig = new ServiceDef();
        srvConfig.setSrvname("edge"+edgeIndex+"-listen","edge"+edgeIndex+"-response","edge"+edgeIndex+"-trafmon");
        srvConfig.setAppname("edge"+edgeIndex);
        srvConfig.setPorts("listen", Constants.edgeListenTopUpPort+edgeIndex, "response", Constants.edgeResponseTopUpPort+edgeIndex, "trafmon", Constants.edgeTrafMonTopUpPort+edgeIndex);
        
        try {
        	
        	File file = new File(Constants.yamlPath+edgeIndex+".yaml");
            FileWriter fr = null;

		    fr = new FileWriter(file);
		    fr.write(podConfig.toString());
		    fr.write(volDef.toStringVol());
		    fr.write(volDef.toStringNodeSel());
		    fr.write(srvConfig.toString()); 
		    
		    fr.close();
                    
        } 
        catch (IOException e) {
                e.printStackTrace();
        }
	}
	
	/**
	 * creates .yaml file for servicedistributor deployment on orchestrator (master) node
	 */
	public static void creatSDYaml() {
		
		PodDefinition podConfig = new PodDefinition();
        podConfig.setPodname("epos");
        podConfig.setAppname("epos"); 
        podConfig.setNumReplica(1); 
        podConfig.setMyregistrykey(Constants.Secret) ;
        podConfig.setContainerName("epos");
        podConfig.setImageName(Constants.ContainerImagesPath+Constants.ServiceDistributorImage);
        podConfig.setContainerPort(Constants.listenSrvDis, Constants.resSrvDis);
        
        
        VolumDef volDef = new VolumDef();
        volDef.setConfigMap(Constants.configMap);
        volDef.setMountPath(Constants.sdMountPath);
        volDef.setSubPath(Constants.SubPath);
        volDef.setNodeSelector("master");
        
        
        ServiceDef srvConfig = new ServiceDef();
        srvConfig.setSrvname("epospro","eposcon");
        srvConfig.setAppname("epos");
        srvConfig.setPorts("listen", Constants.listenSrvDis, "response", Constants.resSrvDis);
        
        
        try {
        	
        	
            File file = new File(Constants.yamlPath+"sd.yaml");
            FileWriter fr = null;

		    fr = new FileWriter(file);
		    fr.write(podConfig.toString());
		    fr.write(volDef.toStringVol());
		    fr.write(volDef.toStringNodeSel());
		    fr.write(srvConfig.toString()); 
		    
		    fr.close();
                    
        } 
        catch (IOException e) {
                e.printStackTrace();
        }
	}

	/**
	 * @param sorted
	 * @param experiment
	 * @param edgeDevices
	 * Writes the measurements of cpu/memory load over edge infrastructure
	 */
	public static void writeLoad(ArrayList<Load> experiment, List<EdgeNode> edgeDevices) {


			double currCpuLoad, currMemLoad;
			try {
			      PrintWriter out = new PrintWriter(new FileWriter(Constants.loadFile+"cpuload.dat"));
			      PrintWriter out1 = new PrintWriter(new FileWriter(Constants.loadFile+"memload.dat"));
			      String str = ""; String str1 = "";
			      
			    for (int i = 0; i < experiment.size(); i++) {
			    	str = ""; str1 = "";
			    	str += (int) experiment.get(i).time+" ";
					str1 += (int) experiment.get(i).time+" ";
					
					for (int j = 0; j<Constants.numEdgeNodes; j++) {
							
								currCpuLoad = (double) experiment.get(i).cpuload[j]/edgeDevices.get(j).CPU;
								currMemLoad = (double) experiment.get(i).memload[j]/edgeDevices.get(j).Memory;
								
							str += currCpuLoad+" ";
							str1 += currMemLoad+" ";
							
							
						}
						out.println(str);
						out1.println(str1);
						
						
					}
					 out.close(); out1.close();
				    }
					catch (IOException e) {
				      System.out.print("Error: " + e);
				      System.exit(1);
				    }
			
		
		
	}

	/**
	 * cleans log directories in ~/Documents/output/deprel/ up and make them ready for new logging
	 */
	public static void cleanup() {
		String line;
		try {
			 
			  String ShCommand = "bash " + Constants.updateBash;
			  Process p = Runtime.getRuntime().exec(ShCommand);
			  p.waitFor(); BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); BufferedReader errorReader = new
			  BufferedReader(new InputStreamReader(p.getErrorStream()));
			  
			  BufferedWriter bw = new BufferedWriter(new
			  OutputStreamWriter(p.getOutputStream()));
			  
			   line = "";
			  
			  while ((line = reader.readLine()) != null) {
				  System.out.println(line); 
			  }
			  
			  line = ""; while ((line = errorReader.readLine()) != null) {
				  System.out.println(line); 
			  }

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
	}

	/**
	 * downloads deployment and release updates from EdgeAgents and servicedistributor
	 */
	public static void downloadRelDep() {
		
		String line;
		
		try {
			String ShCommand = "bash " + Constants.outBash + Constants.filePath;
			Process p = Runtime.getRuntime().exec(ShCommand);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			line = "";

			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			line = "";
			while ((line = errorReader.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @param run
	 * @param experiment
	 * @param edgeDevices
	 * @param deployed
	 * @param released
	 * @param vehicleagents
	 * @param vehagentid
	 * based on the log files of EdgeAgents deploys or releases deployments for services and vehicleagents
	 */
	public static void DeployRelease(int run, ArrayList<Load> experiment, List<EdgeNode> edgeDevices, HashSet<String> deployed, HashSet<String> released, List<MobileDevice> vehicleagents, Map<Integer, Integer> vehagentid) {
		BufferedReader csvInRelease;
		BufferedReader csvInDeploy;
		String line, line1, line2;
		try {	
			
			for (int i = 0 ; i<Constants.numEdgeNodes; i++) {
				System.out.println("run:"+run+" retrieve data from edgeagent"+i);
				csvInDeploy = new BufferedReader(new FileReader(Constants.homePath + "/Documents/output/deprel/edge"+i+"/deploy.csv"));
				
				while((line1 = csvInDeploy.readLine()) != null) {
					String[] deployment = line1.split(",");
					if (!deployed.contains(deployment[0])) {
						
						experiment.get(run).addLoad(edgeDevices.get(Integer.parseInt(deployment[1])), vehicleagents.get(vehagentid.get(Integer.parseInt(deployment[0]))));
						//System.out.println("Deploying new service for vehicleagent "+deployment[0]+" "+Integer.parseInt(deployment[1])+" "+vehagentid.get(Integer.parseInt(deployment[0]))+" ");
						//10000 0 0
						//10001 1 1
							String ShCommand = "bash "+Constants.srvDeployScript+" edge"+i+" "+deployment[0];
						    Process p = Runtime.getRuntime().exec(ShCommand);
						    p.waitFor();
						    
						    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				
						    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
						    line = "";
						    
						    while ((line = reader.readLine()) != null) {
						        System.out.println(line);
						    }
				
						    line = "";
						    while ((line = errorReader.readLine()) != null) {
						        System.out.println(line);
						    }
						    
						    deployed.add(deployment[0]);
					}
				}
				
				csvInDeploy.close();
				csvInRelease = new BufferedReader(new FileReader(Constants.homePath + "/Documents/output/deprel/edge"+i+"/release.csv"));
				while((line2 = csvInRelease.readLine()) != null) {
						String[] deployment = line2.split(",");
						if (!released.contains(deployment[0])) {
						//System.out.println("Releasing service for vehicle agent "+deployment[0]+" "+deployment[0].substring(0, 3));
						
						if(deployment[0].substring(0, 3).compareTo("srv") == 0) {
							experiment.get(run).deducLoad(edgeDevices.get(Integer.parseInt(deployment[1])), vehicleagents.get(vehagentid.get(Integer.parseInt(deployment[0].substring(3, 8)))));
							//System.out.println("Releasing service for vehicle agent "+deployment[0]+" "+Integer.parseInt(deployment[1])+" "+vehagentid.get(Integer.parseInt(deployment[0].substring(3, 8))));
						}
						String ShCommand = "bash "+Constants.srvReleaseScript+deployment[0];
					    Process p = Runtime.getRuntime().exec(ShCommand);
					    p.waitFor();
					    
					    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					    BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
					    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
					    line = "";
					    
					    while ((line = reader.readLine()) != null) {
					        System.out.println(line);
					    }
			
					    line = "";
					    while ((line = errorReader.readLine()) != null) {
					        System.out.println(line);
					    }
					    released.add(deployment[0]);
				}
				
				
			}
				
				csvInRelease.close();
		    } 
			}
			catch (IOException e) {
				
			} 
			catch (InterruptedException e) {
				
		}
		
	}
	
	/**
	 * checks and release all the services running on edge nodes
	 */
	static void releaseResources() {
		try {
			  String ShCommand = "bash " + Constants.downBash;
			  Process p = Runtime.getRuntime().exec(ShCommand);
			  p.waitFor(); BufferedReader reader = new BufferedReader(new
			  InputStreamReader(p.getInputStream())); BufferedReader errorReader = new
			  BufferedReader(new InputStreamReader(p.getErrorStream()));
			  
			  BufferedWriter bw = new BufferedWriter(new
			  OutputStreamWriter(p.getOutputStream())); 
			  
			  String line = "";
			  
			  while ((line = reader.readLine()) != null) {
				  System.out.println(line); 
				  }
			  
			  line = ""; 
			  while ((line = errorReader.readLine()) != null) {
			  System.out.println(line); 
			  }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
		  
	}

	/**
	 * Creates configmap.yaml from input configuration json file to make it available for all the modules of SMOTEC
	 */
	public static void create_configmap() {
		
		//Instantiating the Scanner class to read the file
	      Scanner sc; String str;
	      try {
	    	  	sc = new Scanner(new File(Constants.cityconfigfile));
                BufferedWriter out = new BufferedWriter(new FileWriter(Constants.filePath + "/src/deployments/testbedconfig.yaml"));
				  
				      //instantiating the StringBuffer class
				      StringBuffer buffer = new StringBuffer();
				      //Reading lines of the file and appending them to StringBuffer
				     
				      str = "apiVersion: v1\n"
				      		+ "kind: ConfigMap\n"
				      		+ "metadata:\n"
				      		+ "  name: testbedconfig\n"
				      		+ "data:\n"
				      		+ "  TestbedConfig.json: |-\n";
				      		
				      out.append(str);
				      
				      
				      while (sc.hasNextLine()) {
					         buffer.append("       "+sc.nextLine()+System.lineSeparator());
					      }
					      
					      String fileContents = buffer.toString();
					   out.append(fileContents);
				    		 
				      out.close(); 
					
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
	}

}
