package agano.messaging;

import agano.ipmsg.MalformedMessageException;
import agano.ipmsg.Message;
import agano.ipmsg.MessageFactory;
import agano.runner.parameter.MessageReceivedParameter;
import agano.util.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NettyUdpServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyUdpServer.class);

    private final EventLoopGroup group;

    public interface Factory {
        public NettyUdpServer newInstance(int port);
    }

    @Inject
    public NettyUdpServer(EventBus eventBus, @Assisted int port) {
        this.group = new NioEventLoopGroup();

        new Bootstrap()
                .group(group)
                .channel(NioDatagramChannel.class)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {

                    @Override
                    protected void channelRead0(ChannelHandlerContext context, DatagramPacket packet) throws Exception {
                        logger.debug("{}", packet);
                        String messageStr = packet.content().toString(Charsets.shiftJIS());
                        try {
                            Message message = MessageFactory.fromString(messageStr, packet.recipient().getPort());
                            eventBus.post(new MessageReceivedParameter(message));
                        } catch (MalformedMessageException e) {
                            logger.info("Uninterpretable message", e);
                        }
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext context, Throwable t) {
                        logger.warn("Something is wrong tih udp connection", t);
                    }

                })
                .bind(port);
    }

    public Future shutdown() {
        return group.shutdownGracefully();
    }

    /**
     * see Effective Java Item.7
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            if (!group.isShutdown()) shutdown().await();
        } catch (InterruptedException e) {
            logger.warn("Failed to shutdown the server.", e);
        } finally {
            super.finalize();
        }
    }

}
