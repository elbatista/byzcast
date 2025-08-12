#!/bin/bash

# run example:
# ./scripts/runCluster.sh 120 0 150 6 12 95 0 100 25 false true true false 1
if [ "$#" -lt 14 ]; then 
    echo  "Usage: $0 <duration:sec> <algo:0-flex;1-skeen;2-byz> <#clis> <#servers> <#nodes> <locality> <#msgs> <#gc(ms)> <#clispernode> <tpcc> <payload> <thinktime> <localm> <dag_tree>"
    exit 0; 
fi

i=0;
ID=-1;
log="any"; # either -log or any
warehouse=0;
iniport=50000;
basedir=~/flexcast;
duration=$1;
algo=$2;
clients=$3;
servers=$4;
nodes=$5;
locality=$6;
msgs=$7;
gc=$8;
clispernode=$9;
tpcc=${10}
payload=${11}
thinktime=${12}
localm=${13}
dag_tree=${14}
rc="30"
iniNode=60
clilat=true

algodesc=("flexcast" "skeen" "byzcast")
rm -f -r $basedir/logs $basedir/files $basedir/results;
mkdir $basedir/logs; mkdir $basedir/files; mkdir $basedir/results;

echo false > $basedir/files/stop;
echo "------------------------------------------------------------------------------------------------" >> $basedir/logs/execution.log;
echo "started experiment on" $(date) >> $basedir/logs/execution.log;
echo "duration=$1 algo=${algodesc[$2]} clients=$3 servers=$4 nodes=$5 locality=$6 msgs=$7 gc=$8 \
clispernode=$9 tpcc=$tpcc payload=$payload thinktime=$thinktime" dag_tree=$dag_tree >> $basedir/logs/execution.log;
echo "------------------------------------------------------------------------------------------------" >> $basedir/logs/execution.log;

./scripts/killAll.sh $nodes $iniNode >> $basedir/logs/execution.log;

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

# one more client for the gc:
if [ "$gc" -gt 0 ]; then
    clients=$(($clients+1));
fi

# one more client for the reconfig client:
if [ "$rc" != "" ]; then 
    clients=$(($clients+1));
fi

# Start servers
declare -A warehouses
while IFS=, read -r node region ip
do
    ID=$(($ID+1));
    echo "starting server $ID on $node region $region" >> $basedir/logs/execution.log;
    warehouses[$node]=$ID;
    ./scripts/sshserver.sh $node $basedir $ID $algo $duration $clients $log $payload
    sleep .5;
done < <( awk '!/^ *#/ && NF' "$serverfile");
echo "started $servers servers"  >> $basedir/logs/execution.log;


lastnode="";
ID=0;
clifile=$basedir/config/clients.conf
while IFS=, read -r node region nodewarehouse
do
    warehouse="${warehouses[$nodewarehouse]}"
    echo "$clispernode clients on $node region $region assume as primary warehouse: $warehouse ($nodewarehouse)" >> $basedir/logs/execution.log;

    ./scripts/sshcli.sh $node $basedir $clients $ID $duration $algo $locality $warehouse $msgs $log $tpcc $clispernode $payload $thinktime $localm $dag_tree $region # >> $basedir/logs/execution.log;
    sleep 1;
    ID=$(($ID+$clispernode));
    lastnode=$node;
done < <( awk '!/^ *#/ && NF'  "$clifile");


if [ "$rc" != "" ]; then 
    ssh -o StrictHostKeyChecking=accept-new $lastnode \
    "cd $basedir; java -cp \"bin/*:lib/*\" MainClient -c $clients -i $ID -d $duration -a $algo $log -rc $rc >> logs/reconfigoracle.txt" &
    echo "started reconfig client ($rc sec) on $lastnode" >> $basedir/logs/execution.log;
    ID=$(($ID+1));
fi

if [ "$gc" -gt 0 ]; then
    echo "started $(($clients-1)) clients" >> $basedir/logs/execution.log;
    # ID=$(($ID+1));
    ssh -o StrictHostKeyChecking=accept-new $lastnode \
    "cd $basedir; java -cp \"bin/*:lib/*\" MainClient -c $clients -i $ID -d $duration -a $algo $log -gc $gc >> $basedir/logs/gc.txt" &
    echo started gc client on $lastnode >> $basedir/logs/execution.log;
else
    echo "started $clients clients" >> $basedir/logs/execution.log;
fi

echo "waiting for nodes to finish" >> $basedir/logs/execution.log;
sleep $duration;

while :
do
    nodeFiles=`find $basedir/files -name 'NodeFinished*' | wc -l` #Count files and store in a variable
    if [ "$nodeFiles" -ge $servers ]; then break; fi
    sleep 2;
done

echo "all nodes done" >> $basedir/logs/execution.log;
./scripts/killAll.sh $nodes $iniNode >> $basedir/logs/execution.log;
echo "experiment finished at " $(date)  >> $basedir/logs/execution.log;

expdir="$basedir/experiments/${algodesc[$2]}-reconfig/${servers}nodes/${3}cli/${locality}%/gc${gc}";

if [ "$rc" != "" ]; then
    expdir="$expdir/rc${rc}"
else
    expdir="$expdir/norc"
fi

if $clilat; then
    expdir="$expdir/clilat"
else
    expdir="$expdir/noclilat"
fi

mkdir -p $expdir/config
echo "moving data to" $expdir >> $basedir/logs/execution.log;
cp -r $basedir/logs $expdir/
cp -r $basedir/files $expdir/
cp -r $basedir/results $expdir/
cp    $basedir/config/*.conf* $expdir/config/

echo done. exiting  >> $basedir/logs/execution.log;
