for i in $(seq $1 $2)
do
    if [ "$i" -eq 32 ] || [ "$i" -eq 33 ]; then
        continue
    fi

    ssh -o StrictHostKeyChecking=accept-new node$i "killall -9 java"

    # echo killed node$i
done

echo killed all processes