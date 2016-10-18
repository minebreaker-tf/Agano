package agano.runner.parameter;

import agano.ipmsg.Message;

public final class MessageReceivedParameter {

    private final Message recipant;

    public MessageReceivedParameter(Message recipient) {
        this.recipant = recipient;
    }

    public Message getRecipant() {
        return recipant;
    }

}
