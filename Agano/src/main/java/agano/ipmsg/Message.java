package agano.ipmsg;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class Message {

    private final String version;
    private final long packetNumber;
    private final String user;
    private final String host;
    private final long command;
    private final String load;

    public Message(
            @Nonnull String version,
            long packetNumber,
            @Nonnull String user,
            @Nonnull String host,
            long command,
            @Nonnull String load) {
        this.version = version;
        this.packetNumber = packetNumber;
        this.user = user;
        this.host = host;
        this.command = command;
        this.load = load;
    }

    /**
     * パケットの一意性は、「パケット番号：IPアドレス：ソースポート」で判断されます。
     * …ソースポートってなんだ
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return 34; // TODO
    }

    // TODO: 実装が正しいか後で確認
    @Override
    public boolean equals(Object object) {
        return object != null &&
                object instanceof  Message &&
                object.hashCode() == this.hashCode();
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

}
