package agano.runner.controller;

import agano.config.Config;
import agano.ipmsg.*;
import agano.messaging.ServerManager;
import agano.runner.parameter.SendFileParameter;
import agano.runner.state.StateManager;
import agano.runner.state.User;
import agano.util.AganoException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public final class SendFileController {

    private static final Logger logger = LoggerFactory.getLogger(SendFileController.class);

    private final StateManager stateManager;
    private final ServerManager serverManager;
    private final EventBus eventBus;
    private final Config config;

    private final AtomicLong fileIdMaker;

    @Inject
    public SendFileController(StateManager stateManager, ServerManager serverManager, EventBus eventBus, Config config) {
        this.stateManager = stateManager;
        this.serverManager = serverManager;
        this.eventBus = eventBus;
        this.config = config;

        this.fileIdMaker = new AtomicLong();
    }

    @Subscribe
    public void sendFile(SendFileParameter parameter) {
        logger.debug("Sending: {}, File: {}", parameter.getMessage(), parameter.getAttachments());

        Optional<User> user = stateManager.get().getSelectedUser();
        if (!user.isPresent()) return;

        long packetNumber = MessageBuilder.generatePacketNumber();
        List<SendableAttachment> attachments = new ArrayList<>();
        parameter.getAttachments().forEach(path -> {
            if (!Files.exists(path)) {
                logger.info("Attached file does not exist.");
                JOptionPane.showMessageDialog(
                        (Component) stateManager.getObserver(),
                        "File does not exist.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!Files.isRegularFile(path)) throw new AganoException("Sending directory is currently not supported."); // TODO

            try {
                SendableAttachment attachment = new SendableAttachment(
                        fileIdMaker.incrementAndGet(),
                        path.toFile().getName(),
                        Files.size(path),
                        Files.getLastModifiedTime(path).toMillis(),
                        new FileInfo(FileType.IPMSG_FILE_REGULAR),
                        path,
                        packetNumber
                );
                attachments.add(attachment);
                stateManager.swap(state -> state.addSendableFile(attachment));
            } catch (IOException e) {
                throw new AganoException(e);
            }
        });

        FileAttachedMessage msg = new MessageBuilder()
                .setUp(
                        config,
                        OperationBuilder.of(Command.IPMSG_SENDMSG)
                                        .add(Option.IPMSG_SENDCHECKOPT)
                                        .add(Option.IPMSG_UTF8OPT)
                                        .add(Option.IPMSG_FILEATTACHOPT)
                                        .build(),
                        parameter.getMessage()
                )
                .packetNumber(packetNumber)
                .attachments(attachments)
                .buildFileAttachedMessage();

        serverManager.getUdpServer().submit(msg, user.get().getAddress());

//        eventBus.post(new SendMessageParameter(parameter.getMessage()));

    }

}
