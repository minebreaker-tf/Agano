package agano.runner.parameter;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SendMessageParameter {

    private final String message;

    public SendMessageParameter(@Nonnull String message) {
        this.message = checkNotNull(message);
    }

    @Nonnull
    public String getMessage() {
        return message;
    }

}
