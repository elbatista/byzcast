node=$1
basedir=$2
clients=$3
ID=$4
duration=$5
algo=$6
clispernode=$7
payloadSizeBytes=$8

ssh -o StrictHostKeyChecking=accept-new $node "cd $basedir; ./scripts/runClients.sh \
$node $basedir $clients $ID $duration $algo $clispernode $payloadSizeBytes" &
