import byzcast.messages.ByzCastMessage;
import io.netty.channel.Channel;

public interface ClientProxyTest {
    short getId();
    void setChannelToDest(Channel ch, short dst);
    void buffer(ByzCastMessage m); // opcional se quiser receber algo
} 