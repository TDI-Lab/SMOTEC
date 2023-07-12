#!/bin/bash
echo "command: $0"
echo "creating Service Distributor:"
echo "with the deployment files path: $1"
cd $1/src/deployments
kubectl create namespace smotec
kubectl apply -f testbedconfig.yaml --namespace smotec
kubectl apply -f deploymentsd.yaml --namespace smotec
