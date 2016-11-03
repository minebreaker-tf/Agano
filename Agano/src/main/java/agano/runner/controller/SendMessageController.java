package agano.runner.controller;

import agano.ipmsg.MessageBuilder;
import agano.ipmsg.OperationBuilder;
import agano.ipmsg.Option;
import agano.messaging.ServerManager;
import agano.runner.parameter.SendMessageParameter;
import agano.runner.state.StateManager;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static agano.ipmsg.Command.IPMSG_SENDMSG;

@Singleton
public final class SendMessageController {

    private static final Logger logger = LoggerFactory.getLogger(SendMessageController.class);

    private final StateManager stateManager;
    private final ServerManager serverManager;

    @Inject
    public SendMessageController(StateManager stateManager, ServerManager serverManager) {
        this.stateManager = stateManager;
        this.serverManager = serverManager;
    }

    @Subscribe
    public void sendMessage(SendMessageParameter parameter) {
        logger.debug("Sending: {}", parameter.getMessage());

        // TODO: Impl for test
        serverManager.getUdpServer().submit(
                new MessageBuilder().setUp(
                        OperationBuilder.ofDefault(IPMSG_SENDMSG)
                                        //.add(Option.IPMSG_NOADDLISTOPT)
                                        .add(Option.IPMSG_SENDCHECKOPT).build(),
                        parameter.getMessage()
                ).build(),
                new InetSocketAddress("192.168.0.12", 2425)
        );

//        stateManager.swap(state -> state.addTalkToUser());
    }

}
