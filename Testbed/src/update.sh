#!/bin/bash
rm -r ~/Documents/output/deployments/*
cd ~/Documents/output/deprel/edge0
>deploy.csv
>release.csv
cd ~/Documents/output/deprel/edge1
>deploy.csv
>release.csv
echo "update done!"
