package agano.ipmsg;

import agano.util.StringUtils;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.StringJoiner;

/**
 * IP Messengerの添付ファイル情報を表すクラスです.
 */
public class Attachment {

    private final long fileID;
    private final String filename;
    private final long filesize;
    private final long mtime; // 最終変更時刻
    private final FileInfo fileInfo;

    public Attachment(
            long fileID,
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

    public long getFileID() {
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
        return new StringJoiner(":").add(Long.toString(fileID))
                                    .add(filename)
                                    .add(Long.toHexString(filesize))
                                    .add(Long.toHexString(mtime / 1000))
                                    .add(fileInfo.toString())
                                    .toString();
    }

    public String explain() {
        return MoreObjects.toStringHelper(this)
                          .add("FileID", getFileID())
                          .add("Filename", getFilename())
                          .add("Filesize", getFilesize())
                          .add("Mtime", getMtime())
                          .add("FileInfo", getFileInfo())
                          .toString();
    }

    @Nonnull
    public static String encodeAttachments(@Nonnull List<? extends Attachment> attachments) {
        StringBuilder builder = new StringBuilder();
        for (Attachment each : attachments) {
            builder.append(each).append(StringUtils.fileDelimiter);
        }
        return builder.toString();
    }

    @Nonnull
    public static List<Attachment> decodeAttachments(@Nonnull String exp) {
        String[] attachments = exp.split(StringUtils.fileDelimiter);
        ImmutableList.Builder<Attachment> builder = ImmutableList.builder();
        for (String attachment : attachments) {
            String[] sep = attachment.split(":");
            builder.add(new Attachment(
                    Long.parseLong(sep[0]),
                    sep[1],
                    Long.parseLong(sep[2], 16),
                    Long.parseLong(sep[3], 16),
                    new FileInfo(Long.parseLong(sep[4], 16))
            ));
        }

        return builder.build();
    }

}
