package agano.ipmsg;

import agano.util.Constants;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 送受信に使われるメッセージを表すクラスです.
 */
public class Message {

    private final String version;
    private final long packetNumber;
    private final String user;
    private final String host;
    private final Operation operation;
    private final String load;
    private final int port;

    /**
     * @param version      フォーマットバージョン
     * @param packetNumber パケット番号(一意性の判断に使われる)
     * @param user         自ユーザー名
     * @param host         自ホスト名/IPアドレス
     * @param operation      命令を表す番号
     * @param load         拡張部
     * @param port         メッセージの送信元ポート
     */
    public Message(
            @Nonnull String version,
            long packetNumber,
            @Nonnull String user,
            @Nonnull String host,
            long operation,
            @Nonnull String load,
            int port) {
        this.version = checkNotNull(version);
        this.packetNumber = packetNumber;
        this.user = checkNotNull(user);
        this.host = checkNotNull(host);
        this.operation = new Operation(operation);
        this.load = checkNotNull(load);
        this.port = port;
    }

    public Message(
            @Nonnull String version,
            long packetNumber,
            @Nonnull String user,
            @Nonnull String host,
            @Nonnull Operation operation,
            @Nonnull String load,
            int port) {
        this(version, packetNumber, user, host, operation.getCode(), load, port);
    }

    /**
     * {@inheritDoc}
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash(packetNumber, port, host);
    }

    /**
     * Messageクラスの等価性はパケット・ホスト・ポート[のみ]によって判断されます.
     * オペレーションや積み荷が異なっていても、等かと判断されるので注意してください.
     * この実装はIP Messengerの仕様です.
     *
     * @param other 比較対象
     * @return 比較結果
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Message &&
               this.packetNumber == ((Message) other).packetNumber &&
               this.host.equals(((Message) other).host) &&
               this.port == ((Message) other).port;
    }

    /**
     * メッセージをそのまま送信できる形でString化します.
     *
     * @return このメッセージのString表現
     */
    @Override
    public String toString() {
        String ret = Joiner.on(":").join(Constants.protocolVersion, packetNumber, user, host, operation);
        if (!load.isEmpty()) ret += ":" + load;
        return ret;
    }

    @Nonnull
    public String explain() {
        return MoreObjects.toStringHelper(this)
                          .add("Version", version)
                          .add("PacketNumber", packetNumber)
                          .add("User", user)
                          .add("Host", host)
                          .add("Operation", operation.explain())
                          .add("Load", load)
                          .add("Port", port)
                          .toString();
    }

    @Nonnull
    public String getVersion() {
        return version;
    }

    public long getPacketNumber() {
        return packetNumber;
    }

    @Nonnull
    public String getUser() {
        return user;
    }

    @Nonnull
    public String getHost() {
        return host;
    }

    @Nonnull
    public Operation getOperation() {
        return operation;
    }

    @Nonnull
    public String getLoad() {
        return load;
    }

    public int getPort() {
        return port;
    }

}
