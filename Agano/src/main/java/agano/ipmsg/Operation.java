package agano.ipmsg;

import com.google.common.base.Optional;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public final class Operation {

    private final long code;
    private final Command command;
    private final EnumSet<Option> options;

    public Operation(long raw) {
        this.code = raw;

        Optional<Command> value = Command.find(code);
        if (value.isPresent()) {
            command = value.get();
        } else {
            command = Command.IPMSG_NOOPERATION;
        }

        options = Option.find(code);
    }

    public long get() {
        return code;
    }

    @Nonnull
    public Command getCommand() {
        return command;
    }

    @Nonnull
    public EnumSet<Option> getOptions() {
        return EnumSet.copyOf(options);
    }

    public boolean isEnabledOption(Option option) {
        return options.contains(option);
    }

    @Override
    public String toString() {
        return Long.toString(code);
    }

}
