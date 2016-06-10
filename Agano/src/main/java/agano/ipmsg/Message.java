package agano.ipmsg;

import javax.annotation.Nullable;

public final class Message {

    private final int status;
    private final String text;

    public Message(int status, @Nullable String text) {
        this.status = status;
        this.text = text != null ? text : "";
    }

    public Message(int status) {
        this(status, "");
    }

    public int getStatus() {
        return status;
    }

    public String getText() {
        return text;
    }

}
