package agano.ipmsg;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public final class Message {

    private final int status;
    private final String text;

    public Message(int status, @Nullable String text) {
        this.status = status;
        this.text = text != null ? text : "";
    }

    public static Message fromByte(ByteBuffer original) {
        return null;
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
