#!/bin/bash
set -e
./scripts/runDSLabCluster.sh 60 0 10 15 65536 [0]&& \
java StatsReaderClients.java 150 60 && \
./scripts/runDSLabCluster.sh 60 0 10 15 512000 [0]&& \
java StatsReaderClients.java 150 60 && \
./scripts/runDSLabCluster.sh 60 0 10 15 1048576 [0]&& \
java StatsReaderClients.java 150 60 && \
./scripts/runDSLabCluster.sh 60 0 20 15 65536 [0]&& \
java StatsReaderClients.java 300 60 && \
./scripts/runDSLabCluster.sh 60 0 20 15 512000 [0]&& \
java StatsReaderClients.java 300 60 && \
./scripts/runDSLabCluster.sh 60 0 20 15 1048576 [0]&& \
java StatsReaderClients.java 300 60 && \
cd .. && \
cd byzcast-old && \
./scripts/runDSLabCluster.sh 60 0 10 15 65536 [0]&& \
java StatsReaderClients.java 150 60 && \
./scripts/runDSLabCluster.sh 60 0 10 15 512000 [0]&& \
java StatsReaderClients.java 150 60 && \
./scripts/runDSLabCluster.sh 60 0 10 15 1048576 [0]&& \
java StatsReaderClients.java 150 60 && \
./scripts/runDSLabCluster.sh 60 0 20 15 65536 [0]&& \
java StatsReaderClients.java 300 60 && \
./scripts/runDSLabCluster.sh 60 0 20 15 512000 [0]&& \
java StatsReaderClients.java 300 60 && \
./scripts/runDSLabCluster.sh 60 0 20 15 1048576 [0]&& \
java StatsReaderClients.java 300 60 
# ./scripts/runDSLabCluster.sh 120 0 4 15 65536&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 120 0 6 15 65536&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 120 0 8 15 65536&& \
# java StatsReaderClients.java
# ./scripts/runDSLabCluster.sh 120 0 4 15 1048576&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 120 0 6 15 1048576 && \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 120 0 8 15 1048576&& \
# java StatsReaderClients.java
# cd .. && \
# cd byzcast-old && \
# ./scripts/runDSLabCluster.sh 120 0 4 15 65536&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 120 0 6 15 65536&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 120 0 8 15 65536&& \
# java StatsReaderClients.java
# ./scripts/runDSLabCluster.sh 120 0 4 15 1048576&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 120 0 6 15 1048576 && \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 120 0 8 15 1048576&& \
# java StatsReaderClients.java
