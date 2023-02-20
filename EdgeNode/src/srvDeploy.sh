#!/bin/bash
echo "Deploying services"
echo "command: $0"
echo "First arg: $1"
echo "second arg: $2"
cd $1
cat deployment$2.yaml
kubectl apply -f deployment$2.yaml
