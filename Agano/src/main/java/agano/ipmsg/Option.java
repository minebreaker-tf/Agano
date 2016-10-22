package agano.ipmsg;

import java.util.EnumSet;

public enum Option {

    /*  option for all command  */
    IPMSG_ABSENCEOPT(0x00000100),
    IPMSG_SERVEROPT(0x00000200),
    IPMSG_DIALUPOPT(0x00010000),
    IPMSG_FILEATTACHOPT(0x00200000),
    IPMSG_ENCRYPTOPT(0x00400000),
    IPMSG_UTF8OPT(0x00800000),
    IPMSG_CAPUTF8OPT(0x01000000),
    IPMSG_ENCEXTMSGOPT(0x04000000),
    IPMSG_CLIPBOARDOPT(0x08000000),
    IPMSG_CAPFILEENCOPT(0x00001000),

    /*  option for SENDMSG command  */
    IPMSG_SENDCHECKOPT(0x00000100),
    IPMSG_SECRETOPT(0x00000200),
    IPMSG_BROADCASTOPT(0x00000400),
    IPMSG_MULTICASTOPT(0x00000800),
    IPMSG_AUTORETOPT(0x00002000),
    IPMSG_RETRYOPT(0x00004000),
    IPMSG_PASSWORDOPT(0x00008000),
    IPMSG_NOLOGOPT(0x00020000),
    IPMSG_NOADDLISTOPT(0x00080000),
    IPMSG_READCHECKOPT(0x00100000),
    IPMSG_SECRETEXOPT(IPMSG_READCHECKOPT.value | IPMSG_SECRETOPT.value),;

    private static final long optionMask = 0xffffff00;

    private final long value;

    Option(long value) {
        this.value = value;
    }

    public long getCode() {
        return value;
    }

    public static EnumSet<Option> find(long code) {
        EnumSet<Option> options = EnumSet.noneOf(Option.class);
        long optionCode = optionMask & code;

        for (Option each : Option.values()) {
            if ((each.getCode() & optionCode) != 0) {
                options.add(each);
            }
        }

        return options;
    }

}
