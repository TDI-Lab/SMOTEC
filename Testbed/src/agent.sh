#!/bin/bash
echo "creating agents"
echo "command: $0"
echo "Creating Agent: $1"
echo "CPU demand: $2"
echo "Memory demand: $3"
echo "Mobility profile: $4"
echo "base address: $5"
j="1"
cd $5/src/deployments
cat deployment$1.yaml
kubectl apply -f deployment$1.yaml
