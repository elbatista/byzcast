import byzcast.ByzCastNode;
import byzcast.DisseminatorNode;
import util.ArgsParser;

public class MainServer {
    public static void main(String args[]){
        
        ArgsParser p = ArgsParser.getServerParser(args);

        if(p.getAlgorithm() == 0)
            new ByzCastNode(p.getId(), p);
        else
            new DisseminatorNode(p.getId(), p);
    }
}