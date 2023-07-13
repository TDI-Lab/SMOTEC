#!/bin/bash
echo "creating edge: $1"
cd $2/src/deployments
kubectl apply -f deployment$1.yaml --namespace smotec
