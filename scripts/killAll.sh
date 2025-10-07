for i in $(seq $1 $2)
do
    ssh -o StrictHostKeyChecking=accept-new node$i "killall -9 java"
    # echo killed node$i
done
echo killed all processes