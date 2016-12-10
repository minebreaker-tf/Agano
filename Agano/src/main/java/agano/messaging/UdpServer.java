package agano.messaging;

import agano.ipmsg.Message;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
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
    public UdpServer(MessageInboundHandler messageInboundHandler, EventBus eventBus, @Assisted int port) {

        this.eventBus = eventBus;
        this.group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(messageInboundHandler);
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
            shutdown();
        } finally {
            super.finalize();
        }
    }

}
