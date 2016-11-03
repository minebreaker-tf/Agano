package agano.runner.controller;

import agano.ipmsg.Message;
import agano.ipmsg.MessageBuilder;
import agano.ipmsg.Operation;
import agano.ipmsg.Option;
import agano.messaging.ServerManager;
import agano.runner.parameter.MessageReceivedParameter;
import agano.runner.state.StateManager;
import agano.runner.state.User;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Optional;

import static agano.ipmsg.Command.IPMSG_ANSENTRY;
import static agano.ipmsg.Command.IPMSG_RECVMSG;

@Singleton
public final class ReceiveMessageController {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveMessageController.class);

    private final StateManager stateManager;
    private final ServerManager serverManager;

    @Inject
    public ReceiveMessageController(StateManager stateManager, ServerManager serverManager) {
        this.stateManager = stateManager;
        this.serverManager = serverManager;
    }

    // TODO コントローラー分割
    @Subscribe
    public void receiveMessage(MessageReceivedParameter parameter) {
        logger.debug("Received: {}", parameter.getMessage().explain());

        Message received = parameter.getMessage();
        InetSocketAddress senderAddress = new InetSocketAddress(received.getHost(), received.getPort());
        Operation op = received.getOperation();

        User sender = addUser(received); // データを受信した時点で、相手を登録しておく

        switch (op.getCommand()) {
        case IPMSG_BR_ENTRY:
            serverManager.getUdpServer().submit(
                    new MessageBuilder().setUp(IPMSG_ANSENTRY, "").build(),
                    senderAddress
            );
            break;
        case IPMSG_ANSENTRY:
            // NOP
            break;
        case IPMSG_BR_EXIT:
            // TODO
            break;
        case IPMSG_SENDMSG:
            receivedSendOp(sender, received);
            break;
        default:
            logger.info("Unknown message received: {}", received);
        }

    }

    private void receivedSendOp(User sender, Message received) {
        logger.info("Message received: {}", received.getLoad());
        if (received.getOperation().isEnabledOption(Option.IPMSG_SENDCHECKOPT)) {
            serverManager.getUdpServer().submit(
                    new MessageBuilder().setUp(IPMSG_RECVMSG, "").build(),
                    sender.getAddress()
            );
        }
        stateManager.swap(state -> state.addTalkToUser(sender, received));
    }

    private User addUser(Message received) {
        Optional<User> knownUser = stateManager.get().getUsers().stream()
                                               .filter(user -> user.getName().equals(received.getUser()) &&
                                                               user.getAddress().getHostName().equals(received.getHost()))
                                               .findAny();

        if (knownUser.isPresent()) {
            return knownUser.get();
        } else {
            User newUser = new User(
                    received.getUser(),
                    new InetSocketAddress(received.getHost(), received.getPort()),
                    Collections.emptyList()
            );

            if (!received.getOperation().getOptions().contains(Option.IPMSG_NOADDLISTOPT))
                stateManager.swap(state -> state.addUser(newUser));

            return newUser;
        }
    }

}
