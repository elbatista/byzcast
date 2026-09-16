import byzcast.ByzCastRandomClientVar1;
import byzcast.ByzCastNode;
import byzcast.ByzCastNodeVar1;
import byzcast.ByzCastRandomClientVar2;
import byzcast.ByzCastRandomClient;
import util.ArgsParser;

public class MainClient {
    public static void main(String args[]){
        ArgsParser p = ArgsParser.getClientParser(args);

        if(p.getAlgorithm() == 0) 
        new ByzCastRandomClient(p.getId(), p, true); //Trocar
        else if(p.getAlgorithm() == 1)
        new ByzCastRandomClientVar1(p.getId(), p, true); 
        else if(p.getAlgorithm() == 2)
        new ByzCastRandomClientVar2(p.getId(), p, true);
    }
}