package agano.libraries.guava;

import agano.runner.controller.Controllers;
import agano.runner.controller.ReceiveMessageController;
import agano.runner.controller.SendFileController;
import agano.runner.controller.SendMessageController;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public final class EventBusBinder {

    private final EventBus eventBus;
    private final Controllers controllers;
    private final ReceiveMessageController receiveMessageController;
    private final SendMessageController sendMessageController;
    private final SendFileController sendFileController;

    @Inject
    public EventBusBinder(
            EventBus eventBus,
            Controllers controllers,
            ReceiveMessageController receiveMessageController,
            SendMessageController sendMessageController,
            SendFileController sendFileController) {
        this.eventBus = eventBus;
        this.controllers = controllers;
        this.receiveMessageController = receiveMessageController;
        this.sendMessageController = sendMessageController;
        this.sendFileController = sendFileController;
    }

    public void bind() {
        eventBus.register(controllers);
        eventBus.register(receiveMessageController);
        eventBus.register(sendMessageController);
        eventBus.register(sendFileController);
    }

}
