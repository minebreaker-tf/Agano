package agano.ipmsg;

import agano.util.AganoException;

public class MalformedMessageException extends AganoException {

    public MalformedMessageException(String raw) {
        super("Invalid message format: " + raw);
    }

}
