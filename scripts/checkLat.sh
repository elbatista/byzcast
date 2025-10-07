#!/usr/bin/env bash

# Usage: ./ping_pairs_ssh_once.sh [start_node] [end_node] [threshold_ms]
# Defaults:
#   start_node = 50
#   end_node = 80
#   threshold_ms = 1

start=${1:-50}
end=${2:-80}
threshold=${3:-1}
prefix="node"

echo "Checking inter-node latency from ${start} to ${end} with threshold ${threshold} ms"

for ((i=start; i<=end; i++)); do
  node_i="${prefix}${i}"
  echo "Connecting to ${node_i}..."

  for ((j=i+1; j<=end; j++)); do
    node_j="${prefix}${j}"

    # SSH into node_i and ping node_j once
    latency=$(ssh "$node_i" "ping -c 1 -W 1 $node_j" 2>/dev/null | grep 'time=' | awk -F'time=' '{print $2}' | awk '{print $1}')

    if [ -z "$latency" ]; then
      echo "No response: ${node_i} -> ${node_j}"
      continue
    fi

    latency_int=${latency%.*}
    if [ "$latency_int" -gt "$threshold" ]; then
      echo "High latency: ${node_i} -> ${node_j} : ${latency} ms"
    fi
  done
done
