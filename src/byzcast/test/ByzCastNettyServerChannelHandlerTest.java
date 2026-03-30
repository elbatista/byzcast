import byzcast.messages.ByzCastMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ByzCastNettyServerChannelHandlerTest
        extends SimpleChannelInboundHandler<ByzCastMessage> {

    private final TestServer server;

    public ByzCastNettyServerChannelHandlerTest(TestServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByzCastMessage msg) {
        msg.setChannelIn(ctx.channel());  
        server.buffer(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}