package agano.ipmsg;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 命令とオプションをひとまとめにしたクラス
 */
public final class Operation {

    private final long code;
    private final Command command;
    private final EnumSet<Option> options;

    public Operation(long raw) {
        this.code = raw;

        Optional<Command> value = Command.find(code);
        command = value.orElse(Command.IPMSG_NOOPERATION);

        this.options = Option.find(code);
    }

    Operation(@Nonnull Command command, @Nonnull EnumSet<Option> options) {

        this.command = checkNotNull(command);
        this.options = checkNotNull(options).clone();

        long code = command.getCode();
        for (Option op : options) {
            code |= op.getCode();
        }

        this.code = code;
    }

    public long getCode() {
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
        return options.contains(checkNotNull(option));
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

    public String explain() {
        return MoreObjects.toStringHelper(this)
                          .add("Command", command)
                          .add("Options", options)
                          .toString();
    }

}
