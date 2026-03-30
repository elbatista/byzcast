import base.Node;
import base.Host;

public class TestClientMain {

    public static void main(String[] args) {

        short cliId = 1;
        int payload = Integer.parseInt(args[2]);

        Host serverHost = new Host("192.168.3.34", 50000);
        Node serverNode = new Node((short) 0, serverHost, 0);

        TestClient client = new TestClient(cliId, payload, serverNode);

        client.awaitConnection();

        System.out.println("Connected to server 0");

        client.run();
    }
}