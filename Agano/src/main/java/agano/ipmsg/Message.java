package agano.ipmsg;

import agano.util.Constants;
import agano.util.StringUtils;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 送受信に使われるメッセージを表すクラスです.
 */
public final class Message {

    private final String version;
    private final long packetNumber;
    private final String user;
    private final String host;
    private final Operation operation;
    private final String load;
    private final int port;
    private final List<Attachment> attachments;

    /**
     * @param version      フォーマットバージョン
     * @param packetNumber パケット番号(一意性の判断に使われる)
     * @param user         自ユーザー名
     * @param host         自ホスト名/IPアドレス
     * @param command      命令を表す番号
     * @param load         拡張部
     * @param port         メッセージの送信元ポート
     */
    public Message(
            @Nonnull String version,
            long packetNumber,
            @Nonnull String user,
            @Nonnull String host,
            long command,
            @Nonnull String load,
            int port,
            List<Attachment> attachments) {
        this(version, packetNumber, user, host, new Operation(command), load, port, attachments);
    }

    public Message(
            @Nonnull String version,
            long packetNumber,
            @Nonnull String user,
            @Nonnull String host,
            @Nonnull Operation command,
            @Nonnull String load,
            int port,
            @Nonnull List<Attachment> attachments) {
        this.version = checkNotNull(version);
        this.packetNumber = packetNumber;
        this.user = checkNotNull(user);
        this.host = checkNotNull(host);
        this.operation = checkNotNull(command);
        this.load = checkNotNull(load);
        this.port = port;
        this.attachments = ImmutableList.copyOf(attachments);
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
        StringBuilder exp = new StringBuilder();
        exp.append(
                Joiner.on(":").join(Constants.protocolVersion, packetNumber, user, host, operation, load));
        if (!attachments.isEmpty()) {
            exp.append(StringUtils.nullPointer)
               .append(Attachment.encodeAttachments(attachments));
        }

        return exp.toString();
    }

    public String explain() {
        return MoreObjects.toStringHelper(this)
                          .add("Version", version)
                          .add("PacketNumber", packetNumber)
                          .add("User", user)
                          .add("Host", host)
                          .add("Operation", operation.explain())
                          .add("Load", load)
                          .add("Port", port)
                          .add("Attachments", attachments)
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

    @Nonnull
    public List<Attachment> getAttachments() {
        return attachments;
    }

}
