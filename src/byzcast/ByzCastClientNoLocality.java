package byzcast;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import org.jgrapht.alg.lca.TarjanLCAFinder;
import org.jgrapht.graph.DefaultEdge;
import base.Node;
import byzcast.messages.ByzCastMessage;
import byzcast.messages.ByzCastMessage.TransactionType;
import byzcast.messages.ByzCastMessage.Type;
import byzcast.proxies.ByzCastClientProxy;
import util.ArgsParser;
import util.FileManager;
import util.Stats;

public class ByzCastClientNoLocality extends ByzCastClientProxy {
    protected ArgsParser args;
    protected int seqNumber, totalTime, numDests=1;
    protected short numNodes = 0;
    protected CyclicBarrier syncAllConnections;
    protected FileManager files;
    protected final Random gen;

    public ByzCastClientNoLocality(short id, ArgsParser args, boolean start){
        super(id, args.getTree());
        this.args = args;
        totalTime = args.getDuration();
        this.files = new FileManager();
        this.gen = new Random(System.nanoTime());
        ArrayList<Node> nodes = files.loadHosts();
        syncAllConnections = new CyclicBarrier(nodes.size()+1);
        for(Node server : nodes) connectTo(server, syncAllConnections);
        numNodes = (short) nodes.size();
        short root = 0;
        lcafinder = new TarjanLCAFinder<Short,DefaultEdge>(tree, root);        
        if(start) start();
    }
    
    // generates an unique message id, based on the client id
    private int nextSeqNumber(){
        seqNumber++;
        while((seqNumber % args.getClientCount()) != getId())
            seqNumber++;
        return seqNumber;
    }

    private void start() {
        // wait all netty threads connect to all servers
        try {syncAllConnections.await();} catch(InterruptedException|BrokenBarrierException e){print("Broken barrier!!!!");}
        
        sleep(5000);
        print("Started AWS ByzCast No Locality experiment");
        // send initialization message to all servers
        sendInitMessage();
        // send ready message to a server
        // the server will reply when all clients are ready, then we "guarantee" all clients start at (~) the same time
        sendReadyMessage();
        print("All other clients ready!");
        
        
        if(args.getNumPartitions() > 0) {
            print ("Sending messages to", args.getNumPartitions(), "destinations");
            numDests = args.getNumPartitions();
        }

        print("ByzCast Tree:", args.getTree());

        stats = new Stats(totalTime, numNodes);

        long startTime = System.nanoTime();
        long now;
        long elapsed = 0;//, usLat = startTime;

        while ((elapsed / 1e9) < totalTime) {
            ByzCastMessage m = newMessage();

            now = System.nanoTime();
            multicast(m);
            stats.store((System.nanoTime() - now) / 1000, (m.getDst().length > 1));
            
            elapsed = (now - startTime);

        }

        if (stats.getCount() > 0) {
            try {Files.createDirectories(Paths.get("results" + (args.getRegion().equals("") ? "" : "/"+args.getRegion())));} catch (IOException e) {}
            stats.persist("results" + (args.getRegion().equals("") ? "" : "/"+args.getRegion()) + "/" + getId() + "-stats-client-byzcast.txt", 15);
            stats.persistPerNodes("results" + (args.getRegion().equals("") ? "" : "/"+args.getRegion()) + "/" + getId() + "-stats-client-byzcast-per-node.txt", 15);
            print("LOCAL STATS:", stats);
        }

        sendEndMessage();

        print("Finished AWS ByzCast experiment. Elapsed: ", elapsed / 1e9, "seconds");
        exit();
    }

    protected ByzCastMessage newMessageTo(short... dst){
        ByzCastMessage m = new ByzCastMessage(nextSeqNumber());
        m.setType(Type.MSG);
        m.setTransaction(TransactionType.NOPAYLOAD);
        m.setCliId(getId());
        m.setDst(dst);
        return m;
    }

    private ByzCastMessage newMessage(){
        ByzCastMessage m = new ByzCastMessage(nextSeqNumber());
        m.setType(Type.MSG);
        m.setTransaction(TransactionType.NOPAYLOAD);
        m.setDst(generateRandDests());
        m.setCliId(getId());
        return m;
    }

    private short[] generateRandDests() {
        Set<Short> uniqueNumbers = new HashSet<>();
        int size = numDests;// randomNumber(2, numNodes, gen); // only global
        while (uniqueNumbers.size() < size)
            uniqueNumbers.add((short)randomNumber(0, numNodes-1, gen));
        short [] tempdst = new short[size];
        short i = 0;
        for(short u : uniqueNumbers.stream().sorted().collect(Collectors.toList())){
            tempdst[i] = u;
            i++;
        }
        return tempdst;
    }

    public static int randomNumber(int min, int max, Random r) {
        return (int) (r.nextDouble() * (max - min + 1) + min);
    }

}