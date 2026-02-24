#!/bin/bash
set -e

./scripts/runDSLabCluster.sh 60 0 1 15 65536 [0]&& \
java StatsReaderClients.java && \
./scripts/runDSLabCluster.sh 60 0 1 15 512000 [0]&& \
java StatsReaderClients.java && \
./scripts/runDSLabCluster.sh 60 0 1 15 1048576 [0]&& \
java StatsReaderClients.java && \
cd .. && \
cd byzcast-old && \
./scripts/runDSLabCluster.sh 60 0 1 15 65536 [0]&& \
java StatsReaderClients.java && \
./scripts/runDSLabCluster.sh 60 0 1 15 512000 [0]&& \
java StatsReaderClients.java && \
./scripts/runDSLabCluster.sh 60 0 1 15 1048576 [0]&& \
java StatsReaderClients.java 
# ./scripts/runDSLabCluster.sh 60 0 4 15 65536&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 60 0 6 15 65536&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 60 0 8 15 65536&& \
# java StatsReaderClients.java
# ./scripts/runDSLabCluster.sh 60 0 4 15 1048576&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 60 0 6 15 1048576 && \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 60 0 8 15 1048576&& \
# java StatsReaderClients.java
# cd .. && \
# cd byzcast-old && \
# ./scripts/runDSLabCluster.sh 60 0 4 15 65536&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 60 0 6 15 65536&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 60 0 8 15 65536&& \
# java StatsReaderClients.java
# ./scripts/runDSLabCluster.sh 60 0 4 15 1048576&& \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 60 0 6 15 1048576 && \
# java StatsReaderClients.java && \
# ./scripts/runDSLabCluster.sh 60 0 8 15 1048576&& \
# java StatsReaderClients.java
