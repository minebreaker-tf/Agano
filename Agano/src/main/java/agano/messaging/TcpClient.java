package agano.messaging;

import agano.ipmsg.Message;
import com.google.inject.Inject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TcpClient {

    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);

    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Bootstrap bootstrap;

    @Inject
    public TcpClient() {
        try {
            bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                            // ファイル受信

                            ctx.close();
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
                            logger.warn("Error while receiving the file.", t);
                            ctx.close();
                        }
                    });

        } finally {
            group.shutdownGracefully();
        }
    }

    @Nonnull
    public ChannelFuture submit(@Nonnull Message message, @Nonnull InetSocketAddress destination) {
        ByteBuf buf = Unpooled.copiedBuffer(checkNotNull(message).toString(), StandardCharsets.UTF_8);
        logger.debug("Sending: {}", buf);
        return bootstrap.connect(checkNotNull(destination)).channel().writeAndFlush(buf);
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
            logger.warn("Failed to shutdown the tcp client.", e);
        } finally {
            super.finalize();
        }
    }

}
