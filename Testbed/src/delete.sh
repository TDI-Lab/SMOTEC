#!/bin/bash
echo "Deleting resouces/services"
kubectl delete service --all -n smotec
kubectl delete deployment --all -n smotec
kubectl delete configmap testbedconfig --namespace smotec




