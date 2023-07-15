#!/bin/bash
echo "----------------------------------------"
echo "deploying service $2 on node $1"
cd ~/Documents/output/deployments/$1
kubectl apply -f deployment$2.yaml --namespace smotec
