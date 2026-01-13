import byzcast.ByzCastRandomClient;
import util.ArgsParser;

public class MainClient {
    public static void main(String args[]){
        ArgsParser p = ArgsParser.getClientParser(args);
        new ByzCastRandomClient(p.getId(), p, true); //Trocar
    }
}