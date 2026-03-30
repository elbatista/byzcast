
import base.Node;
import byzcast.messages.ByzCastMessageDecoder;
import byzcast.messages.ByzCastMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ByzCastNettyServerChannelTest extends Thread {

    private final TestServer server;
    private final Node node;

    public ByzCastNettyServerChannelTest(TestServer server, Node node) {
        this.server = server;
        this.node = node;
        start();
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ch.pipeline().addLast(
                         new ByzCastMessageDecoder(null),
                         new ByzCastMessageEncoder(),
                         new ByzCastNettyServerChannelHandlerTest(server)
                     );
                 }
             });

             ChannelFuture f = b.bind(
                node.getHost().getName(),
                node.getHost().getPort()
            ).sync();
            System.out.println("SimpleTestServer listening on port " + node.getHost().getPort());
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}