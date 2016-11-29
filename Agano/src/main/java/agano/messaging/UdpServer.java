package agano.messaging;

import agano.ipmsg.MalformedMessageException;
import agano.ipmsg.Message;
import agano.ipmsg.MessageFactory;
import agano.runner.parameter.MessageReceivedParameter;
import agano.util.NetHelper;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkNotNull;

public final class UdpServer {

    private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);

    private final EventBus eventBus;
    private final EventLoopGroup group;
    private final Channel channel;

    public interface Factory {
        public UdpServer newInstance(int port);
    }

    @Inject
    public UdpServer(EventBus eventBus, @Assisted int port) {

        this.eventBus = eventBus;
        this.group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new InboundHandler());
        this.channel = bootstrap.bind(port).syncUninterruptibly().channel();
    }

    @Nonnull
    public ChannelFuture submit(@Nonnull Message message, @Nonnull InetSocketAddress destination) {
        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(checkNotNull(message).toString(), StandardCharsets.UTF_8),
                checkNotNull(destination)
        );
        return submit(packet);
    }

    @Nonnull
    public ChannelFuture submit(@Nonnull String message, @Nonnull Charset encoding, @Nonnull InetSocketAddress destination) {
        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(checkNotNull(message), checkNotNull(encoding)),
                checkNotNull(destination)
        );
        return submit(packet);
    }

    private ChannelFuture submit(DatagramPacket packet) {
        logger.debug("Sending packet: {}", packet);
        return channel.writeAndFlush(packet);
    }

    @Nonnull
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
            logger.warn("Failed to shutdown the udp server.", e);
        } finally {
            super.finalize();
        }
    }

    private final class InboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        @Override
        protected void channelRead0(ChannelHandlerContext context, DatagramPacket packet) {
            logger.debug("Received packet: {}", packet);

            if (packet.sender().getAddress().equals(NetHelper.localhost())) return; // TODO Localhost判定が変

            String strMessage = packet.content().toString(StandardCharsets.UTF_8).trim();
            Message message = MessageFactory.fromString(strMessage, packet.recipient().getPort());
            try {
                eventBus.post(new MessageReceivedParameter(message));
            } catch (MalformedMessageException e) {
                logger.info("Uninterpretable message received.", e);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable t) {
            logger.warn("Something is wrong with udp connection", t);
        }

    }

}
