package agano.ipmsg;

import agano.util.Charsets;
import agano.util.Constants;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

import static agano.util.StringUtils.stringToByteBuffer;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 送受信に使われるメッセージを表す
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
            int port) {
        this.version = checkNotNull(version);
        this.packetNumber = packetNumber;
        this.user = checkNotNull(user);
        this.host = checkNotNull(host);
        this.operation = checkNotNull(new Operation(command));
        this.load = checkNotNull(load);
        this.port = port;
    }

    @Nonnull
    public ByteBuffer asByte() {
        return stringToByteBuffer(this.toString(), Charsets.shiftJIS());
    }

    /**
     * パケットの一意性は、「パケット番号：IPアドレス：ソースポート」で判断されます。
     * …ソースポートってなんだ
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return (int) (packetNumber >> 16) ^ (int) (packetNumber) + port; // TODO 実装が微妙なので要修正
    }

    // TODO: 実装が正しいか後で確認
    @Override
    public boolean equals(Object object) {
        return object != null && // TODO: GuavaのChainで置き換え
               object instanceof Message &&
               ((Message) object).getPacketNumber() == this.getPacketNumber() &&
               ((Message) object).getHost()
                                 .equals(this.getHost()) &&
               ((Message) object).getPort() == this.getPort();
    }

    /**
     * メッセージをそのまま送信できる形でString化します。
     * (i.e. getByte()でそのまま送信できます)
     *
     * @return このメッセージのString表現
     */
    @Override
    public String toString() {
        return Joiner.on(":")
                     .join(Constants.protocolVersion, packetNumber, user, host, operation, load);
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
