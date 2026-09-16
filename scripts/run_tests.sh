#!/bin/bash

# # for i in {29..70}; do
# #     echo "==== node$i ===="
# #     ssh -o ConnectTimeout=3 \
# #         -o BatchMode=yes \
# #         node$i "hostname" 2>&1
# # done

set -e

CLIENTS=(2 10 20 30) #2 10 20 30 50 70   30 50 70

# fazer 50 70 só para var1

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
# run_tests 1 Var1 65536 64 2
run_tests 2 Var2 65536 64 2

run_tests 1 Var1 512000 512 2
run_tests 2 Var2 512000 512 2

run_tests 1 Var1 1048576 1024 2
run_tests 2 Var2 1048576 1024 2

# versão antiga
cd ../byzcast-old

run_tests 0 Origin 65536 64 2
run_tests 0 Origin 512000 512 2
run_tests 0 Origin 1048576 1024 2

#LIMITACOES:
# Em Var/Original com 3 destinos, 65536kb nao funciona com 70 clientes pois da o erro infinito.
# Mas Var1 funciona com 70 clientes e 65536kb destinos, o que é estranho.
# Em Var/Original com 3 destinos, 1048576kb nao funciona com 30 clientes pois estoura a memoria.

#Erro "aleatório": 
# BUILD SUCCESSFUL
# Total time: 3 seconds
# Exception in thread "main" java.lang.NullPointerException: Cannot invoke "io.netty.channel.Channel.writeAndFlush(Object)" because the return value of "java.util.HashMap.get(Object)" is null
#         at byzcast.proxies.ByzCastClientProxy.sendReadyMessage(ByzCastClientProxy.java:99)
#         at byzcast.ByzCastRandomClientVar1.start(ByzCastRandomClientVar1.java:100)
#         at byzcast.ByzCastRandomClientVar1.<init>(ByzCastRandomClientVar1.java:66)
#         at MainClient.main(MainClient.java:14)
# ^C

#Erro "aleatório" 2: 
# BUILD SUCCESSFUL
# Total time: 3 seconds
# Exception in thread "main" java.lang.IllegalStateException: Channel not initialized for peer 6
#         at byzcast.proxies.ByzCastClientProxy.sendInitMessage(ByzCastClientProxy.java:83)
#         at byzcast.ByzCastRandomClientVar1.start(ByzCastRandomClientVar1.java:95)
#         at byzcast.ByzCastRandomClientVar1.<init>(ByzCastRandomClientVar1.java:66)
#         at MainClient.main(MainClient.java:14)
# ^C


#Erro "infinito": 
# May 27, 2026 11:33:31 PM io.netty.channel.AbstractChannel$AbstractUnsafe register
# WARNING: Force-closing a channel whose registration task was not accepted by an event loop: [id: 0xd3c6c6df]
# java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
#         at java.base/java.lang.Thread.start0(Native Method)
#         at java.base/java.lang.Thread.start(Thread.java:1518)
#         at io.netty.util.concurrent.ThreadPerTaskExecutor.execute(ThreadPerTaskExecutor.java:33)
#         at io.netty.util.concurrent.SingleThreadEventExecutor.doStartThread(SingleThreadEventExecutor.java:886)
#         at io.netty.util.concurrent.SingleThreadEventExecutor.startThread(SingleThreadEventExecutor.java:875)
#         at io.netty.util.concurrent.SingleThreadEventExecutor.execute(SingleThreadEventExecutor.java:780)
#         at io.netty.channel.AbstractChannel$AbstractUnsafe.register(AbstractChannel.java:479)
#         at io.netty.channel.SingleThreadEventLoop.register(SingleThreadEventLoop.java:80)
#         at io.netty.channel.SingleThreadEventLoop.register(SingleThreadEventLoop.java:74)
#         at io.netty.channel.MultithreadEventLoopGroup.register(MultithreadEventLoopGroup.java:86)
#         at io.netty.bootstrap.AbstractBootstrap.initAndRegister(AbstractBootstrap.java:333)
#         at io.netty.bootstrap.Bootstrap.doResolveAndConnect(Bootstrap.java:163)
#         at io.netty.bootstrap.Bootstrap.connect(Bootstrap.java:145)
#         at io.netty.bootstrap.Bootstrap.connect(Bootstrap.java:126)
#         at byzcast.comms.ByzCastNettyClientChannel.run(ByzCastNettyClientChannel.java:63)

# May 27, 2026 11:33:31 PM io.netty.channel.AbstractChannel$AbstractUnsafe register
# WARNING: Force-closing a channel whose registration task was not accepted by an event loop: [id: 0xe6c68a14]
# java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
#         at java.base/java.lang.Thread.start0(Native Method)
#         at java.base/java.lang.Thread.start(Thread.java:1518)
#         at io.netty.util.concurrent.ThreadPerTaskExecutor.execute(ThreadPerTaskExecutor.java:33)
#         at io.netty.util.concurrent.SingleThreadEventExecutor.doStartThread(SingleThreadEventExecutor.java:886)
#         at io.netty.util.concurrent.SingleThreadEventExecutor.startThread(SingleThreadEventExecutor.java:875)
#         at io.netty.util.concurrent.SingleThreadEventExecutor.execute(SingleThreadEventExecutor.java:780)
#         at io.netty.channel.AbstractChannel$AbstractUnsafe.register(AbstractChannel.java:479)
#         at io.netty.channel.SingleThreadEventLoop.register(SingleThreadEventLoop.java:80)
#         at io.netty.channel.SingleThreadEventLoop.register(SingleThreadEventLoop.java:74)
#         at io.netty.channel.MultithreadEventLoopGroup.register(MultithreadEventLoopGroup.java:86)
#         at io.netty.bootstrap.AbstractBootstrap.initAndRegister(AbstractBootstrap.java:333)
#         at io.netty.bootstrap.Bootstrap.doResolveAndConnect(Bootstrap.java:163)
#         at io.netty.bootstrap.Bootstrap.connect(Bootstrap.java:145)
#         at io.netty.bootstrap.Bootstrap.connect(Bootstrap.java:126)
#         at byzcast.comms.ByzCastNettyClientChannel.run(ByzCastNettyClientChannel.java:63)

# May 27, 2026 11:33:31 PM io.netty.channel.AbstractChannel$AbstractUnsafe register
# WARNING: Force-closing a channel whose registration task was not accepted by an event loop: [id: 0x042b99b3]
# java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
#         at java.base/java.lang.Thread.start0(Native Method)
#         at java.base/java.lang.Thread.start(Thread.java:1518)
#         at io.netty.util.concurrent.ThreadPerTaskExecutor.execute(ThreadPerTaskExecutor.java:33)

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
# java StatsReaderClients.java 2 60 Var1 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 10 15 65536 [0]&& \
# java StatsReaderClients.java 10 60 Var1 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 20 15 65536 [0]&& \
# java StatsReaderClients.java 20 60 Var1 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 30 15 65536 [0]&& \
# java StatsReaderClients.java 30 60 Var1 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 50 15 65536 [0]&& \
# java StatsReaderClients.java 50 60 Var1 64 2 && \
# ./scripts/runDSLabCluster.sh 60 1 70 15 65536 [0]&& \
# java StatsReaderClients.java 70 60 Var1 64 2 && \


# ./scripts/runDSLabCluster.sh 60 1 2 15 512000 [0]&& \
# java StatsReaderClients.java 2 60 Var1 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 10 15 512000 [0]&& \
# java StatsReaderClients.java 10 60 Var1 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 20 15 512000 [0]&& \
# java StatsReaderClients.java 20 60 Var1 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 30 15 512000 [0]&& \
# java StatsReaderClients.java 30 60 Var1 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 50 15 512000 [0]&& \
# java StatsReaderClients.java 50 60 Var1 512 2 && \
# ./scripts/runDSLabCluster.sh 60 1 70 15 512000 [0]&& \
# java StatsReaderClients.java 70 60 Var1 512 2 && \

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
# java StatsReaderClients.java 2 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 30 15 1048576 [0]&& \
# java StatsReaderClients.java 30 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 50 15 1048576 [0]&& \
# java StatsReaderClients.java 50 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 70 15 1048576 [0]&& \
# java StatsReaderClients.java 70 60 Var1 1024 2 && \

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
# java StatsReaderClients.java 2 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 10 15 1048576 [0]&& \
# java StatsReaderClients.java 10 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 20 15 1048576 [0]&& \
# java StatsReaderClients.java 20 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 30 15 1048576 [0]&& \
# java StatsReaderClients.java 30 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 50 15 1048576 [0]&& \
# java StatsReaderClients.java 50 60 Var1 1024 2 && \
# ./scripts/runDSLabCluster.sh 60 1 70 15 1048576 [0]&& \
# java StatsReaderClients.java 70 60 Var1 1024 2 && \

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