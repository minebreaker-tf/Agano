package agano.ipmsg;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public final class OperationBuilder {

    private static final EnumSet<Option> defaultOperations = EnumSet.of(
            Option.IPMSG_CAPUTF8OPT,
            Option.IPMSG_FILEATTACHOPT
    );

    private Command command;
    private EnumSet<Option> operations;

    private OperationBuilder(Command command) {
        this.command = command;
        this.operations = EnumSet.noneOf(Option.class);
    }

    @Nonnull
    public static OperationBuilder of(@Nonnull Command command) {
        return new OperationBuilder(checkNotNull(command));
    }

    @Nonnull
    public static OperationBuilder ofDefault(@Nonnull Command command) {
        OperationBuilder builder = new OperationBuilder(checkNotNull(command));
        builder.operations.addAll(defaultOperations);
        return builder;
    }

    @Nonnull
    public OperationBuilder add(@Nonnull Option option) {
        operations.add(checkNotNull(option));
        return this;
    }

    @Nonnull
    public OperationBuilder addAll(@Nonnull Set<Option> option) {
        checkNotNull(option);
        option.forEach(operations::add);
        return this;
    }

    @Nonnull
    public Operation build() {
        return new Operation(command, operations);
    }

}
