package agano.messaging;

import agano.ipmsg.FileSendRequestMessage;
import agano.ipmsg.MalformedMessageException;
import agano.ipmsg.MessageFactory;
import agano.ipmsg.SendableAttachment;
import agano.runner.state.StateManager;
import com.google.inject.Inject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public final class FileSendingHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger logger = LoggerFactory.getLogger(FileSendingHandler.class);

    private final StateManager stateManager;

    @Inject
    public FileSendingHandler(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * ファイル送信リクエストを受信し、正当なリクエストだった場合ファイルを送信します.
     * <p>
     * {@inheritDoc}
     * </p>
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        logger.debug("Received packet: {}", msg);

        FileSendRequestMessage message;
        try {
            message = MessageFactory.fileSendRequestMessageFromString(
                    msg.toString(StandardCharsets.UTF_8).trim(),
                    ((InetSocketAddress) ctx.channel().remoteAddress()).getPort()
            );
        } catch (MalformedMessageException e) {
            logger.info("Uninterpretable file send request received.", e);
            ctx.close();
            return;
        }

        Optional<SendableAttachment> attached = stateManager
                .get().getSendableFiles().stream()
                .filter(each -> message.getRequestFileID() == each.getFileID() &&
                                message.getRequestPacketNumber() == each.getPacketNumber())
//                                message.getOffset() == each.getFilesize()) // TODO オフセットってひょっとして送信を始める箇所のオフセットの事か
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

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
        logger.warn("Something is wrong with tcp connection", t);
        ctx.close();
    }

}
