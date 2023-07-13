#!/bin/bash
cd $1/src/deployments
kubectl create namespace smotec
kubectl apply -f testbedconfig.yaml --namespace smotec
kubectl apply -f deploymentsd.yaml --namespace smotec
