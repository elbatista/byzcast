// Chama o send para Server, sem esperar retorno, anota mensagens enviadas

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;

import org.jgrapht.Graph;
import org.jgrapht.alg.lca.TarjanLCAFinder;
import org.jgrapht.graph.DefaultEdge;
import base.Node;
import byzcast.messages.ByzCastMessage;
import byzcast.messages.ByzCastMessage.TransactionType;
import byzcast.messages.ByzCastMessage.Type;
import io.netty.channel.Channel;
import util.Stats;

public class TestClient extends Node implements ClientProxyTest {

    private HashMap<Short, Channel> outChannels = new HashMap<>();
    protected int randPayloadSize = 0;
    protected final Random gen = new Random(System.nanoTime());
    private final CountDownLatch connectionLatch = new CountDownLatch(1);

    public TestClient(short id, int payloadSize, Node serverNode) {
        super(id);
        this.randPayloadSize = payloadSize;
        sleep(1000);
        connectTo(serverNode);
    }

    public void connectTo(Node dest) {
        new ByzCastNettyClientChannelTest(dest, this);
    }

    @Override
    public void setChannelToDest(Channel ch, short dst) {
        outChannels.put(dst, ch);
        connectionLatch.countDown();
        print(connectionLatch);
    }

    private void generatePayload(ByzCastMessage m) {
        byte[] payload = new byte[randPayloadSize];
        if (randPayloadSize > 0) gen.nextBytes(payload);
        m.setRandPayload(payload);
    }

    public void send(ByzCastMessage m, short dst) {
        outChannels.get(dst).writeAndFlush(m);
    }

    private ByzCastMessage newMessage(short dst) {
        ByzCastMessage m = new ByzCastMessage(1);
        m.setType(Type.MSG);
        m.setTransaction(TransactionType.NOPAYLOAD);
        m.setCliId(getId());
        m.setDst(new short[]{dst});
        generatePayload(m);
        return m;
    }

    public void awaitConnection() {
        try {
            connectionLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void run() {
        ByzCastMessage m = newMessage((short) 0);
        generatePayload(m);
    
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++) {
            send(m, (short) 0); 
        }
    
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
    
        System.out.println("Envio concluído em " + timeElapsed + " ms");

        sleep(180000);
    }

    @Override
    public void buffer(ByzCastMessage m) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buffer'");
    }
}

//run chama um java e passa path + parametros

//fixa tamanho varia lat 
//fixa lat varia tamanho

//testar:
//tempo de envio = latencia da rede + 
//tempo de sinalização da mensagem na vazão da aresta da rede (1gbit/s)


// Depois:
 
//como ajustar numero de clientes fora de multiplos de 15

//gráfico final (prot vs ori): trafego gtpcc X número de clientes,
//destinos aleatórios, tamanho do pacote variados