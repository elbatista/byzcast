package byzcast;

import java.util.List;
import util.ArgsParser;
import util.FileManager;
import base.Host;
import base.Node;
import byzcast.messages.ByzCastMessage;
import byzcast.messages.LightMessage;
import byzcast.messages.LightMessagesList;
import byzcast.proxies.ByzCastServerProxy;

public class DisseminatorNode extends ByzCastServerProxy {
    protected int numNodes;
    protected FileManager files;
    private LightMessagesList history = new LightMessagesList();
    private int msgsTotal=0;

    public DisseminatorNode(short id, ArgsParser args){
        super(id, args.getClientCount());
        this.files = new FileManager();
        List<Node> nodes = files.loadHosts();
        numNodes = nodes.size();
        Host thisHost = null;
        for(Node n : nodes){
            if(n.getId() == id){
                thisHost = n.getHost();
                break;
            }
        }
        setHost(thisHost);
        print(this, "Disseminator Node - Start listening ...");

        // sets connection to all children nodes
        for(Node node : nodes){
            if(node.getId() > getId()){
                connectTo(node);
            }
        }
        
    }

    @Override
    protected void receiveMsg(ByzCastMessage m){
        // print("Received message", m);

        msgsTotal++;

        // lca disseminates the message
        if(m.getLca() == getId())
            for(short dest : m.getDst()) if(dest != getId()) send(m, dest);

        // all who received, deliver
        deliver(m);
        
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
        //printF("Avg msg size", Stats.of(getSizes()).mean());
        files.persistMsgSizes(getSizes(), getId());
        print("-------------------------------------");
        files.nodeFinished(getId());
        exit();
    }
}
