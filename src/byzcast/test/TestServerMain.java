import base.Node;
import base.Host;

public class TestServerMain {
    public static void main(String[] args) {
        short serverId = 0;

        Host myHost = new Host("192.168.3.34", 50000);
        Node self = new Node(serverId, myHost, 0);

        new TestServer(serverId);
    }
}