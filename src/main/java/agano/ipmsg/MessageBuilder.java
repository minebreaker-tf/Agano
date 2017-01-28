package agano.ipmsg;

import agano.config.Config;
import agano.util.Constants;
import agano.util.NetHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MessageBuilder {

    private String version;
    private long packetNumber;
    private String user;
    private String host;
    private Operation operation;
    private String load;
    private int port;

    private List<? extends Attachment> attachments;

    private long requestPacketNumber;
    private long requestFileID;
    private long requestOffset;

    @Nonnull
    public Message build() {
        return new Message(version, packetNumber, user, host, operation.getCode(), load, port);
    }

    @Nonnull
    public FileAttachedMessage buildFileAttachedMessage() {
        checkNotNull(attachments);
        return new FileAttachedMessage(version, packetNumber, user, host, operation, load, port, attachments);
    }

    @Nonnull
    public FileSendRequestMessage buildFileSendRequest() {
        return new FileSendRequestMessage(version, packetNumber, user, host, operation, port, requestPacketNumber, requestFileID, requestOffset);
    }

    private static AtomicLong last = new AtomicLong();

    public static long generatePacketNumber() {
        long val = System.currentTimeMillis() / 1000;  // IPmsgに桁数をあわせる
        return last.updateAndGet(n -> n != val ? val : n + 1); // TODO 値がアプリ全体を通して重複しないようにする。そのうちもっといい実装に変更。
        // 番号は時間である必要がないから、単純なインクリメントにするのがベストかも
    }

    @Nonnull
    public MessageBuilder setUp(@Nonnull Config config, @Nonnull Operation operation, @Nonnull String load) {
        checkNotNull(config);

        this.version = Constants.protocolVersion;
        this.packetNumber = generatePacketNumber();
        this.user = config.getUsername();
        this.host = NetHelper.localhost().getHostName();
        this.operation = checkNotNull(operation);
        this.load = checkNotNull(load);
        this.port = config.getPort();

        return this;
    }

    @Nonnull
    public MessageBuilder setUp(@Nonnull Config config, @Nonnull Command command, @Nonnull String load) {
        return setUp(config, OperationBuilder.ofDefault(command).build(), load);
    }

    @Deprecated
    @Nonnull
    public MessageBuilder setUp(@Nonnull Operation operation, @Nonnull String load) {
        this.version = Constants.protocolVersion;
        this.packetNumber = generatePacketNumber();
        this.user = "default-user";
        this.host = NetHelper.localhost().getHostName();
        this.operation = checkNotNull(operation);
        this.load = checkNotNull(load);
        this.port = Constants.defaultPort;

        return this;
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

    @Nonnull
    public MessageBuilder attachments(List<? extends Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    @Nonnull
    public MessageBuilder fileSendRequest(long requestPacketNumber, long requestFileID, long requestOffset) {
        this.requestPacketNumber = requestPacketNumber;
        this.requestFileID = requestFileID;
        this.requestOffset = requestOffset;
        return this;
    }

}
