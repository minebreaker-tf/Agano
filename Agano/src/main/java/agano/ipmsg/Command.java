package agano.ipmsg;

import com.google.common.base.Optional;

public enum Command {

    /** 意味なし */
    IPMSG_NOOPERATION(0x00000000),

    /** エントリー通知 */
    IPMSG_BR_ENTRY(0x00000001),
    IPMSG_BR_EXIT(0x00000002),
    IPMSG_ANSENTRY(0x00000003),
    IPMSG_BR_ABSENCE(0x00000004),;

    private static final long commandMask = 0x000000ff;

    private final long value;

    Command(long value) {
        this.value = value;
    }

    public long getCode() {
        return value;
    }

    public static Optional<Command> find(long code) {
        long commandCode = commandMask & code;
        for (Command each : Command.values()) {
            if (each.getCode() == commandCode) {
                return Optional.of(each);
            }
        }

        return Optional.absent();
    }

}
