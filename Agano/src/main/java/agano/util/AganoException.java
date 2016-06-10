package agano.util;

public class AganoException extends RuntimeException {

    public AganoException() {
    }

    public AganoException(String description) {
        super(description);
    }

    public AganoException(Exception e) {
        super(e);
    }

}
