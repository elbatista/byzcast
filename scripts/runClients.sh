node=$1
basedir=$2
clients=$3
ID=$4
duration=$5
algo=$6
clispernode=$7
payloadSizeBytes=$8

for i in $(seq 1 $clispernode)
do
    /home/cunha/jdk-23.0.2/bin/java -Xmx64g -cp "bin/*:lib/*" MainClient -c $clients -i $ID -d $duration -a $algo -rps $payloadSizeBytes >> $basedir/logs/clients/client$ID.txt &
    sleep .1
    ID=$(($ID+1));
done