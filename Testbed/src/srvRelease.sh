
#!/bin/bash
echo "------------------------------------------------------------"
echo "command: $0"
echo "First arg: $1"
echo "releasing service/agent"
kubectl delete deployment $1 --namespace smotec
kubectl delete service $1-service --namespace smotec
