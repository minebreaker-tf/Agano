package agano.ipmsg;

import agano.util.Constants;
import agano.util.NetHelper;

import javax.annotation.Nonnull;
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

    @Nonnull
    public Message build() {
        return new Message(version, packetNumber, user, host, operation.getCode(), load, port);
    }

    private static AtomicLong last = new AtomicLong();

    private long generatePacketNumber() {
        long val = System.currentTimeMillis() / 1000;  // IPmsgに桁数をあわせる
        return last.updateAndGet(n -> n != val ? val : n + 1); // TODO 値がアプリ全体を通して重複しないようにする。そのうちもっといい実装に変更。
        // 実際には同期のパフォーマンスのせいで勝手にずれてくれるorz
        // 番号は時間である必要がないから、単純なインクリメントにするのがベストかも
    }

    // TODO Config化
    @Nonnull
    public MessageBuilder setUp(@Nonnull Operation operation, @Nonnull String load) {
        this.version = Constants.protocolVersion;
        this.packetNumber = generatePacketNumber();
        this.user = "default-user"; // TODO
        this.host = NetHelper.localhost().getHostName();
        this.operation = checkNotNull(operation);
        this.load = checkNotNull(load);
        this.port = Constants.defaultPort;

        return this;
    }

    @Nonnull
    public MessageBuilder setUp(@Nonnull Command command, @Nonnull String load) {
        return setUp(OperationBuilder.ofDefault(command).build(), load);
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
