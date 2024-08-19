import byzcast.ByzCastClientNoLocality;
import util.ArgsParser;

public class MainClient {
    public static void main(String args[]){
        ArgsParser p = ArgsParser.getClientParser(args);
        new ByzCastClientNoLocality(p.getId(), p, true);
    }
}