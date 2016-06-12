package agano.messaging;

import agano.util.AganoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UdpSender {

    private static Logger logger = LoggerFactory.getLogger(UdpSender.class);

    private final DatagramChannel channel;
    private final SocketAddress address;

    public UdpSender(SocketAddress address) {

        this.address = address;

        try {
            this.channel = DatagramChannel.open();
            this.channel.socket().setReuseAddress(true);
            this.channel.configureBlocking(false);
            this.channel.socket().bind(address);
            this.channel.connect(address);
        } catch (IOException e) {
            logger.warn("Failed to initialize socket", e);
            throw new AganoException(e);
        }

    }

    public void send(ByteBuffer buf) {
        try {
            channel.write(buf);
        } catch (IOException e) {
            logger.warn("Failed to send message", e);
        }
    }

}
