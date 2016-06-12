package agano.messaging;

import agano.util.AganoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import static com.google.common.base.Preconditions.checkNotNull;

public class UdpSender implements Closeable {

    private static Logger logger = LoggerFactory.getLogger(UdpSender.class);

    private final DatagramChannel channel;

    public UdpSender() {

        try {
            this.channel = DatagramChannel.open();
            this.channel.socket().setReuseAddress(true);
            this.channel.configureBlocking(false);
        } catch (IOException e) {
            logger.warn("Failed to initialize socket", e);
            throw new AganoException(e);
        }

    }

    public void send(@Nonnull SocketAddress address, @Nonnull ByteBuffer buf) {

        checkNotNull(address);
        checkNotNull(buf);

        try {
            channel.send(buf, address);
        } catch (IOException e) {
            logger.warn("Failed to send message", e);
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            logger.warn("Failed to close the channel", e);
        }
    }
}
