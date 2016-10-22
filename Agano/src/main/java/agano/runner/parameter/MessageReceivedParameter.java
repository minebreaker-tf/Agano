package agano.runner.parameter;

import agano.ipmsg.Message;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MessageReceivedParameter {

    private final Message receivedMessage;

    public MessageReceivedParameter(@Nonnull Message recipient) {
        this.receivedMessage = checkNotNull(recipient);
    }

    @Nonnull
    public Message getMessage() {
        return receivedMessage;
    }

}
