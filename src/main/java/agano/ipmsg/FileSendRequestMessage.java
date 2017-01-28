package agano.ipmsg;

import com.google.common.base.Joiner;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * ファイル送信要求メッセージを表すためのクラスです.
 * 基本的にTCP通信で使われます.
 */
public final class FileSendRequestMessage extends Message {

    private final long requestPacketNumber;
    private final long requestFileID;
    private final long offset;

    public FileSendRequestMessage(
            @Nonnull String version,
            long packetNumber,
            @Nonnull String user,
            @Nonnull String host,
            @Nonnull Operation operation,
            int port,
            long requestPacketNumber,
            long requestFileID,
            long offset) {
        super(version, packetNumber, user, host, operation,
              Joiner.on(":").join(
                      Long.toHexString(requestPacketNumber),
                      Long.toHexString(requestFileID),
                      Long.toHexString(offset)
              ),
              port
        );
        this.requestPacketNumber = requestPacketNumber;
        this.requestFileID = requestFileID;
        this.offset = offset;

        checkArgument(operation.getCommand().equals(Command.IPMSG_GETFILEDATA), "Command [%s] should be IPMSG_GETFILEDATA", operation.getCommand());
    }

    public long getRequestPacketNumber() {
        return requestPacketNumber;
    }

    public long getRequestFileID() {
        return requestFileID;
    }

    public long getOffset() {
        return offset;
    }

}
