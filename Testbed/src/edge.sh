#!/bin/bash
echo "command: $0"
echo "creating edge: $1"
echo "with the deployment files path: $2"
echo "with the input file: $3"
kubectl get pods
cd $2/src/deployments
kubectl apply -f deployment$1.yaml
echo $1;
