package agano.messaging;

import agano.ipmsg.Message;
import agano.util.AganoException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TcpClient {

    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);

    /** ファイルが添付された関連するメッセージ. */
    private static final AttributeKey<Message> MESSAGE = AttributeKey.valueOf("MESSAGE");
    /** 保存先に指定されたファイルのパス(ファイル名を含む). */
    private static final AttributeKey<Path> PATH = AttributeKey.valueOf("PATH");
    /** 転送されるファイルのサイズ. */
    private static final AttributeKey<Integer> SIZE = AttributeKey.valueOf("SIZE");

    private final EventLoopGroup group = new NioEventLoopGroup();

    @Nonnull
    public ChannelFuture submit(@Nonnull Message message, @Nonnull InetSocketAddress destination, int size, @Nonnull Path saveTo) {
        checkNotNull(message);
        checkNotNull(destination);
        checkNotNull(saveTo);

        logger.debug("Sending: {} Destination: {}", message, destination);
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() { // TODO fragmentation issue
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                msg.retain();

                                // ファイル受信
                                Message relevant = ctx.channel().attr(MESSAGE).get();
                                if (relevant == null) throw new AganoException("Something is wrong with application state.");
                                Path saveTo = ctx.channel().attr(PATH).get();
                                if (saveTo == null) throw new AganoException("Something is wrong with application state.");
                                int size = ctx.channel().attr(SIZE).get();

                                logger.info("Receiving file. File: {} Size: {}", saveTo, size);

                                try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(saveTo))) { // Truncate file if exists
                                    msg.readBytes(bos, size);
                                } catch (IOException e) {
                                    throw new AganoException("Failed to write file.", e);
                                } catch (IndexOutOfBoundsException e) {
                                    throw new AganoException("Sent file size differs from claimed one.", e);
                                } finally {
                                    msg.release();
                                    ctx.close();
                                    group.shutdownGracefully();
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
                                logger.warn("Error while receiving the file.", t);
                                ctx.close();
                                group.shutdownGracefully();
                            }
                        });
                    }
                });
        Channel ch = bootstrap.connect(destination).syncUninterruptibly().channel();
        ch.attr(MESSAGE).set(message);
        ch.attr(PATH).set(saveTo);
        ch.attr(SIZE).set(size);
        ByteBuf buf = ch.alloc().buffer();
        buf.writeCharSequence(message.toString() + ":", StandardCharsets.UTF_8);
        return ch.writeAndFlush(buf);
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
