package agano.ipmsg;

import agano.util.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.StringJoiner;

/**
 * IP Messengerの添付ファイル情報を表すクラスです.
 */
public class Attachment {

    private final String fileID;
    private final String filename;
    private final long filesize;
    private final long mtime; // 最終変更時刻
    private final FileInfo fileInfo;

    public Attachment(
            String fileID,
            String filename,
            long fileSize,
            long mtime,
            FileInfo fileInfo) {
        this.fileID = fileID;
        this.filename = filename;
        this.filesize = fileSize;
        this.mtime = mtime;
        this.fileInfo = fileInfo;
    }

    public String getFileID() {
        return fileID;
    }

    public String getFilename() {
        return filename;
    }

    public long getFilesize() {
        return filesize;
    }

    public long getMtime() {
        return mtime;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    @Override
    public String toString() {
        return new StringJoiner(":").add(fileID)
                                    .add(filename)
                                    .add(Long.toHexString(filesize))
                                    .add(Long.toHexString(mtime))
                                    .add(fileInfo.toString())
                                    .toString();
    }

    public static String encodeAttachments(@Nonnull List<Attachment> attachments) {
        StringBuilder builder = new StringBuilder();
        for (Attachment each : attachments) {
            builder.append(each).append(StringUtils.fileDelimiter);
        }
        return builder.toString();
    }

}
