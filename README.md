# SMOTEC
SMOTEC is an open-source edge computing testbed for smart mobility experimentation. The testbed offers multiple components as Testbed, EdgeAgent, MobileAgent, Service, and ServiceDistributor and relies on open-source technologies such as K3s, Docker, ZeroMQ, EPOS, Grafana/Prometheus, Java, and SUMO.

The testbed uses the light version of Kubernetes, K3s, as its management layer. For conducting experiments with SMOTEC you need a K3s (or K8s) cluster including at least one master (orchestrator) node and two worker nodes. An orchestrator is defined as a host running the k3s server command, with control-plane and datastore components managed by K3s. We built SMOTEC using low-cost Raspberry Pis, which can be deployed easily at large-scale, Desktop machines, and servers. These machines are connected to the orchestrator by joining the K3s cluster as worker nodes.

If you do not have a configured Kubernetes cluster follow the instructions of SMOTEC documentation available in this repository to setup one and then continue with this installation guidance. All the commands explained here are run on orchestrator machine.

## Installation and setup
Download the Testbed source code and output directory. Put the Testbed and output in the home directory and /root/Documents, respectively.

Make sure that a version of Oracle Java 17 is installed. You can download it from [here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

Make sure K3s is up and running by running the following command in orchestrator terminal:

$sudo systemctl status k3s.service

$sudo kubectl get nodes

Remove sudo permission for calling k3s API:

$sudo chmod 644 /etc/rancher/k3s/k3s.yaml

## Config SMOTEC
Config parameters for a traffic monitoring experiment are available in Testbed/Conf/TestbedConfig.json with the default values for a 3-Node infrastructure including two edge nodes and one orchestrator and 10 mobile nodes.

You may change the parameters based on the requitrements of your experiment such as the number of edge nodes and their characteristics (e.g., location, coverage range, processing power, and memory), the number of mobile nodes and their service requirements (e.g., processing power and memory), Name and path to the testbed Docker images (images for MobileAgent, EdgeAgent, ServiceDistributor, and Service), EPOS simulation settings, etc.

SMOTEC utilizes the node lables in kubernetes cluster for identifying nodes for service placement. For this, you need to update testbedConfig.json based on your edge infrastructure:

Check the label of your edge nodes:
$ kubectl get nodes --show-labels

SMOTEC uses the label/value pair with the label "nn" to deploy a container image on an edge node. Choose your nodes you want to be part of your edge infrastructure one by one, and add a label to them by running the following command:
$ kubectl label nodes node-name nn="your-label"

Update the config file by editing the values for "NodeLabel" and "Orchestrator" according to the label values you used in previous command. The node identified as the "Orchestrator" will run service distributor and mobile agents. The remaining nodes identified by "NodeLabel" will be used as the edge infrastructure to run edge agents and user services.

## Run SMOTEC
Run from command line. Navigate to the Testbed directory and execute:

java -jar Testbed.jar <"path to TestbedConfig.json">

Prometheus/Grafana can be used for visualizing the status of edge resources and services during the experiment life-time.

## Results
After running an experiment, the output results are available in /root/Documents/output directory. It contains:

The output of service distributor module is available in "epos-out" directory.

Resource utilization output from the edge nodes in ".csv" format.

A copy of service placement plans selected by EPOS for every edge node is available in "S-plans".

Resource allocations (Deployments) and deallocations (releases) performed by Testbed and K3s are listed in "deprel" directory, with the corresponding deployment files in "deployment" directory.

Datasets of service placement plans for every edge node are in "Datasets" directory.

## How to Extend SMOTEC
For contributing to this testbed download the modules in this repository import them into your java IDE to start.

Follow the documentation available in this repository for more information on SMOTEC source code, Docker images, and how to extend the testbed.

Docker images of this testbed are publicly available on [Docker Hub](https://hub.docker.com/repository/docker/zeinabne/smotec/).

Already configured virtual machines with SMOTEC are also available on [Zenodo](https://doi.org/10.5281/zenodo.8167871)
