package agano.ipmsg;

import com.google.common.base.Optional;

import javax.annotation.Nonnull;
import java.util.EnumSet;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Operation {

    private final long code;
    private final Command command;
    private final EnumSet<Option> options;

    public Operation(long raw) {
        this.code = raw;

        Optional<Command> value = Command.find(code);
        command = value.or(Command.IPMSG_NOOPERATION);

        this.options = Option.find(code);
    }

    Operation(Command command, EnumSet<Option> options) {
        checkNotNull(command);
        checkNotNull(options);

        this.command = command;
        this.options = options.clone();

        long code = command.getCode();
        for (Option op : options) {
            code |= op.getCode();
        }

        this.code = code;
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

    public boolean isEnabledOption(@Nonnull Option option) {
        checkNotNull(option);
        return options.contains(option);
    }

    @Override
    public boolean equals(Object other) {
        return other != null &&
               other instanceof Operation &&
               this.code == ((Operation) other).code;
    }

    @Override
    public String toString() {
        return Long.toString(code);
    }

}
