import byzcast.ByzCastRandomClientSequential;
import byzcast.ByzCastRandomClientParallel;
import byzcast.ByzCastRandomClientLCA;
import util.ArgsParser;

public class MainClient {
    public static void main(String args[]){
        ArgsParser p = ArgsParser.getClientParser(args);

        if(p.getAlgorithm() == 0) 
        new ByzCastRandomClientLCA(p.getId(), p, true); // separa no LCA
        else if(p.getAlgorithm() == 1)
        new ByzCastRandomClientSequential(p.getId(), p, true); // separa no cliente sequencial
        else if(p.getAlgorithm() == 2)
        new ByzCastRandomClientParallel(p.getId(), p, true); // separa no cliente "paralelo"
    }
}