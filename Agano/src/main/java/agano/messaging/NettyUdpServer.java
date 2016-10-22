package agano.messaging;

import agano.ipmsg.MalformedMessageException;
import agano.ipmsg.Message;
import agano.ipmsg.MessageFactory;
import agano.runner.parameter.MessageReceivedParameter;
import agano.util.AganoException;
import agano.util.Charsets;
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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public final class NettyUdpServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyUdpServer.class);

    private final EventLoopGroup group;
    private final Channel channel;

    public interface Factory {
        public NettyUdpServer newInstance(int port);
    }

    @Inject
    public NettyUdpServer(EventBus eventBus, @Assisted int port) {

        this.group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {

                    @Override
                    protected void channelRead0(ChannelHandlerContext context, DatagramPacket packet) throws Exception {
                        logger.debug("Received packet: {}", packet);

                        // Discard packets from self.
                        if (packet.sender().getAddress().equals(InetAddress.getLocalHost())) return;

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
                        logger.warn("Something is wrong with udp connection", t);
                    }

                });
        try {
            this.channel = bootstrap.bind(port).sync().channel();
        } catch (InterruptedException e) {
            throw new AganoException(e);
        }

    }

    public ChannelFuture submit(Message message, InetSocketAddress destination) {
        ByteBuffer buf = message.asByte();
        buf.flip();
        DatagramPacket packet = new DatagramPacket(Unpooled.copiedBuffer(buf).retain(), destination);
        logger.debug("Sending packet: {}", packet);
        return channel.writeAndFlush(packet);
    }

    public ChannelFuture submit(String message, Charset encoding, InetSocketAddress destination) {
        DatagramPacket packet = new DatagramPacket(Unpooled.copiedBuffer(message, encoding).retain(), destination);
        logger.debug("Sending packet: {}", packet);
        return channel.writeAndFlush(packet);
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
