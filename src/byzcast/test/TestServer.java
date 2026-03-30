import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import base.Host;
import base.Node;
import byzcast.comms.ByzCastNettyServerChannel;
import byzcast.messages.ByzCastMessage;
import byzcast.messages.ByzCastMessage.Type;
import byzcast.proxies.ByzCastServerProxy;
import io.netty.channel.Channel;
import util.MsgSize;

public class TestServer extends Node {

    private final ConcurrentLinkedQueue<ByzCastMessage> queue = new ConcurrentLinkedQueue<>();
    private final AtomicLong totalMsgs = new AtomicLong(0);
    private int num = 0;
    private long start = 0;
    private long end = 0;

    public TestServer(short id) {
        super(id);
        if (id == 0) {
            setHost(new Host("192.168.3.34", 50000));
        } else if (id == 1) {
            setHost(new Host("192.168.3.36", 50010));
        }

        System.out.println("TestServer rodando com id = " + id + " em " + getHost());

        new ByzCastNettyServerChannelTest(this, this);
    }

    public void buffer(ByzCastMessage m) {
        queue.offer(m);

        if(num == 0){
            start = System.currentTimeMillis();
        }
        num++;
        print(num);
        if(num == 2000){
            end = System.currentTimeMillis();
            print(end - start);
            print("================");
            print(start);
            print(end);
        }
    }

    private void handle(ByzCastMessage m) {
        if (m.getType() == Type.MSG) {
            totalMsgs.incrementAndGet();
        }
    }

    public long getTotalMsgs() {
        return totalMsgs.get();
    }

    public static void main(String[] args) {
        System.out.println("Entrei aqui");
        short serverId = 0; // ou ler do args se quiser
        new TestServer(serverId);
        System.out.println("TestServer rodando com id = " + serverId);

    }
}