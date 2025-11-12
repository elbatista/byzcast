package byzcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.javatuples.Pair;
import org.jgrapht.alg.lca.TarjanLCAFinder;
import org.jgrapht.graph.DefaultEdge;
import util.ArgsParser;
import util.FileManager;
import base.Host;
import base.Node;
import java.lang.Long;
import byzcast.messages.ByzCastMessage.Split;
import byzcast.messages.ByzCastMessage;
import byzcast.messages.LightMessage;
import byzcast.messages.LightMessagesList;
import byzcast.proxies.ByzCastServerProxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ByzCastNode extends ByzCastServerProxy {
    protected int numNodes;
    protected FileManager files;
    private LightMessagesList history = new LightMessagesList();
    private List<Node> children = new ArrayList<>();
    private List<Node> connected = new ArrayList<>();
    private ArrayList<String[]> mappings = new ArrayList<>();
    private int msgsTotal=0, msgsToMe=0, testO=0, testP=0;
    private BlockingQueue<ByzCastMessage> ordQueue = new LinkedBlockingQueue<>();
    private ConcurrentHashMap<Long, ByzCastMessage> payloads = new ConcurrentHashMap<>();

    private volatile boolean running = true;

    public ByzCastNode(short id, ArgsParser args){
        super(id, args.getClientCount());
        this.files = new FileManager();
        List<Node> nodes = files.loadHosts();
        numNodes = nodes.size();
        Host thisHost = null;
        short root = nodes.get(0).getId();
        lcafinder = new TarjanLCAFinder<Short,DefaultEdge>(tree, root);
        startDeliveryThread();
        
        for(Node n : nodes){
            if(n.getId() == id){
                thisHost = n.getHost();
                break;
            }
        }
        setHost(thisHost);
        print(this, "ByzCast Node - Start listening ...");
        // sets connection to all children nodes

        for(Node node : nodes){
            if(node.getId() > getId()){
                connectTo(node);
                connected.add(node);
            }
        }

        for(Pair<Short, Short> p : files.loadByzCastTree(mappings, getId())){
            if (p.getValue0() == getId()){
                for(Node node : nodes){
                        if(p.getValue1() == node.getId()){
                            children.add(node);
                        }
                    }
                }
            }        
        }

    @Override
    protected void receiveMsg(ByzCastMessage m){
        ByzCastMessage payloadMsg;
        msgsTotal++;
        if(m.isAddressedTo(getId())) msgsToMe++;

        if (m.getSplit() == Split.ORD && m.isAddressedTo(getId())) {
            ordQueue.add(m);
        } else if (m.getSplit() == Split.PAY) {
            payloads.put(Long.valueOf(m.getId()), m);
        }

        Set<Short> sent = new HashSet<>();

        if (getId() == getLca(m,0)){
            //Caso seja, separa payload de msg Ordem
            // separar a msg na normal e em uma que é só o payload (sabe o id da de Ordem) -> randPayload

            payloadMsg = m.cloneMessage(m);
            payloadMsg.setSplit(Split.PAY);
            m = m.splitSelf(m);

            //Encaminha msg DEST para os destinos conforme a árvore
            for(Node n : connected){
                if(payloadMsg.isAddressedTo(n.getId())){
                    send(payloadMsg, n.getId());
                }
            }

            if(m.isAddressedTo(getId())){
                deliver(m);
                // print("LCA Delivered!");
                // print(m.getId(), Arrays.toString(m.getDst()));
            }
        }

        // envia msg de Ordem
        for(Node n : children){
            if(m.isAddressedTo(n.getId())){
                send(m, n.getId());
                sent.add(n.getId());
            }
        }

        // for each mapping, send to the children in the mapping, if not sent yet
        for(String[] map : mappings){
            if(m.isAddressedTo(Short.valueOf(map[1])) && !sent.contains(Short.valueOf(map[2]))){
                // print("Will send to child",Short.valueOf(map[2]), "via mapping, for node", Short.valueOf(map[1]));
                send(m, Short.valueOf(map[2]));
                sent.add(Short.valueOf(map[2]));
            }
        }

        // if(m.isAddressedTo(getId()) && m.getSplit() == Split.ORD){
        //     deliver(m);
        //     // print("Delivered message", m);
        // }
    }

    private void startDeliveryThread() {
        Thread deliveryThread = new Thread(() -> {
            try {
                while (running) {
                    // pega próxima mensagem de ordem da fila (bloqueia se estiver vazia)
                    ByzCastMessage ordMsg = ordQueue.take();
    
                    long msgId = ordMsg.getId();
    
                    // espera até o payload correspondente chegar
                    ByzCastMessage payMsg = null;
                    while (running && payMsg == null) {
                        payMsg = payloads.remove(msgId);
                        // print(msgId);
                        // print(payloads);
                        if (payMsg == null) {
                            Thread.sleep(10); // espera um pouco antes de tentar de novo
                        }
                    }
    
                    // quando as duas chegaram:
                    if (ordMsg.isAddressedTo(getId())) {
                        deliver(ordMsg, payMsg);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    
        deliveryThread.start();
    }
    

    private void deliver(ByzCastMessage ordMsg, ByzCastMessage payMsg) {
        // Combina as informações se quiser
        history.add(new LightMessage(ordMsg.getId(), ordMsg.getDst()));
        sendReply(ordMsg);
        // print("Delivered message with ID", ordMsg.getId(), " (ORD + PAY)");
        // print(Arrays.toString(ordMsg.getDst()));
    }

    private void deliver(ByzCastMessage m) {
        history.add(new LightMessage(m.getId(), m.getDst()));
        sendReply(m);
        // print("Delivered message", m);
    }

    protected void finish(){
        if(bufferQueue.size() > 0){
            print("Queue is not empty !!! ");
            files.stop();
            exit();
        }
        print("Queue is empty ! =]");
        files.persistMessages(history, getId(), false, false);
        print("-------------------------------------");
        print("Total msgs in the history:", history.size());
        print("Total local msgs received:", localMsgs);
        print("Total msgs received:", msgsTotal);
        print("Total msgs to me received:", msgsToMe);
        print("% of overhead:", 100-((msgsToMe*100)/msgsTotal));
        //printF("Avg msg size", Stats.of(getSizes()).mean());
        files.persistMsgSizes(getSizes(), getId());
        print("-------------------------------------");
        files.nodeFinished(getId());
        exit();
    }
}
