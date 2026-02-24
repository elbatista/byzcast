#!/bin/bash

# run example:
# ./scripts/runCluster.sh 120 0 150 6 12 95 0 100 25 false true true false 1
if [ "$#" -lt 5 ]; then 
    echo  "Usage: $0 <duration:sec> <algo:0-ByzCast;1-Disseminator> <#clispernode> <#servers> <payload size>"
    exit 0; 
fi

basedir=~/byzcast;
countClientNodes=$(grep -c "node" "$basedir/config/clients.conf")
i=0;
ID=0;
iniport=50000;
duration=$1;
algo=$2;
clispernode=$3
servers=$4;
clients=$((countClientNodes * clispernode));
payloadSizeBytes=$5
dests=$6
firstNode=34
lastNode=64
algodesc=("ByzCast" "Disseminator")
# payloadSizeBytes=1048576 # 1MB
rm -f -r $basedir/logs $basedir/files $basedir/results;
mkdir $basedir/logs; mkdir $basedir/logs/nodes; mkdir $basedir/logs/clients; mkdir $basedir/files; mkdir $basedir/results;

echo false > $basedir/files/stop;
echo "------------------------------------------------------------------------------------------------" >> $basedir/logs/execution.log;
echo "started experiment on" $(date) >> $basedir/logs/execution.log;
echo "Algorithm = ${algodesc[$2]}" >> $basedir/logs/execution.log;
echo "Duration = $duration sec; ClixNode = $clispernode; Cli Nodes = $countClientNodes; Total Clients = $clients; Servers = $servers; " >> $basedir/logs/execution.log;
echo "Payload size = $5 bytes" >> $basedir/logs/execution.log;
echo "------------------------------------------------------------------------------------------------" >> $basedir/logs/execution.log;

./scripts/killAll.sh $firstNode $lastNode >> $basedir/logs/execution.log;

# compile, create config file, and update all other nodes
echo creating hosts.config for $servers servers >> $basedir/logs/execution.log;
echo "#id ip port" > $basedir/config/hosts.config;
confserverid=0;
serverfile=$basedir/config/servers.conf
while IFS=, read -r node region ip
do
    # echo "will deploy server id $confserverid on $node ($ip) representing region $region" >> $basedir/logs/execution.log;
    echo "$confserverid $ip $iniport" >> $basedir/config/hosts.config;
    confserverid=$(($confserverid+1));
    iniport=$(($iniport+10));
done < <( awk '!/^ *#/ && NF' "$serverfile");

cd $basedir;
echo compiling source code >> $basedir/logs/execution.log;
ant clean; ant;

sleep 1;

##### Start servers
while IFS=, read -r node region ip
do
    echo "starting server $ID on $node region $region" >> $basedir/logs/execution.log;
    ./scripts/sshserver.sh $node $basedir $ID $algo $duration $clients
    sleep .5;
    ID=$(($ID+1));
done < <( awk '!/^ *#/ && NF' "$serverfile");
echo "started $servers servers"  >> $basedir/logs/execution.log;


##### Start clients
ID=0;
clifile=$basedir/config/clients.conf
while IFS=, read -r node region
do
    echo "$clispernode clients (from id $ID) on $node region $region " >> $basedir/logs/execution.log;

    ./scripts/sshcli.sh $node $basedir $clients $ID $duration $algo $clispernode $payloadSizeBytes $dests
    sleep .5;
    ID=$(($ID+$clispernode));
done < <( awk '!/^ *#/ && NF'  "$clifile");

echo "waiting for nodes to finish" >> $basedir/logs/execution.log;
sleep $duration;

while :
do
    nodeFiles=`find $basedir/files -name 'NodeFinished*' | wc -l` #Count files and store in a variable
    if [ "$nodeFiles" -ge $servers ]; then break; fi
    sleep 2;
done

echo "all nodes done" >> $basedir/logs/execution.log;
./scripts/killAll.sh $firstNode $lastNode >> $basedir/logs/execution.log;
echo "experiment finished at " $(date)  >> $basedir/logs/execution.log;

expdir="$basedir/experiments/${algodesc[$algo]}/${servers}nodes/${clients}cli/ps$payloadSizeBytes";

mkdir -p $expdir/config
echo "moving data to" $expdir >> $basedir/logs/execution.log;
cp -r $basedir/logs $expdir/
cp -r $basedir/files $expdir/
cp -r $basedir/results $expdir/
cp    $basedir/config/*.conf* $expdir/config/

echo done. exiting  >> $basedir/logs/execution.log;
