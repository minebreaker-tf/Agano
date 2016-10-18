package agano.messaging;

import agano.ipmsg.Message;
import agano.ipmsg.MessageFactory;
import agano.ipmsg.MalformedMessageException;
import agano.runner.parameter.MessageReceivedParameter;
import agano.util.Charsets;
import agano.util.Constants;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public final class NettyUdpServer implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(NettyUdpServer.class);

    private final EventLoopGroup group;

    @Inject
    public NettyUdpServer(EventBus eventBus) {
        this.group = new NioEventLoopGroup();

        new Bootstrap().group(group)
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
                .bind(Constants.port);
    }

    @Override
    public void close() throws IOException {
        group.shutdownGracefully();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}
