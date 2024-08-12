import byzcast.ByzCastNode;
import util.ArgsParser;

public class MainServer {
    public static void main(String args[]){
        ArgsParser p = ArgsParser.getServerParser(args);
        new ByzCastNode(p.getId(), p);
    }
}