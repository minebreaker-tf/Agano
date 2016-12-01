package agano.runner.parameter;

import agano.ipmsg.Attachment;
import agano.ipmsg.FileAttachedMessage;

public final class ReceiveFileParameter {

    private final FileAttachedMessage fileAttachedMessage;
    private final Attachment wantedAttachment;

    public ReceiveFileParameter(FileAttachedMessage fileAttachedMessage, Attachment wantedAttachment) {
        this.fileAttachedMessage = fileAttachedMessage;
        this.wantedAttachment = wantedAttachment;
    }

    public FileAttachedMessage getFileAttachedMessage() {
        return fileAttachedMessage;
    }

    public Attachment getWantedAttachment() {
        return wantedAttachment;
    }

}
