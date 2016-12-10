package agano.messaging;

import agano.ipmsg.FileSendRequestMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TCP経由でリクエストを送信するためのクラスです.
 * 現状では一度{@code submit()}を呼び出すと、チャンネルがクローズされます.
 */
public final class TcpClient {

    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);

    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Component mainForm;

    public TcpClient(@Nullable Component mainForm) {
        this.mainForm = mainForm;
    }

    /**
     * ファイル送信リクエストをTCPポートに送信します.
     * 要求が受け入れられた場合、ファイルの受信を開始します.
     *
     * @param message     ファイルが添付されていたメッセージ
     * @param destination リクエストの送信先
     * @param size        転送されるファイルサイズ
     * @param saveTo      保存先パス
     * @return リクエストを送信したFuture(ファイル転送の完了ではない)
     */
    @Nonnull
    public ChannelFuture submit(
            @Nonnull FileSendRequestMessage message,
            @Nonnull InetSocketAddress destination,
            int size,
            @Nonnull Path saveTo) {

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
                        ch.pipeline().addLast(new FileReceivingHandler(saveTo, size, mainForm, TcpClient.this::shutdown));
                    }
                });
        Channel ch = bootstrap.connect(destination).syncUninterruptibly().channel();
        ByteBuf buf = ch.alloc().buffer();
        buf.writeCharSequence(message.toString(), StandardCharsets.UTF_8);
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
            shutdown();
        } finally {
            super.finalize();
        }
    }

}
