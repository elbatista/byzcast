import byzcast.ByzCastNodeSequential;
import byzcast.ByzCastNodeLCA;
import byzcast.ByzCastNodeParallel;
import byzcast.DisseminatorNode;
import util.ArgsParser;

public class MainServer {
    public static void main(String args[]){
        
        ArgsParser p = ArgsParser.getServerParser(args);

        if(p.getAlgorithm() == 0)
        new ByzCastNodeLCA(p.getId(), p);
        //new ByzCastNodeVar1(p.getId(), p);
        if(p.getAlgorithm() == 1)
        new ByzCastNodeSequential(p.getId(), p);
        if(p.getAlgorithm() == 2)
        new ByzCastNodeParallel(p.getId(), p);
    }
}