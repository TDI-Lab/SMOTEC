#!/bin/bash
echo "creating agents"
echo "command: $0"
echo "First arg: $1"
echo "Second arg: $2"
j="1"
for (( i=1 ; i<=$1 ; i++ ))
do
	cd /home/spring/Documents/Testbed/src/deployments
	cat deployment${j}.yaml
	echo $i;
done