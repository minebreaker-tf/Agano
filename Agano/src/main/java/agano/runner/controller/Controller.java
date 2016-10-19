package agano.runner.controller;

import agano.runner.parameter.MessageReceivedParameter;
import agano.runner.parameter.SendMessageParameter;
import agano.runner.state.StateManager;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final StateManager manager;

    @Inject
    public Controller(StateManager manager) {
        this.manager = manager;
    }

    @Subscribe
    public void sendMessage(SendMessageParameter parameter) {
        logger.debug("Sending: {}", parameter.getMessage());

        manager.swap(state -> state.swapChatText(parameter.getMessage()));
    }

    @Subscribe
    public void receiveMessage(MessageReceivedParameter parameter) {
        logger.debug("Received: {}", parameter.getRecipant());

        
    }

}
