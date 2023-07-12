package pi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author zeinab
 *
 */
public class Utility {

	static String line = "";
	static String cvsSplitBy = ",";
	static int edgeAgentId;

	/**
	 * @param agentPlans writes binary/utilization plans generated as service
	 *                   placement plans
	 */
	public static void writePlans(Plan[] agentPlans) {
		edgeAgentId = Constants.edgeAgentIndex;
		writeUtilizationToFile(agentPlans, edgeAgentId);
		writeBinaryToFile(agentPlans, edgeAgentId);

	}

	/**
	 * @param plans
	 * @param id    writes utilization values of edge nodes associated with every
	 *              binary plan
	 */
	public static void writeUtilizationToFile(Plan[] plans, int id) {
		boolean append_value = false;

		try {
			File dir = new File(new File("").getAbsolutePath() + "/datasets/Utilization");

			String file_path_EPOS = dir + "/agent_" + id + ".plans";
			FileWriter writer_EPOS = new FileWriter(file_path_EPOS, append_value);

			for (int col = 0; col < Constants.EPOS_NUM_PLANS; col++) {
				writer_EPOS.append(String.valueOf(plans[col].getCosts()));
				writer_EPOS.append(":");
				int size = plans[col].utilPlan.length;
				for (int i = 0; i < size; i++) {// both of CPU and Mem will be printed
					writer_EPOS.write(String.format("%.12f", plans[col].utilPlan[i]));// prevents wrong format of values
																						// as negative values
					if (i != size - 1) {
						writer_EPOS.append(",");
					}
				}
				writer_EPOS.append("\r\n");
			}
			writer_EPOS.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * @param plans
	 * @param id    writes binary values which represents request to host mappings
	 *              for received requests
	 */
	private static void writeBinaryToFile(Plan[] plans, int id) {
		boolean append_value = false;
		System.out.println("Writing Binary Plans...");

		try {
			File dir = new File(new File("").getAbsolutePath() + "/datasets/Binary");
			String file_path_EPOS = dir + "/agent_" + id + ".plans";
			FileWriter writer_EPOS = new FileWriter(file_path_EPOS, append_value);
			System.out.println(" dir " + file_path_EPOS);

			for (int col = 0; col < Constants.EPOS_NUM_PLANS; col++) {
				if (plans[col].empty_node == true) {
					writer_EPOS.write(String.format("node without any request"));
					break;
				}

				int size = plans[col].y.length;
				System.out.println("Writing Binary Plans with the dimension: col " + col + " " + plans[col].y.length);
				for (int i = 0; i < size; i++) {
					writer_EPOS.write(String.format(String.valueOf(plans[col].y[i])));
					System.out.println("i " + i + " " + plans[col].y[i]);

					if (i != plans[col].y.length - 1) {
						writer_EPOS.append(",");
					}
				}

				writer_EPOS.append("\r\n");
			}
			writer_EPOS.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}


	/**
	 * @param i
	 * @param reqId
	 * @param request
	 * @param host
	 * create deployment file for service placement for deploying the request on host node
	 */
	public static void creatYaml(int i, int reqId, Request request, int host, String hostLabel) {
		
		PodDefinition podConfig = new PodDefinition();
		podConfig.setPodname("srv" + reqId);
		podConfig.setAppname("srv" + reqId);
		podConfig.setNumReplica(1);
		podConfig.setMyregistrykey("my-registry-key");
		podConfig.setContainerName("srv" + reqId);
		podConfig.setImageName(Constants.containerImagesPath + Constants.ServiceName);
		podConfig.setContainerPort(reqId);
		podConfig.setCpuDemand(request.getCPU());// set cpu resource demand
		podConfig.setMemDemand(request.getMemory());// set memory resource demand
		podConfig.setId(reqId);
		podConfig.setHostId(host);

		HostNodDef volDef = new HostNodDef();
		volDef.setNodeSelector(hostLabel);

		ServiceDef srvConfig = new ServiceDef();
		srvConfig.setSrvname("srv" + reqId + "-service");
		srvConfig.setAppname("srv" + reqId);
		srvConfig.setPort(reqId);
		srvConfig.setTargetPort(reqId);
		srvConfig.setNodePort(reqId);
		
		try {

			File file = new File(Constants.deployDir + "deployment" + reqId + ".yaml");
			FileWriter fr = null;

			fr = new FileWriter(file);
			fr.write(podConfig.toString());
			fr.write(volDef.toStringSrv());
			fr.write(srvConfig.toString());
			fr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param simulationRequests
	 * @param maxTime
	 * writes experiment results 
	 * not used yet, for later
	 */
//	public static void WriteResults(List<SRequest> simulationRequests, int maxTime) {
//		// TODO Auto-generated method stub
//		String CSV_SEPARATOR_To_Write = ",";
//
//		try {
//			BufferedWriter csvWriter = new BufferedWriter(
//					new FileWriter(Constants.bp + "/output/conn-" + Constants.edgeAgentIndex + "-" + maxTime + ".csv"));
//			for (int i = 0; i < simulationRequests.size(); i++) {
//				csvWriter.append(simulationRequests.get(i).time + "").append(CSV_SEPARATOR_To_Write)
//						.append(simulationRequests.get(i).conrequests.size() + "").append(CSV_SEPARATOR_To_Write);
//				for (int j = 0; j < simulationRequests.get(i).conrequests.size(); j++) {
//					csvWriter.append(simulationRequests.get(i).conrequests.get(j).vehAgentId + "")
//							.append(CSV_SEPARATOR_To_Write);
//					csvWriter.append(simulationRequests.get(i).conrequests.get(j).hostId + "")
//							.append(CSV_SEPARATOR_To_Write);
//				}
//				csvWriter.append(System.lineSeparator());
//			}
//
//			csvWriter.flush();
//			csvWriter.close();
//
//		} catch (Exception e) {
//			System.out.println("Error in FileReader !!!");
//			e.printStackTrace();
//		}
//
//		try {
//			BufferedWriter csvWriter = new BufferedWriter(new FileWriter(
//					Constants.bp + "/output/disconn-" + Constants.edgeAgentIndex + "-" + maxTime + ".csv"));
//			for (int i = 0; i < simulationRequests.size(); i++) {
//				csvWriter.append(simulationRequests.get(i).time + "").append(CSV_SEPARATOR_To_Write)
//						.append(simulationRequests.get(i).disrequests.size() + "").append(CSV_SEPARATOR_To_Write);
//				for (int j = 0; j < simulationRequests.get(i).disrequests.size(); j++) {
//					csvWriter.append(simulationRequests.get(i).disrequests.get(j).vehAgentId + "")
//							.append(CSV_SEPARATOR_To_Write);
//					// csvWriter.append(simulationRequests.get(i).disrequests.get(j).hostId+"").append(CSV_SEPARATOR_To_Write);
//				}
//				csvWriter.append(System.lineSeparator());
//			}
//
//			csvWriter.flush();
//			csvWriter.close();
//
//		} catch (Exception e) {
//			System.out.println("Error in FileReader !!!");
//			e.printStackTrace();
//		}
//
//		try {
//			BufferedWriter csvWriter = new BufferedWriter(new FileWriter(
//					Constants.bp + "/output/reconn-" + Constants.edgeAgentIndex + "-" + maxTime + ".csv"));
//			for (int i = 0; i < simulationRequests.size(); i++) {
//				csvWriter.append(simulationRequests.get(i).time + "").append(CSV_SEPARATOR_To_Write)
//						.append(simulationRequests.get(i).recrequests.size() + "").append(CSV_SEPARATOR_To_Write);
//				for (int j = 0; j < simulationRequests.get(i).recrequests.size(); j++) {
//					csvWriter.append(simulationRequests.get(i).recrequests.get(j).vehAgentId + "")
//							.append(CSV_SEPARATOR_To_Write);
//					// csvWriter.append(simulationRequests.get(i).disrequests.get(j).hostId+"").append(CSV_SEPARATOR_To_Write);
//				}
//				csvWriter.append(System.lineSeparator());
//			}
//
//			csvWriter.flush();
//			csvWriter.close();
//
//		} catch (Exception e) {
//			System.out.println("Error in FileReader !!!");
//			e.printStackTrace();
//		}
//
//	}

	/**
	 * @param string
	 * @param file
	 * @param type
	 * @param host
	 * updates deployment and release files for utilizing by testbed and K3s
	 */
	public static void updateDepRel(String string, String file, int type, int host) {
		String CSV_SEPARATOR_To_Write = ",";
		FileWriter csvWriter = null;
		try {
			csvWriter = new FileWriter(Constants.bp + "/output/" + file + ".csv", true);
			if (type == 0) {
				csvWriter.append("agent" + string).append(CSV_SEPARATOR_To_Write)

						.append(System.lineSeparator());
				csvWriter.append("srv" + string).append(CSV_SEPARATOR_To_Write).append("" + host)
						.append(CSV_SEPARATOR_To_Write).append(System.lineSeparator());
			} else {
				csvWriter.append(string).append(CSV_SEPARATOR_To_Write).append(host + "").append(CSV_SEPARATOR_To_Write)
						.append(System.lineSeparator());
			}
			csvWriter.flush();
			csvWriter.close();

		} catch (Exception e) {
			System.out.println("Error in FileReader !!!");
			e.printStackTrace();
		}
	}

	/**
	 * read the edge nodes location and their resource characteristics
	 * 
	 * @param infrastructure
	 * @param neighbors
	 */
	static void read_neighbors(String infrastructure, List<Neighbor> neighbors) {
		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(new FileReader(infrastructure));

			JSONArray jsonObjects = (JSONArray) obj;

			for (Object o : jsonObjects) {
				JSONObject jsonObject = (JSONObject) o;
				int numNeighbors = (int) (long) jsonObject.get("NumEdgeNodes");

				JSONArray jsonArrayid = (JSONArray) jsonObject.get("EdgeId");
				JSONArray jsonArrayX = (JSONArray) jsonObject.get("Xpoints");
				JSONArray jsonArrayY = (JSONArray) jsonObject.get("Ypoints");
				JSONArray jsonArrayCPU = (JSONArray) jsonObject.get("CpuCap");
				JSONArray jsonArrayMem = (JSONArray) jsonObject.get("MemCap");
				JSONArray jsonArrayStorage = (JSONArray) jsonObject.get("StorageCap");
				JSONArray jsonArrayNodeLabel = (JSONArray) jsonObject.get("NodeLabel");
				
				Constants.containerImagesPath = (String) jsonObject.get("ContainerImagesPath");
				Constants.srvdisSrvListener = (String) jsonObject.get("SrvDisLi");// eposCon
				Constants.srvdisSrvProducer = (String) jsonObject.get("SrvDisRe");
				Constants.EPOS_NUM_PLANS = (int) (long) jsonObject.get("EPOSNumPlans");
				Constants.ServiceName = (String) jsonObject.get("ServiceName");

				for (int i = 0; i < numNeighbors; i++) {
					neighbors.add(i,
							new Neighbor((int) (long) (jsonArrayid.get(i)), (String) (jsonArrayNodeLabel.get(i)), (int) (long) (jsonArrayX.get(i)),
									(int) (long) (jsonArrayY.get(i)), (int) (long) (jsonArrayCPU.get(i)),
									(int) (long) (jsonArrayMem.get(i)), (int) (long) (jsonArrayStorage.get(i))));
				}
			}
			System.out.println("Number of edgeAgents in the network: " + neighbors.size()
					+ " Constants.containerImagesPath" + Constants.containerImagesPath);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	static void switchOff(String edgeAgent) {// release hosted agents also

		try {
			String ShCommand = "bash " + Constants.srvReleaseScript + " edge" + edgeAgent;
			Process p = Runtime.getRuntime().exec(ShCommand);
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

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

}
