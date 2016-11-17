package agano.ipmsg;

import java.nio.file.Path;

public final class SendableAttachment extends Attachment {

    private final Path sendingFile;

    public SendableAttachment(
            long fileID,
            String filename,
            long fileSize,
            long mtime,
            FileInfo fileInfo,
            Path fileToSend) {
        super(fileID, filename, fileSize, mtime, fileInfo);
        this.sendingFile = fileToSend;
    }

    public Path getSendingFile() {
        return sendingFile;
    }

}
