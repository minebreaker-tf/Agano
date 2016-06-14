package agano.ipmsg;

import java.util.NoSuchElementException;

public final class IpmsgProtocolUtils {

    public static final long commandMask = 0x000000ff;
    public static final long optionMask = 0xffffff00;

    IpmsgProtocol getCommand(long value) {
        long command = commandMask & value;
        for (IpmsgProtocol each : IpmsgProtocol.values()) {
            if (each.getValue() == command)
                return each;
        }

        throw new NoSuchElementException("No valid command found");
    }

    IpmsgProtocol getOption(long value) {
        long option = optionMask & value;
        for (IpmsgProtocol each : IpmsgProtocol.values()) {
            if (each.getValue() == option) {
                return each;
            }
        }

        throw new NoSuchElementException("No valid option found");
    }

}
