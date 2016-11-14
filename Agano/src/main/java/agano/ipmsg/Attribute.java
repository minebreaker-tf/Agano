package agano.ipmsg;

import java.util.EnumSet;

public enum Attribute {

    /* file attribute options for fileattach command */
    IPMSG_FILE_RONLYOPT(0x00000100),
    IPMSG_FILE_HIDDENOPT(0x00001000),
    IPMSG_FILE_EXHIDDENOPT(0x00002000),    // for MacOS X
    IPMSG_FILE_ARCHIVEOPT(0x00004000),
    IPMSG_FILE_SYSTEMOPT(0x00008000),

// 拡張属性は別物。実装時に別クラスにまとめる。
//    /* extend attribute types for fileattach command */
//    IPMSG_FILE_UID(0x00000001),
//    IPMSG_FILE_USERNAME(0x00000002),    // uid by string
//    IPMSG_FILE_GID(0x00000003),
//    IPMSG_FILE_GROUPNAME(0x00000004),    // gid by string
//    IPMSG_FILE_CLIPBOARDPOS(0x00000008),    //
//    IPMSG_FILE_PERM(0x00000010),    // for UNIX
//    IPMSG_FILE_MAJORNO(0x00000011),    // for UNIX devfile
//    IPMSG_FILE_MINORNO(0x00000012),    // for UNIX devfile
//    IPMSG_FILE_CTIME(0x00000013),    // for UNIX
//    IPMSG_FILE_MTIME(0x00000014),
//    IPMSG_FILE_ATIME(0x00000015),
//    IPMSG_FILE_CREATETIME(0x00000016),
//    IPMSG_FILE_CREATOR(0x00000020),    // for Mac
//    IPMSG_FILE_FILETYPE(0x00000021),    // for Mac
//    IPMSG_FILE_FINDERINFO(0x00000022),    // for Mac
//    IPMSG_FILE_ACL(0x00000030),
//    IPMSG_FILE_ALIASFNAME(0x00000040),    // alias fname
    ;

    private static final long optionMask = 0xffffff00;

    private final long value;

    Attribute(long value) {
        this.value = value;
    }

    public long getCode() {
        return value;
    }

    public static EnumSet<Attribute> find(long code) {
        EnumSet<Attribute> attributes = EnumSet.noneOf(Attribute.class);
        long optionCode = optionMask & code;

        for (Attribute each : Attribute.values()) {
            if ((each.getCode() & optionCode) != 0) {
                attributes.add(each);
            }
        }

        return attributes;
    }

}
