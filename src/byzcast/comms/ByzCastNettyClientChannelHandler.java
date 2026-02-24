package byzcast.comms;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import byzcast.messages.ByzCastMessage;
import byzcast.messages.ByzCastMessage.Type;
import byzcast.proxies.ByzCastClientProxy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ByzCastNettyClientChannelHandler extends ChannelInboundHandlerAdapter {
    private ByzCastClientProxy proxy;
    private short dst;
    private CyclicBarrier syncAllConnections;
    //private ArrayList activeChannelFlags;


    public ByzCastNettyClientChannelHandler(ByzCastClientProxy p, short dst, CyclicBarrier syncAllConnections){
        this.proxy = p;
        this.dst = dst;
        this.syncAllConnections = syncAllConnections;
        //this.activeChannelFlags = activeChannelFlags;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //if(ctx.channel == null)
        //activeChannelFlags[dst] == false
        proxy.setChannelToDest(ctx.channel(), dst);
        System.out.println("This channelActive was called by:" + dst);
        if(syncAllConnections != null) 
            syncAllConnections.await(); //Isso pode não estar funcionando, questão de channel active
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByzCastMessage m = (ByzCastMessage)msg;
        if(m.getType() == Type.CONN){
            proxy.receiveReplyInitMsg(m);
            return;
        }
        if(m.getType() == Type.READY){
            proxy.receiveReplyReadyMsg(m);
            return;
        }
        if(m.getType() == Type.END){
            proxy.receiveReplyEndMsg(m);
            return;
        }
        proxy.receiveReply(m);
    }
}
