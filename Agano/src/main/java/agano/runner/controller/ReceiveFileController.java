package agano.runner.controller;

import agano.config.Config;
import agano.ipmsg.*;
import agano.messaging.TcpClient;
import agano.runner.parameter.ReceiveFileParameter;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkArgument;

public final class ReceiveFileController {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveFileController.class);

    private final Config config;

    @Inject
    public ReceiveFileController(Config config) {
        this.config = config;
    }

    @Subscribe
    public void sendReceiveFileRequest(ReceiveFileParameter parameter) {
        logger.debug("Sending file request for attachment: {}", parameter.getFileAttachedMessage());

        FileAttachedMessage fileAttachedMessage = parameter.getFileAttachedMessage();
        Attachment attachment = parameter.getWantedAttachment();

        checkArgument(fileAttachedMessage.getAttachments().contains(attachment));

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

        TcpClient tcpClient = new TcpClient();
        tcpClient.submit(
                requestMessage,
                new InetSocketAddress(fileAttachedMessage.getHost(), fileAttachedMessage.getPort()),
                (int) attachment.getFilesize(),
                Paths.get(System.getProperty("user.home"), attachment.getFilename()) // TODO
        ).addListener(f -> logger.info("Sent file send request."));
    }

}
