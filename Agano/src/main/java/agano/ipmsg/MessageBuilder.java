package agano.ipmsg;

import agano.util.AganoException;
import agano.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MessageBuilder {

    private static final Logger logger = LoggerFactory.getLogger(MessageBuilder.class);

    private String version;
    private long packetNumber;
    private String user;
    private String host;
    private Operation operation;
    private String load;
    private int port;

    @Nonnull
    public Message build() {
        return new Message(version, packetNumber, user, host, operation.getCode(), load, port);
    }

    @Nonnull
    public MessageBuilder setUp(@Nonnull Operation operation, @Nonnull String load) {
        this.version = Constants.protocolVersion;
        this.packetNumber = System.currentTimeMillis();
        this.user = "default-user"; // TODO
        try {
            this.host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.warn("Failed to detect local address.", e);
            throw new AganoException(e);
        }
        this.operation = checkNotNull(operation);
        this.load = checkNotNull(load);
        this.port = Constants.defaultPort;

        return this;
    }

    @Nonnull
    public MessageBuilder setUp(@Nonnull Command command, @Nonnull String load) {
        return setUp(OperationBuilder.of(command).build(), load);
    }

    @Nonnull
    public MessageBuilder version(@Nonnull String version) {
        this.version = checkNotNull(version);
        return this;
    }

    @Nonnull
    public MessageBuilder packetNumber(long packetNumber) {
        this.packetNumber = packetNumber;
        return this;
    }

    @Nonnull
    public MessageBuilder user(@Nonnull String user) {
        this.user = checkNotNull(user);
        return this;
    }

    @Nonnull
    public MessageBuilder host(@Nonnull String host) {
        this.host = checkNotNull(host);
        return this;
    }

    @Nonnull
    public MessageBuilder operation(@Nonnull Operation operation) {
        this.operation = checkNotNull(operation);
        return this;
    }

    @Nonnull
    public MessageBuilder load(@Nonnull String load) {
        this.load = checkNotNull(load);
        return this;
    }

    @Nonnull
    public MessageBuilder port(int port) {
        this.port = port;
        return this;
    }

}
