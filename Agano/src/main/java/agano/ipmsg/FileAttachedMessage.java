package agano.ipmsg;

import agano.util.StringUtils;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ファイルが添付されたメッセージです.
 * 添付ファイルは拡張部に、下記の書式で指定されます.
 * <pre>
 *     fileID:filename:size:mtime:fileattr[:extend-attr=val1[,val2...][:extend-attr2=...]]:\a[:]fileID...
 * </pre>
 * 拡張属性には現在未対応です.
 */
public final class FileAttachedMessage extends Message {

    private final List<? extends Attachment> attachments;

    /**
     * {@inheritDoc}
     *  @param version
     * @param packetNumber
     * @param user
     * @param host
     * @param operation
     * @param load         添付ファイル情報を含まない拡張部
     * @param port
     * @param attachments  添付ファイルリスト
     */
    public FileAttachedMessage(
            @Nonnull String version,
            long packetNumber,
            @Nonnull String user,
            @Nonnull String host,
            @Nonnull Operation operation,
            @Nonnull String load, // 添付ファイル情報を含まない
            int port,
            @Nonnull List<? extends Attachment> attachments) {
        super(version, packetNumber, user, host, operation, load, port);
        this.attachments = checkNotNull(attachments);

        checkArgument(operation.getCommand().equals(Command.IPMSG_SENDMSG), "Command [%s] should be IPMSG_SENDMSG.", operation.getCommand());
        checkArgument(operation.isEnabledOption(Option.IPMSG_FILEATTACHOPT), "IPMSG_FILEATTACHOPT is disabled.");
    }

    @Nonnull
    public List<? extends Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public String toString() {
        StringBuilder exp = new StringBuilder();

        exp.append(super.toString());

        if (!attachments.isEmpty()) {
            exp.append(StringUtils.nullPointer)
               .append(Attachment.encodeAttachments(attachments));
        }

        return exp.toString();
    }

    @Nonnull
    public String explain() {
        return MoreObjects.toStringHelper(this)
                          .add("Version", getVersion())
                          .add("PacketNumber", getPacketNumber())
                          .add("User", getUser())
                          .add("Host", getHost())
                          .add("Operation", getOperation().explain())
                          .add("Load", getLoad())
                          .add("Port", getPort())
                          .add("Attachments", getAttachments())
                          .toString();
    }

}
