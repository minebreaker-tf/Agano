package agano.ipmsg;

import agano.util.AganoException;

import java.util.EnumSet;
import java.util.NoSuchElementException;

// TODO スタブクラス
public class IpmsgProtocol {

    private static final long commandMask = 0x000000ff;
    private static final long optionMask = 0xffffff00;

    private final long value;

    public IpmsgProtocol(long value) {
        this.value = value;
    }

    public IpmsgProtocolCommand getCommand() {
        long thisCommand = value & commandMask;

        EnumSet<IpmsgProtocolCommand> commands = EnumSet.allOf(IpmsgProtocolCommand.class);
        for (IpmsgProtocolCommand each : commands) {
            if (each.getValue() == thisCommand) {
                return each;
            }
        }

        throw new AganoException(new NoSuchElementException(Long.toHexString(value)));
    }

    public long getAsValue() {
        return value;
    }

}
