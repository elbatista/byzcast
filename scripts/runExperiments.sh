#!/bin/bash

duration=30;
algos=(0);
clispernode=(20 40 70);
payloadSizes=(64)
servers=15;

# 51200  50KB
# 102400 100KB
# 512000 500KB
# Usage: ./scripts/runDSLabCluster.sh <duration:sec> <algo:0-ByzCast;1-Disseminator> <#clispernode> <#servers> <payload size>

for a in "${algos[@]}"
do
  for ps in "${payloadSizes[@]}"
  do
    for c in "${clispernode[@]}"
    do
        ./scripts/runDSLabCluster.sh $duration $a $c $servers $ps
    done
  done
done
