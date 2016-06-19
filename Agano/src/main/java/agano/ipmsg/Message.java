package agano.ipmsg;

import agano.util.Charsets;
import agano.util.Constants;
import com.google.common.base.Joiner;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

import static agano.util.StringUtils.stringToByteBuffer;

/**
 * 送受信に使われるメッセージを表す
 */
public class Message {

    private final String version;
    private final long packetNumber;
    private final String user;
    private final String host;
    private final long command;
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
    // パッケージプライベートにしようかなあ
    public Message(
            @Nonnull String version,
            long packetNumber,
            @Nonnull String user,
            @Nonnull String host, long command, @Nonnull String load, int port) {
        this.version = version;
        this.packetNumber = packetNumber;
        this.user = user;
        this.host = host;
        this.command = command;
        this.load = load;
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
        return object != null &&
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
                .join(Constants.protocolVersion, packetNumber, user, host, command, load);
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

    public long getCommand() {
        return command;
    }

    @Nonnull
    public String getLoad() {
        return load;
    }

    public int getPort() {
        return port;
    }

}
