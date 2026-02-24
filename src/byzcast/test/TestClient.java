// Chama o send para Server, sem esperar retorno, anota mensagens enviadas

import java.util.HashMap;
import java.util.Random;
import org.jgrapht.Graph;
import org.jgrapht.alg.lca.TarjanLCAFinder;
import org.jgrapht.graph.DefaultEdge;
import base.Node;
import byzcast.messages.ByzCastMessage;
import byzcast.messages.ByzCastMessage.TransactionType;
import byzcast.messages.ByzCastMessage.Type;
import io.netty.channel.Channel;
import util.Stats;


public class TestClient extends Node {
    private HashMap<Short, Channel> outChannels;
    protected Graph<Short,DefaultEdge> tree;
    protected TarjanLCAFinder<Short,DefaultEdge> lcafinder;
    
    protected Stats stats;
    protected int randPayloadSize = 0;
    protected final Random gen = new Random(System.nanoTime());
    short lca;
    short[] dsts;

    // public void connectTo(Node dest){
    //     this(node, serverProxy, null);
    // }

    private void generatePayload(ByzCastMessage m) {
        m.setTransaction(TransactionType.NOPAYLOAD);
        byte[] payload = new byte[randPayloadSize];
        if (randPayloadSize > 0) {
            gen.nextBytes(payload);
            m.setRandPayload(payload);
        } else {
            m.setRandPayload(null);
        }
    }

    public void send(ByzCastMessage m, short dst){
        try {
            outChannels.get(dst).writeAndFlush(m);
        }
        catch(Exception e){
            e.printStackTrace();
            print(e);
            exit();
        }
    }

    private ByzCastMessage newMessage(){
        ByzCastMessage m = new ByzCastMessage(1);
        m.setType(Type.MSG);
        //m.setDst(generateMaxXDests(2));
        //m.setDst([0]);
        m.setCliId(getId());
        return m;
    }

    public void run(){
        ByzCastMessage m = newMessage();

        generatePayload(m);

        long start = System.currentTimeMillis();
        
        for(int i = 0; i < 10000; i++){
            //send(m,destino)
        }

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
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