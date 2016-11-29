package agano.messaging;

import agano.ipmsg.FileSendRequestMessage;
import agano.ipmsg.MalformedMessageException;
import agano.ipmsg.MessageFactory;
import agano.ipmsg.SendableAttachment;
import agano.runner.state.StateManager;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public final class TcpServer {

    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    private final StateManager stateManager;
    private final EventLoopGroup group;
    private final EventLoopGroup workerGroup;

    public interface Factory {
        public TcpServer newInstance(int port);
    }

    @Inject
    public TcpServer(StateManager stateManager, @Assisted int port) {
        this.stateManager = stateManager;
        this.group = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group, workerGroup)
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new Handler())
                 .bind(port).syncUninterruptibly();
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
            logger.warn("Failed to shutdown the tcp server.", e);
        } finally {
            super.finalize();
        }
    }

    private final class Handler extends SimpleChannelInboundHandler<ByteBuf> {

        // ファイル送信リクエストを受信
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            logger.debug("Received packet: {}", msg);

            String strMessage = msg.toString(StandardCharsets.UTF_8).trim();
            try {
                FileSendRequestMessage message = MessageFactory.fileSendRequestMessageFromString(
                        strMessage,
                        ((InetSocketAddress) ctx.channel().remoteAddress()).getPort()
                );

                Optional<SendableAttachment> attached = stateManager
                        .get().getSendableFiles().stream()
                        .peek(each -> System.out.println(each.explain()))
                        .filter(each -> message.getRequestFileID() == each.getFileID() &&
                                         message.getRequestPacketNumber() == each.getPacketNumber())
//                                        message.getOffset() == each.getFilesize()) // TODO オフセットってひょっとして送信を始める箇所のオフセットの事か
                        .findAny();

                if (attached.isPresent()) {

                    ByteBuf buf = ctx.alloc().buffer();
                    buf.writeBytes(Files.readAllBytes(attached.get().getSendingFile())); // まあIPMsgでバカでかいファイルを送る人はいないだろう
                    ctx.writeAndFlush(buf)
                       .addListener(f -> ctx.close());

                } else {
                    logger.info("Unregistered send request received: {}", message);
                    ctx.close();
                }

            } catch (MalformedMessageException e) {
                logger.info("Uninterpretable file send request received.", e);
            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
            logger.warn("Something is wrong with tcp connection", t);
            ctx.close();
        }
    }

}
