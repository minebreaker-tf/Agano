package agano.ipmsg;

import java.util.Optional;

public enum FileType {

    /* file types for fileattach command */
    IPMSG_FILE_REGULAR(0x00000001),
    IPMSG_FILE_DIR(0x00000002),
    IPMSG_FILE_RETPARENT(0x00000003),    // return parent directory
    IPMSG_FILE_SYMLINK(0x00000004),
    IPMSG_FILE_CDEV(0x00000005),    // for UNIX
    IPMSG_FILE_BDEV(0x00000006),    // for UNIX
    IPMSG_FILE_FIFO(0x00000007),    // for UNIX
    IPMSG_FILE_RESFORK(0x00000010),    // for Mac
    IPMSG_FILE_CLIPBOARD(0x00000020),    // for Windows Clipboard
    ;

    private static final long mask = 0x000000ff;

    private final long value;

    FileType(long value) {
        this.value = value;
    }

    public long getCode() {
        return value;
    }

    public static Optional<FileType> find(long code) {
        long optionCode = mask & code;

        for (FileType each : FileType.values()) {
            if ((each.getCode() == optionCode)) {
                return Optional.of(each);
            }
        }

        return Optional.empty();
    }
}
