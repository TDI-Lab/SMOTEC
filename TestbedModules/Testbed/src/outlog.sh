#!/bin/bash
echo "----------------------------------------------------------"
echo "retrieving output from service distributor and edge nodes:"
kubectl cp smotec/$(kubectl get pods -o=name -n smotec |grep epos | sed "s/^.\{4\}//"):/tmp/S_plans ~/Documents/output/S_plans
kubectl cp smotec/$(kubectl get pods -o=name -n smotec |grep epos | sed "s/^.\{4\}//"):/tmp/output ~/Documents/output/epos-out
kubectl cp smotec/$(kubectl get pods -o=name -n smotec |grep edge0 | sed "s/^.\{4\}//"):/tmp/datasets/ ~/Documents/output/datasets/edge0
kubectl cp smotec/$(kubectl get pods -o=name -n smotec |grep edge0 | sed "s/^.\{4\}//"):/tmp/src/deployments/ ~/Documents/output/deployments/edge0
kubectl cp smotec/$(kubectl get pods -o=name -n smotec |grep edge0 | sed "s/^.\{4\}//"):/tmp/output/ ~/Documents/output/deprel/edge0
kubectl cp smotec/$(kubectl get pods -o=name -n smotec |grep edge1 | sed "s/^.\{4\}//"):/tmp/datasets/ ~/Documents/output/datasets/edge1
kubectl cp smotec/$(kubectl get pods -o=name -n smotec |grep edge1 | sed "s/^.\{4\}//"):/tmp/src/deployments/ ~/Documents/output/deployments/edge1
kubectl cp smotec/$(kubectl get pods -o=name -n smotec |grep edge1 | sed "s/^.\{4\}//"):/tmp/output/ ~/Documents/output/deprel/edge1