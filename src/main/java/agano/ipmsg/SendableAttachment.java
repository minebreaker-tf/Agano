package agano.ipmsg;

import java.nio.file.Path;

public final class SendableAttachment extends Attachment {

    private final Path sendingFile;
    private final long packetNumber;

    public SendableAttachment(
            long fileID,
            String filename,
            long fileSize,
            long mtime,
            FileInfo fileInfo,
            Path fileToSend,
            long packetNumber) {
        super(fileID, filename, fileSize, mtime, fileInfo);
        this.sendingFile = fileToSend;
        this.packetNumber = packetNumber;
    }

    public Path getSendingFile() {
        return sendingFile;
    }

    public long getPacketNumber() {
        return packetNumber;
    }

}
