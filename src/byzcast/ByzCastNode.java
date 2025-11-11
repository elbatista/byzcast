package byzcast;

import java.util.ArrayList;
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
import byzcast.messages.ByzCastMessage.Split;
import byzcast.messages.ByzCastMessage;
import byzcast.messages.LightMessage;
import byzcast.messages.LightMessagesList;
import byzcast.proxies.ByzCastServerProxy;

public class ByzCastNode extends ByzCastServerProxy {
    protected int numNodes;
    protected FileManager files;
    private LightMessagesList history = new LightMessagesList();
    private List<Node> children = new ArrayList<>();
    private List<Node> connected = new ArrayList<>();
    private ArrayList<String[]> mappings = new ArrayList<>();
    private int msgsTotal=0, msgsToMe=0, testO=0, testP=0,testX=0;

    public ByzCastNode(short id, ArgsParser args){
        super(id, args.getClientCount());
        this.files = new FileManager();
        List<Node> nodes = files.loadHosts();
        numNodes = nodes.size();
        Host thisHost = null;
        short root = nodes.get(0).getId();
        lcafinder = new TarjanLCAFinder<Short,DefaultEdge>(tree, root);
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

        if(m.getSplit() == Split.ORD){
            if(testO < 10){
                print("Got my Order Split from");
                print(m.getId());
                print(getLca(m,0));
                testO++;
            }
        }

        if(m.getSplit() == Split.PAY){
            if(testP < 10){
                print("Got my Payload Split from");
                print(m.getId());
                print(getLca(m,0));
                print(m.getDst());
                testP++;
            }
        }

        ByzCastMessage payloadMsg;
        msgsTotal++;
        if(m.isAddressedTo(getId())) msgsToMe++;

        Set<Short> sent = new HashSet<>();

        // //Conferir se é o LCA
        // if (getId() == getLca(m,0) && testO < 10){
        //     print("Im the LCA!");
        //     testO++;
        // }
        // else{
        //     if(testO < 10){
        //         print("Im the not the LCA!");
        //         print(String.valueOf(getId()));
        //         print(String.valueOf(getLca(m,0)));
        //         testO++;
        //     }
        // }

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

        //     // send to its children
        //     for(Node n : children){
        //         if(m.isAddressedTo(n.getId())){
        //             // print("Will send to child", n.getId());
        //             send(m, n.getId());
        //             sent.add(n.getId());
        //         }
        //     }

        //     // for each mapping, send to the children in the mapping, if not sent yet
        //     for(String[] map : mappings){
        //         if(m.isAddressedTo(Short.valueOf(map[1])) && !sent.contains(Short.valueOf(map[2]))){
        //             // print("Will send to child",Short.valueOf(map[2]), "via mapping, for node", Short.valueOf(map[1]));
        //             send(m, Short.valueOf(map[2]));
        //             sent.add(Short.valueOf(map[2]));
        //         }
        //     }
        // }

        if(m.isAddressedTo(getId())){
            deliver(m);
            // print("Delivered message", m);
        }
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
