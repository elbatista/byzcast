package byzcast.proxies;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import org.jgrapht.Graph;
import org.jgrapht.alg.lca.TarjanLCAFinder;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTExporter;
import base.Node;
import byzcast.comms.ByzCastNettyClientChannel;
import byzcast.messages.ByzCastMessage;
import byzcast.messages.ByzCastMessage.Type;
import io.netty.channel.Channel;
import util.FileManager;
import util.Stats;
import java.util.concurrent.ConcurrentHashMap;


public class ByzCastClientProxy extends Node {
    private ConcurrentHashMap<Short, Channel> outChannels;
    private Semaphore sema = new Semaphore(0);
    private ReentrantLock lock = new ReentrantLock();
    private ArrayList<ByzCastMessage> replies = new ArrayList<>();
    private short expectedReplies = 0;
    protected Graph<Short,DefaultEdge> tree;
    protected TarjanLCAFinder<Short,DefaultEdge> lcafinder;
    
    protected Stats stats;
    private long startTime;
    private HashMap<Short, Long> latsPerNode = new HashMap<>();
    short lca;
    short[] dsts;

    public ByzCastClientProxy(short id){
        super(id);
        outChannels = new ConcurrentHashMap<>();
        tree = new FileManager().loadByzCastTreeAsGraph();
        print("ByzCast Tree:", getTreeString());
    }

    protected String getTreeString() {
        DOTExporter<Short, DefaultEdge> exporter = new DOTExporter<>(v->String.valueOf(v));
        Writer writer = new StringWriter();
        exporter.exportGraph(tree, writer);
        return writer.toString().replaceAll("\n", " ");
    }

    public ConcurrentHashMap<Short, Channel> getOutChannels() {
    return outChannels;
    }

    public void connectTo(Node dest){
        new ByzCastNettyClientChannel(dest, this);
    }

    public void connectTo(Node dest, CyclicBarrier syncAllConnections){
        new ByzCastNettyClientChannel(dest, this, syncAllConnections);
    }

    public void setChannelToDest(Channel c, short dst){
        print("Channel to node", dst, ":", c);
        try {
            outChannels.put(dst, c);
        }
        catch(Exception e){
            e.printStackTrace();
            print(e);
            exit();
        }
    }

    // public void sendInitMessage(){
    //     ByzCastMessage m = new ByzCastMessage(-1);
    //     sleep(500);
    //     m.setType(Type.CONN);
    //     m.setCliId(getId());

    //     System.out.println("Keys: " + outChannels.keySet());
    //     System.out.println("Channel: " + outChannels.get((short)0));
    //     for(short i : outChannels.keySet()){
    //         try {
    //             Channel ch = outChannels.get(i);
    //             if (ch == null) {
    //                 throw new IllegalStateException("Channel not initialized for peer " + i);
    //             }
    //             ch.writeAndFlush(m);
    //             //outChannels.get(i).writeAndFlush(m);
    //             sema.acquire();
    //         } catch (InterruptedException e) {
                
    //             e.printStackTrace();
    //         }
    //     }
    // }

    public void sendInitMessage() {
        ByzCastMessage m = new ByzCastMessage(-1);
        
        sleep(500);
        
        m.setType(Type.CONN);
        m.setCliId(getId());
        
        for (short i : outChannels.keySet()) {
            Channel ch = outChannels.get(i);
        
            if (ch == null) {
                System.out.println("================================");
                System.out.println("NULL CHANNEL IN INIT MESSAGE!");
                System.out.println("Client = " + getId());
                System.out.println("Destination = " + i);
                System.out.println("Known channels = " + outChannels.keySet());
                System.out.println("Message type = " + m.getType());
                System.out.println("Message id = " + m.getId());
                System.out.println("Thread = " + Thread.currentThread().getName());
                System.out.println("================================");
            
                continue;
            }
        
            System.out.println(
                "[SEND INIT] Client=" + getId() +
                " dst=" + i +
                " channel=" + ch.id() +
                " active=" + ch.isActive() +
                " open=" + ch.isOpen()
            );
        
            try {
                ch.writeAndFlush(m);
                sema.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendReadyMessage() {
        sleep(500);

        ByzCastMessage m = new ByzCastMessage();
        m.setType(Type.READY);
        m.setCliId(getId());

        short dst = 0;

        Channel ch = outChannels.get(dst);

        if (ch == null) {
            System.out.println("================================");
            System.out.println("NULL CHANNEL!");
            System.out.println("Client = " + getId());
            System.out.println("Trying to send to dst = 0");
            System.out.println("Known channels = " + outChannels.keySet());
            System.out.println("Message type = " + m.getType());
            System.out.println("Message id = " + m.getId());
            System.out.println("Thread = " + Thread.currentThread().getName());
            System.out.println("================================");

            return;
        }

        System.out.println(
            "[SEND READY] Client=" + getId() +
            " dst=0" +
            " channel=" + ch.id() +
            " active=" + ch.isActive() +
            " open=" + ch.isOpen()
        );

        try {
            ch.writeAndFlush(m);
            sema.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendEndMessage(){
        ByzCastMessage m = new ByzCastMessage();
        m.setType(Type.END);
        m.setCliId(getId());
        for(short i : outChannels.keySet()){
            try {
                outChannels.get(i).writeAndFlush(m);
                sema.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void receiveReplyInitMsg(ByzCastMessage reply){
        print("Init OK - Server", reply.getSender());
        sema.release();
    }

    public void send(ByzCastMessage m, short dst){
        // print(m.getId(), Arrays.toString(m.getDst()));
        try {
            Channel ch = outChannels.get(dst);
            if (ch == null) {
                System.out.println("================================");
                System.out.println("NULL CHANNEL!");
                System.out.println("Client = " + getId());
                System.out.println("Trying to send to dst = " + dst);
                System.out.println("Known channels = " + outChannels.keySet());
                System.out.println("Map size = " + outChannels.size());
                System.out.println("Message type = " + m.getType());
                System.out.println("Message id = " + m.getId());
                System.out.println("Thread = " + Thread.currentThread().getName());
                System.out.println("================================");

                throw new RuntimeException("Channel " + dst + " is null" + " for client " + getId() + ". Known channels: " + outChannels.keySet());
            }
            ch.writeAndFlush(m);
            // outChannels.get(dst).writeAndFlush(m);
        }
        catch(Exception e){
            e.printStackTrace();
            print(e);
            exit();
        }
    }

    public void receiveReplyReadyMsg(ByzCastMessage reply){
        print("Ready OK - Server", reply.getSender());
        sema.release();
    }

    public void receiveReplyEndMsg(ByzCastMessage reply){
        print("End OK - Server", reply.getSender());
        sema.release();
    }

    public void receiveReply(ByzCastMessage reply){
        lock.lock();

        // store latency per node in ms
        latsPerNode.put(reply.getSender(), ((System.currentTimeMillis() - startTime)));

        replies.add(reply);

        if(replies.size() == expectedReplies){
            if(stats != null) stats.store(latsPerNode, expectedReplies>1, dsts);
            sema.release();
        }
        lock.unlock();
    }

    public ByzCastMessage multicast(ByzCastMessage m){
        // Standard multicast is for ByzCast algorithm (0)
        return multicast(m, 0);
    }

    //Version of multicast that does not wait for replies. Used for sending ORD messages to the LCA before sending PAY messages to the destinations.
    public ByzCastMessage multicastNoReply(ByzCastMessage m, int algo){
        lca = getLca(m, algo);
        send(m, lca);
        return null;
    }

     public ByzCastMessage sendPayload(ByzCastMessage m, ArrayList<Node> nodes){
        for(Node n : nodes){
            if(m.isAddressedTo(n.getId())){
                // print(payloadMsg);
                // print(n.getId());
                send(m, n.getId());
            }
        }
        return null;
    }
    
    public ByzCastMessage sendPayloadWithReply(ByzCastMessage m, ArrayList<Node> nodes){
        replies.clear();
        expectedReplies = (short) m.getDst().length;
        latsPerNode.clear();
        startTime = System.currentTimeMillis();
        dsts = m.getDst();
        for(Node n : nodes){
            if(m.isAddressedTo(n.getId())){
                // print(payloadMsg);
                // print(n.getId());
                send(m, n.getId());
            }
        }
        try {
            sema.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return replies.get(0);
    }


    public ByzCastMessage multicast(ByzCastMessage m, int algo){
        replies.clear();
        expectedReplies = (short) m.getDst().length;
        latsPerNode.clear();
        startTime = System.currentTimeMillis();
        dsts = m.getDst();
        lca = getLca(m, algo);
        send(m, lca);
        try {
            sema.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return replies.get(0);
    }
    //Pegar ESSE LCA
    public short getLca(ByzCastMessage m, int algo) {
        if(m.getDst().length == 1) return m.getDst()[0];

        // Disseminator:
        if(algo == 1) return m.getLca();

        // ByzCast:
        ArrayList<Pair<Short,Short>> list = new ArrayList<>();
        for(int i = 0; i < m.getDst().length; i++){
            if((i+1) < m.getDst().length){
                list.add(new Pair<Short,Short>(m.getDst()[i], m.getDst()[i+1]));
            }
        }
        List<Short> sorted = lcafinder.getBatchLCA(list);

        while(true){
            if(sorted.size() > 1){
                list = new ArrayList<>();
                for(int i = 0; i < sorted.size(); i++){
                    if((i+1) < sorted.size()){
                        list.add(new Pair<Short,Short>(sorted.get(i), sorted.get(i+1)));
                    }
                }
                sorted = lcafinder.getBatchLCA(list);
                continue;
            }
            break;
        }

        return sorted.get(0);
    }

}