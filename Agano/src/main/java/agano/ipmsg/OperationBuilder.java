package agano.ipmsg;

import javax.annotation.Nonnull;
import java.util.EnumSet;

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
        checkNotNull(command);
        return new OperationBuilder(command);
    }

    @Nonnull
    public static OperationBuilder ofDefault(@Nonnull Command command) {
        checkNotNull(command);
        OperationBuilder builder = new OperationBuilder(command);
        builder.operations.addAll(defaultOperations);
        return builder;
    }

    @Nonnull
    public OperationBuilder add(@Nonnull Option option) {
        checkNotNull(option);
        operations.add(option);
        return this;
    }

    @Nonnull
    public Operation build() {
        return new Operation(command, operations);
    }

}
