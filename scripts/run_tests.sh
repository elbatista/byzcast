#!/bin/bash

# # for i in {29..70}; do
# #     echo "==== node$i ===="
# #     ssh -o ConnectTimeout=3 \
# #         -o BatchMode=yes \
# #         node$i "hostname" 2>&1
# # done

set -e

CLIENTS=(50 70) #2 10 20 30 50 70   30 50 70

# fazer 50 70 só para VarCliSeq

run_tests () {
    MODE=$1
    LABEL=$2
    SIZE_BYTES=$3
    SIZE_LABEL=$4
    NUM_DESTS=$5

    for c in "${CLIENTS[@]}"; do
        ./scripts/runDSLabCluster.sh 60 "$MODE" "$c" 15 "$SIZE_BYTES" [0]

        java StatsReaderClients.java  "$c" 60 "$LABEL" "$SIZE_LABEL" "$NUM_DESTS"
    done
}

# byzcast atual
# run_tests 0 LCA 65536 64 4
# run_tests 1 VarCliSeq 65536 64 4
# run_tests 2 VarCliPar 65536 64 4

# run_tests 0 LCA 512000 512 4
# run_tests 1 VarCliSeq 512000 512 4
# run_tests 2 VarCliPar 512000 512 4

# run_tests 0 LCA 1048576 1024 4
run_tests 1 VarCliSeq 1048576 1024 4
# run_tests 2 VarCliPar 1048576 1024 4

# versão antiga
# cd ../byzcast-old

# run_tests 0 Origin 65536 64 4
# run_tests 0 Origin 512000 512 4
# run_tests 0 Origin 1048576 1024 4

#LIMITACOES:
# Em Var/Original com 3 destinos, 65536kb nao funciona com 70 clientes pois da o erro infinito.
# Mas VarCliSeq funciona com 70 clientes e 65536kb destinos, o que é estranho.
# Em Var/Original com 3 destinos, 1048576kb nao funciona com 30 clientes pois estoura a memoria.

# set -e
# ./scripts/runDSLabCluster.sh 60 0 2 15 65536 [0]&& \
# java StatsReaderClients.java 2 60 Var 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 65536 [0]&& \
# java StatsReaderClients.java 10 60 Var 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 65536 [0]&& \
# java StatsReaderClients.java 20 60 Var 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 30 15 65536 [0]&& \
# java StatsReaderClients.java 30 60 Var 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 50 15 65536 [0]&& \
# java StatsReaderClients.java 50 60 Var 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 70 15 65536 [0]&& \
# java StatsReaderClients.java 70 60 Var 64 2 && \

# ./scripts/runDSLabCluster.sh 60 1 2 15 65536 [0]&& \
# java StatsReaderClients.java 2 60 VarCliSeq 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 10 15 65536 [0]&& \
# java StatsReaderClients.java 10 60 VarCliSeq 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 20 15 65536 [0]&& \
# java StatsReaderClients.java 20 60 VarCliSeq 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 30 15 65536 [0]&& \
# java StatsReaderClients.java 30 60 VarCliSeq 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 50 15 65536 [0]&& \
# java StatsReaderClients.java 50 60 VarCliSeq 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 70 15 65536 [0]&& \
# java StatsReaderClients.java 70 60 VarCliSeq 64 2 && \


# ./scripts/runDSLabCluster.sh 60 1 2 15 512000 [0]&& \
# java StatsReaderClients.java 2 60 VarCliSeq 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 10 15 512000 [0]&& \
# java StatsReaderClients.java 10 60 VarCliSeq 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 20 15 512000 [0]&& \
# java StatsReaderClients.java 20 60 VarCliSeq 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 30 15 512000 [0]&& \
# java StatsReaderClients.java 30 60 VarCliSeq 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 50 15 512000 [0]&& \
# java StatsReaderClients.java 50 60 VarCliSeq 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 70 15 512000 [0]&& \
# java StatsReaderClients.java 70 60 VarCliSeq 512 2 && \

# ./scripts/runDSLabCluster.sh 60 0 2 15 512000 [0]&& \
# java StatsReaderClients.java 2 60 Var 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 512000 [0]&& \
# java StatsReaderClients.java 10 60 Var 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 512000 [0]&& \
# java StatsReaderClients.java 20 60 Var 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 30 15 512000 [0]&& \
# java StatsReaderClients.java 30 60 Var 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 50 15 512000 [0]&& \
# java StatsReaderClients.java 50 60 Var 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 70 15 512000 [0]&& \
# java StatsReaderClients.java 70 60 Var 512 2 && \


# ./scripts/runDSLabCluster.sh 60 0 2 15 1048576 [0]&& \
# java StatsReaderClients.java 2 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 30 15 1048576 [0]&& \
# java StatsReaderClients.java 30 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 50 15 1048576 [0]&& \
# java StatsReaderClients.java 50 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 70 15 1048576 [0]&& \
# java StatsReaderClients.java 70 60 Var 1024 2 && \

# ./scripts/runDSLabCluster.sh 60 1 2 15 1048576 [0]&& \
# java StatsReaderClients.java 2 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 30 15 1048576 [0]&& \
# java StatsReaderClients.java 30 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 50 15 1048576 [0]&& \
# java StatsReaderClients.java 50 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 70 15 1048576 [0]&& \
# java StatsReaderClients.java 70 60 VarCliSeq 1024 2 && \

# cd .. && \
# cd byzcast-old && \
# ./scripts/runDSLabCluster.sh 60 0 2 15 65536 [0]&& \
# java StatsReaderClients.java 2 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 65536 [0]&& \
# java StatsReaderClients.java 10 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 65536 [0]&& \
# java StatsReaderClients.java 20 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 30 15 65536 [0]&& \
# java StatsReaderClients.java 30 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 50 15 65536 [0]&& \
# java StatsReaderClients.java 50 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 70 15 65536 [0]&& \
# java StatsReaderClients.java 70 60 Origin 64 2 && \

# ./scripts/runDSLabCluster.sh 60 0 2 15 512000 [0]&& \
# java StatsReaderClients.java 2 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 512000 [0]&& \
# java StatsReaderClients.java 10 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 512000 [0]&& \
# java StatsReaderClients.java 20 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 30 15 512000 [0]&& \
# java StatsReaderClients.java 30 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 50 15 512000 [0]&& \
# java StatsReaderClients.java 50 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 70 15 512000 [0]&& \
# java StatsReaderClients.java 70 60 Origin 512 2 && \

# ./scripts/runDSLabCluster.sh 60 0 2 15 1048576 [0]&& \
# java StatsReaderClients.java 2 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 30 15 1048576 [0]&& \
# java StatsReaderClients.java 30 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 50 15 1048576 [0]&& \
# java StatsReaderClients.java 50 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 70 15 1048576 [0]&& \
# java StatsReaderClients.java 70 60 Origin 1024 2 




# set -e
# ./scripts/runDSLabCluster.sh 60 0 2 15 1048576 [0]&& \
# java StatsReaderClients.java 2 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 30 15 1048576 [0]&& \
# java StatsReaderClients.java 30 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 50 15 1048576 [0]&& \
# java StatsReaderClients.java 50 60 Var 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 70 15 1048576 [0]&& \
# java StatsReaderClients.java 70 60 Var 1024 2 && \

# ./scripts/runDSLabCluster.sh 60 1 2 15 1048576 [0]&& \
# java StatsReaderClients.java 2 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 30 15 1048576 [0]&& \
# java StatsReaderClients.java 30 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 50 15 1048576 [0]&& \
# java StatsReaderClients.java 50 60 VarCliSeq 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 70 15 1048576 [0]&& \
# java StatsReaderClients.java 70 60 VarCliSeq 1024 2 && \

# cd .. && \
# cd byzcast-old && \
# ./scripts/runDSLabCluster.sh 60 0 2 15 1048576 [0]&& \
# java StatsReaderClients.java 2 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 30 15 1048576 [0]&& \
# java StatsReaderClients.java 30 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 50 15 1048576 [0]&& \
# java StatsReaderClients.java 50 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 70 15 1048576 [0]&& \
# java StatsReaderClients.java 70 60 Origin 1024 2




# ./scripts/runDSLabCluster.sh 60 0 2 15 65536 [0]&& \
# java StatsReaderClients.java 2 60 Prot 64 4 && \
# ./scripts/runDSLabCluster.sh 60 0 2 15 512000 [0]&& \
# java StatsReaderClients.java 2 60 Prot 512 4 && \
# ./scripts/runDSLabCluster.sh 60 0 2 15 1048576 [0]&& \
# java StatsReaderClients.java 2 60 Prot 1024 4 && \

# ./scripts/runDSLabCluster.sh 60 0 5 15 65536 [0]&& \
# java StatsReaderClients.java 5 60 Prot 64 4 && \
# ./scripts/runDSLabCluster.sh 60 0 5 15 512000 [0]&& \
# java StatsReaderClients.java 5 60 Prot 512 4 && \
# ./scripts/runDSLabCluster.sh 60 0 5 15 1048576 [0]&& \
# java StatsReaderClients.java 5 60 Prot 1024 4 && \

# ./scripts/runDSLabCluster.sh 60 0 10 15 65536 [0]&& \
# java StatsReaderClients.java 10 60 Prot 64 4 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 512000 [0]&& \
# java StatsReaderClients.java 10 60 Prot 512 4 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 Prot 1024 4 && \

# ./scripts/runDSLabCluster.sh 60 0 20 15 65536 [0]&& \
# java StatsReaderClients.java 20 60 Prot 64 4 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 512000 [0]&& \
# java StatsReaderClients.java 20 60 Prot 512 4 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 Prot 1024 4 && \

# cd .. && \
# cd byzcast-old && \
# ./scripts/runDSLabCluster.sh 60 0 2 15 1048576 [0]&& \
# java StatsReaderClients.java 2 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 30 15 1048576 [0]&& \
# java StatsReaderClients.java 30 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 50 15 1048576 [0]&& \
# java StatsReaderClients.java 50 60 Origin 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 0 70 15 1048576 [0]&& \
# java StatsReaderClients.java 70 60 Origin 1024 2
# ./scripts/runDSLabCluster.sh 60 0 100 15 1048576 [0]&& \
# java StatsReaderClients.java 100 60 Origin 1024 2 
# ./scripts/runDSLabCluster.sh 60 0 120 15 65536 [0]&& \
# java StatsReaderClients.java 120 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 150 15 65536 [0]&& \
# java StatsReaderClients.java 150 60 Origin 64 2 

# ./scripts/runDSLabCluster.sh 60 0 1 15 65536 [0]&& \
# java StatsReaderClients.java 1 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 1 15 512000 [0]&& \
# java StatsReaderClients.java 1 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 1 15 1048576 [0]&& \
# java StatsReaderClients.java 1 60 Origin 1024 2 && \

# ./scripts/runDSLabCluster.sh 60 0 2 15 65536 [0]&& \
# java StatsReaderClients.java 2 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 2 15 512000 [0]&& \
# java StatsReaderClients.java 2 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 2 15 1048576 [0]&& \
# java StatsReaderClients.java 2 60 Origin 1024 2 && \

# ./scripts/runDSLabCluster.sh 60 0 5 15 65536 [0]&& \
# java StatsReaderClients.java 5 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 5 15 512000 [0]&& \
# java StatsReaderClients.java 5 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 5 15 1048576 [0]&& \
# java StatsReaderClients.java 5 60 Origin 1024 2 && \

# ./scripts/runDSLabCluster.sh 60 0 10 15 65536 [0]&& \
# java StatsReaderClients.java 10 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 512000 [0]&& \
# java StatsReaderClients.java 10 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 Origin 1024 2 && \

# ./scripts/runDSLabCluster.sh 60 0 20 15 65536 [0]&& \
# java StatsReaderClients.java 20 60 Origin 64 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 512000 [0]&& \
# java StatsReaderClients.java 20 60 Origin 512 2 && \
# ./scripts/runDSLabCluster.sh 60 0 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 Origin 1024 2