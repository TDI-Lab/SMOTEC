
#!/bin/bash
echo "------------------------------------------------------------"
echo "deleting service $1 and its vehicle agent"
kubectl delete deployment $1 --namespace smotec
kubectl delete service $1-service --namespace smotec
