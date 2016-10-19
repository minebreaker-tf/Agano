package agano.runner.parameter;

import agano.ipmsg.Message;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MessageReceivedParameter {

    private final Message recipant;

    public MessageReceivedParameter(@Nonnull Message recipient) {
        this.recipant = checkNotNull(recipient);
    }

    @Nonnull
    public Message getRecipant() {
        return recipant;
    }

}
