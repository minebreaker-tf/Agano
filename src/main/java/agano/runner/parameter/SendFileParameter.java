package agano.runner.parameter;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SendFileParameter {

    private final String message;
    private final Set<Path> attachments;

    public SendFileParameter(@Nonnull String message, @Nonnull Set<Path> attachments) {
        this.message = message;
        this.attachments = checkNotNull(attachments);
    }

    @Nonnull
    public String getMessage() {
        return message;
    }

    @Nonnull
    public Set<Path> getAttachments() {
        return attachments;
    }

}
