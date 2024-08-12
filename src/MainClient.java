import byzcast.ByzCastClient;
import byzcast.TpccByzCastClient;
import util.ArgsParser;

public class MainClient {
    public static void main(String args[]){
        ArgsParser p = ArgsParser.getClientParser(args);
        if(p.isTpcc())
            new TpccByzCastClient(p.getId(), p);
        else
            new ByzCastClient(p.getId(), p, true);
    }
}