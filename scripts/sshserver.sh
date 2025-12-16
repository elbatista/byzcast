node=$1
basedir=$2
ID=$3
algo=$4
duration=$5
clients=$6

ssh -o StrictHostKeyChecking=accept-new $node \
"cd $basedir; \
/home/cunha/jdk-23.0.2/bin/java -Xmx32g -cp \"bin/*:lib/*\" MainServer -i $ID -a $algo -d $duration -c $clients >> $basedir/logs/nodes/node$ID.txt" & 
