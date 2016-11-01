package agano.runner.controller;

import agano.ipmsg.*;
import agano.messaging.ServerManager;
import agano.runner.parameter.MessageReceivedParameter;
import agano.runner.parameter.SendMessageParameter;
import agano.runner.state.StateManager;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static agano.ipmsg.Command.*;

@Singleton
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final StateManager stateManager;
    private final ServerManager serverManager;

    @Inject
    public Controller(StateManager stateManager, ServerManager serverManager) {
        this.stateManager = stateManager;
        this.serverManager = serverManager;
    }

    @Subscribe
    public void sendMessage(SendMessageParameter parameter) {
        logger.debug("Sending: {}", parameter.getMessage());

        // TEST
        serverManager.getUdpServer().submit(
                new MessageBuilder().setUp(
                        OperationBuilder.ofDefault(IPMSG_SENDMSG)
                                        //.add(Option.IPMSG_NOADDLISTOPT)
                                        .add(Option.IPMSG_SENDCHECKOPT).build(),
                        parameter.getMessage()
                ).build(),
                new InetSocketAddress("192.168.0.12", 2425)
        );

        stateManager.swap(state -> state.swapChatText(parameter.getMessage()));
    }

    // TODO コントローラー分割
    @Subscribe
    public void receiveMessage(MessageReceivedParameter parameter) {
        logger.debug("Received: {}", parameter.getMessage().explain());

        Message received = parameter.getMessage();
        InetSocketAddress senderAddress = new InetSocketAddress(received.getHost(), 2425);
        Operation op = received.getOperation();

        if (op.getCommand().equals(IPMSG_BR_ENTRY)) {
            serverManager.getUdpServer().submit(
                    new MessageBuilder().setUp(IPMSG_ANSENTRY, "").build(),
                    senderAddress
            );
        } else if (op.getCommand().equals(IPMSG_SENDMSG)) {
            logger.info("Message received: {}", received.getLoad());
            if (op.isEnabledOption(Option.IPMSG_SENDCHECKOPT)) {
                serverManager.getUdpServer().submit(
                        new MessageBuilder().setUp(IPMSG_RECVMSG, "").build(),
                        senderAddress
                );
            }
        }

    }

}
