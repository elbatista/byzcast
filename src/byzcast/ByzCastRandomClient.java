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
import byzcast.messages.ByzCastMessage.Split;
import byzcast.proxies.ByzCastClientProxy;
import util.ArgsParser;
import util.FileManager;
import util.Stats;
import util.Util;

public class ByzCastRandomClient extends ByzCastClientProxy {
    protected ArgsParser args;
    protected int seqNumber, totalTime, clientCount, randPayloadSize = 0;
    protected short numNodes = 0;
    protected CyclicBarrier syncAllConnections;
    protected FileManager files;
    private int [] destsSizes;
    protected final Random gen;
    private int algo;
    private String[] algorithm = new String[]{"ByzCast","Disseminator"};
    
    public ByzCastRandomClient(short id, ArgsParser args, boolean start){
        super(id);
        this.args = args;
        this.totalTime = args.getDuration();
        this.files = new FileManager();
        this.gen = new Random(System.nanoTime());
        this.clientCount = args.getClientCount();
        this.randPayloadSize = args.getRandPayloadSize();
        this.algo = args.getAlgorithm();
        ArrayList<Node> nodes = files.loadHosts();
        syncAllConnections = new CyclicBarrier(nodes.size()+1);
        for(Node server : nodes) connectTo(server, syncAllConnections);
        numNodes = (short) nodes.size();
        short root = nodes.get(0).getId();
        lcafinder = new TarjanLCAFinder<Short,DefaultEdge>(tree, root);
        destsSizes = new int [numNodes];
        if(start) start();
    }
    
    // generates an unique message id, based on the client id
    private int nextSeqNumber() {
        seqNumber++;
        int id = getId();
        int remainder = seqNumber % clientCount;
        if (remainder != id) {
            seqNumber += (id - remainder + clientCount) % clientCount;
        }
        return seqNumber;
    }
    
    private void start() {
        // wait all netty threads connect to all servers
        try {syncAllConnections.await();} 
        catch(InterruptedException | BrokenBarrierException e){
            print("Failed to wait for all server connections to complete");
            exit();
        }
        sleep(2000);
        // send initialization message to all servers
        // so they can save the connections to each client
        sendInitMessage();
        
        // send ready message to a server
        // the server will reply when all clients are ready,
        // so that we "guarantee" all clients start at (~) the same time
        sendReadyMessage();

        print("All other clients ready!");
        print("Started AWS ByzCastRandomClient experiment (", algorithm[algo],")");
        print("Rand payload size:", Util.convertBytes(randPayloadSize));

        stats = new Stats(totalTime, numNodes);

        long startTime = System.currentTimeMillis(), elapsed = 0, now;
        int totalMsgs = 0;

        while ((elapsed / 1000) < totalTime) {
            ByzCastMessage m = newMessage();
            
            generatePayload(m);

            now = System.currentTimeMillis();
            multicast(m, algo);
            stats.store((System.currentTimeMillis() - now), (m.getDst().length > 1));
            
            elapsed = (now - startTime);

            destsSizes[m.getDst().length-1]++;

            totalMsgs++;
            if(args.getNumMessages() > 0 && totalMsgs == args.getNumMessages()) break;
        }

        if (stats.getCount() > 0) {
            try {Files.createDirectories(Paths.get("results" + (args.getRegion().equals("") ? "" : "/"+args.getRegion())));} catch (IOException e) {}
            stats.persist("results" + (args.getRegion().equals("") ? "" : "/"+args.getRegion()) + "/" + getId() + "-stats-client-byzcast.txt", 15);
            stats.persistPerNodes("results" + (args.getRegion().equals("") ? "" : "/"+args.getRegion()) + "/" + getId() + "-stats-client-byzcast-per-node.txt", 15);
            print("LOCAL STATS:", stats);
        }

        sendEndMessage();

        for(int i = 0; i < destsSizes.length; i++) print("# of msgs to", i+1, "dests:", destsSizes[i]);

        print("Finished AWS ByzCastRandomClient experiment. Elapsed: ", elapsed / 1000, "seconds");
        exit();
    }

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

    private ByzCastMessage newMessage(){
        ByzCastMessage m = new ByzCastMessage(nextSeqNumber());
        m.setType(Type.MSG);
        //m.setDst(generateMaxXDests(4));
        m.setDst(new short[] {0});
        m.setCliId(getId());
        return m;
    }

    private short[] generateRandDests() {
        Set<Short> uniqueNumbers = new HashSet<>();
        int size = randomNumber(2, numNodes, gen); // only global
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

    private short[] generateMaxXDests(int X) {
        Set<Short> uniqueNumbers = new HashSet<>();
        int size = randomNumber(2, X, gen); // only global
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