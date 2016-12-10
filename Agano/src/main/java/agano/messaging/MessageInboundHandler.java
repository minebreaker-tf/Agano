package agano.messaging;

import agano.ipmsg.MalformedMessageException;
import agano.ipmsg.Message;
import agano.ipmsg.MessageFactory;
import agano.runner.parameter.MessageReceivedParameter;
import agano.util.NetHelper;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public final class MessageInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger logger = LoggerFactory.getLogger(MessageInboundHandler.class);
    private final EventBus eventBus;

    @Inject
    public MessageInboundHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

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
