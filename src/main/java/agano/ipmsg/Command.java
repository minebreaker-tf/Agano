package agano.ipmsg;

import java.util.Optional;

@SuppressWarnings("SpellCheckingInspection")
public enum Command {

    /** 意味なし */
    IPMSG_NOOPERATION(0x00000000),

    /** エントリー通知 */
    IPMSG_BR_ENTRY(0x00000001),
    /** 退出通知 */
    IPMSG_BR_EXIT(0x00000002),
    /** エントリー通知応答 */
    IPMSG_ANSENTRY(0x00000003),
    /** 不在通知……多分 */
    IPMSG_BR_ABSENCE(0x00000004),

    // ホストリスト関連
    IPMSG_BR_ISGETLIST(0x00000010),
    IPMSG_OKGETLIST(0x00000011),
    IPMSG_GETLIST(0x00000012),
    IPMSG_ANSLIST(0x00000013),
    IPMSG_BR_ISGETLIST2(0x00000018),

    /** メッセージ送信 */
    IPMSG_SENDMSG(0x00000020),
    /** 受信通知 (メッセージを受信したときに返信する) */
    IPMSG_RECVMSG(0x00000021),
    /** 既読通知 */
    IPMSG_READMSG(0x00000030),
    // 削除ってなんだ
    IPMSG_DELMSG(0x00000031),
    /** 既読通知返信? */
    IPMSG_ANSREADMSG(0x00000032),

    IPMSG_GETINFO(0x00000040L),
    IPMSG_SENDINFO(0x00000041L),

    IPMSG_GETABSENCEINFO(0x00000050),
    IPMSG_SENDABSENCEINFO(0x00000051),

    IPMSG_GETFILEDATA(0x00000060),
    IPMSG_RELEASEFILES(0x00000061),
    IPMSG_GETDIRFILES(0x00000062),

    IPMSG_GETPUBKEY(0x00000072L),
    IPMSG_ANSPUBKEY(0x00000073L),;

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

        return Optional.empty();
    }

}
