#!/bin/bash
echo "releasing resources"
echo "command: $0"
echo "First arg: $1"
kubectl delete deployment $1
