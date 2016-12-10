package agano.runner.controller;

import agano.config.Config;
import agano.ipmsg.*;
import agano.messaging.TcpClient;
import agano.runner.parameter.ReceiveFileParameter;
import agano.runner.state.StateManager;
import agano.runner.swing.MainForm;
import agano.runner.swing.SwingUtils;
import agano.util.AganoException;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public final class ReceiveFileController {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveFileController.class);

    private final StateManager stateManager;
    private final Config config;

    @Inject
    public ReceiveFileController(StateManager stateManager, Config config) {
        this.stateManager = stateManager;
        this.config = config;
    }

    @Subscribe
    public void sendReceiveFileRequest(ReceiveFileParameter parameter) {
        logger.debug("Sending file request for attachment: {}", parameter.getFileAttachedMessage());

        FileAttachedMessage fileAttachedMessage = parameter.getFileAttachedMessage();
        Attachment attachment = parameter.getWantedAttachment();

        checkArgument(fileAttachedMessage.getAttachments().contains(attachment));
        checkArgument(
                attachment.getFileInfo().getFileType().equals(FileType.IPMSG_FILE_REGULAR), // TODO
                "Receiving non-regular file is not currently supported. FileInfo: %s", attachment
        );

        FileSendRequestMessage requestMessage = new MessageBuilder()
                .setUp(
                        config,
                        Command.IPMSG_GETFILEDATA,
                        ""
                )
                .fileSendRequest(
                        fileAttachedMessage.getPacketNumber(),
                        attachment.getFileID(),
                        0
                )
                .buildFileSendRequest();

        if (stateManager.getObserver() == null) throw new AganoException("Invalid state.");
        Optional<Path> saveTo = SwingUtils.showFileSaveDialog(
                ((MainForm) stateManager.getObserver()).getFrame(), attachment.getFileName());
        if (!saveTo.isPresent()) return;

        new TcpClient(((MainForm) stateManager.getObserver()).getFrame()).submit(
                requestMessage,
                new InetSocketAddress(fileAttachedMessage.getHost(), fileAttachedMessage.getPort()),
                (int) attachment.getFilesize(),
                saveTo.get()
        ).addListener(f -> logger.info("Sent file send request."));
    }

}
