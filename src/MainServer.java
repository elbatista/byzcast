import byzcast.ByzCastNodeVar1;
import byzcast.ByzCastNode;
import byzcast.DisseminatorNode;
import util.ArgsParser;

public class MainServer {
    public static void main(String args[]){
        
        ArgsParser p = ArgsParser.getServerParser(args);

        if(p.getAlgorithm() == 0)
        new ByzCastNode(p.getId(), p);
        //new ByzCastNodeVar1(p.getId(), p);
        else
        new ByzCastNodeVar1(p.getId(), p);
            // new DisseminatorNode(p.getId(), p);
    }
}