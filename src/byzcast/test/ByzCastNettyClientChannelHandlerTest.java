
import byzcast.messages.ByzCastMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ByzCastNettyClientChannelHandlerTest
        extends SimpleChannelInboundHandler<ByzCastMessage> {

    private final TestClient client;
    private final short serverId;

    public ByzCastNettyClientChannelHandlerTest(TestClient client, short serverId) {
        this.client = client;
        this.serverId = serverId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        client.setChannelToDest(ctx.channel(), serverId);
        System.out.println("Connected to server " + serverId);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByzCastMessage msg) {
        // Se quiser, loga replies do servidor
        // System.out.println("Reply from server: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}