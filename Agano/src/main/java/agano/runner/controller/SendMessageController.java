package agano.runner.controller;

import agano.config.Config;
import agano.ipmsg.Message;
import agano.ipmsg.MessageBuilder;
import agano.ipmsg.OperationBuilder;
import agano.ipmsg.Option;
import agano.messaging.ServerManager;
import agano.runner.parameter.SendMessageParameter;
import agano.runner.state.StateManager;
import agano.runner.state.User;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static agano.ipmsg.Command.IPMSG_SENDMSG;

@Singleton
public final class SendMessageController {

    private static final Logger logger = LoggerFactory.getLogger(SendMessageController.class);

    private final StateManager stateManager;
    private final ServerManager serverManager;
    private final Config config;

    @Inject
    public SendMessageController(StateManager stateManager, ServerManager serverManager, Config config) {
        this.stateManager = stateManager;
        this.serverManager = serverManager;
        this.config = config;
    }

    @Subscribe
    public void sendMessage(SendMessageParameter parameter) {
        logger.debug("Sending: {}", parameter.getMessage());

        Optional<User> selected = stateManager.get().getSelectedUser();
        if (!selected.isPresent()) return;
        User user = selected.get();

        Message msg = new MessageBuilder().setUp(
                config,
                OperationBuilder.ofDefault(IPMSG_SENDMSG)
                                //.add(Option.IPMSG_NOADDLISTOPT)
                                .add(Option.IPMSG_SENDCHECKOPT)
                                .add(Option.IPMSG_UTF8OPT).build(),
                parameter.getMessage()
        ).build();

        serverManager.getUdpServer().submit(msg, user.getAddress());

        stateManager.swap(state -> state.addTalkToUser(user, msg));
    }

}
