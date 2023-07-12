#!/bin/bash
echo "creating agents"
echo "command: $0"
echo "Creating Agent: $1"
echo "base address: $2"
cd $2/src/deployments
kubectl apply -f deployment$1.yaml --namespace smotec
