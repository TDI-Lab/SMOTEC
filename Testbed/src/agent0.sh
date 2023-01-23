#!/bin/bash
echo "creating agents"
echo "command: $0"
echo "First arg: $1"
echo "Second arg: $2"
echo "Third arg: $3"
j="1"
for (( i=0 ; i<$1 ; i++ ))
do
	cd /home/spring/Documents/Testbed/src/deployments
	cat deployment$i.yaml
	echo $i;
done