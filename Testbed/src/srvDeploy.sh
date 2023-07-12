#!/bin/bash
echo "----------------------------------------"
echo "command: $0"
echo "First arg: $1"
echo "second arg: $2"
echo "deploying service"
cd ~/Documents/output/deployments/$1
kubectl apply -f deployment$2.yaml --namespace smotec
