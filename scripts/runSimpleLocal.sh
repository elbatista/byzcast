#!/bin/bash

# uso:
# ./scripts/runPayloadTest.sh <payload_size_bytes>

# for n in node34 node36; do
#   ssh $n "pkill -f MainServer"
# done

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <payload_size_bytes>"
    exit 1
fi

PAYLOAD=$1
BASEDIR=~/byzcast

SERVER_IP=192.168.3.34
CLIENT_IP=192.168.3.36

SERVER_ID=0
CLIENT_ID=1

SERVER_PORT=50000
CLIENT_PORT=50010

SERVER_HOST=node34
CLIENT_HOST=node36

rm -rf $BASEDIR/logs
mkdir -p $BASEDIR/logs

cd $BASEDIR || exit 1

echo "==> Compilando..."
ant clean && ant || exit 1

echo "=== Portas em uso antes de subir o servidor ==="
ssh node34 "lsof -i :50000" || echo "Porta 50000 livre"

echo "==> Subindo servidor em $SERVER_HOST..."

ssh $SERVER_IP "
  cd $BASEDIR &&
  nohup java -Xmx2g -cp 'bin:lib/*' TestServer $SERVER_ID > logs/server.log 2>&1
" &

sleep 2
echo "==> Subindo cliente em $CLIENT_IP (payload=$PAYLOAD bytes)..."
echo "=== Portas em uso antes de subir o cliente ==="
lsof -i :50010 || echo "Porta 50010 livre"
ssh $CLIENT_IP "
  cd $BASEDIR &&
  nohup java -Xmx2g -cp 'bin:lib/*' TestClientMain $CLIENT_ID $SERVER_ID $PAYLOAD > logs/client.log 2>&1
" &

echo "==> Execução iniciada"
echo "==> Logs:"
echo "Servidor: $SERVER_IP:$BASEDIR/logs/server.log"
echo "Cliente:  $CLIENT_IP:$BASEDIR/logs/client.log"