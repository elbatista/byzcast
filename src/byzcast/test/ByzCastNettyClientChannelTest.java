import base.Node;
import byzcast.messages.ByzCastMessageDecoder;
import byzcast.messages.ByzCastMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ByzCastNettyClientChannelTest extends Thread {

    private final TestClient client;
    private final Node node;

    public ByzCastNettyClientChannelTest(Node dest, TestClient client) {
        this.node = dest;
        this.client = client;
        start();
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.SO_KEEPALIVE, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ch.pipeline().addLast(
                         new ByzCastMessageDecoder(null),
                         new ByzCastMessageEncoder(),
                         new ByzCastNettyClientChannelHandlerTest(client, node.getId())
                     );
                 }
             });

            Channel channel = null;
            while (channel == null) {
                try {
                    ChannelFuture future = b.connect(node.getHost().getName(), node.getHost().getPort()).sync();
                    channel = future.channel();
                } catch (Exception e) {
                    Thread.sleep(2000);
                }
            }

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}