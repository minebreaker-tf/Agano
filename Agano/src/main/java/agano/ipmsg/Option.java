package agano.ipmsg;

import java.util.EnumSet;

public enum Option {

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
