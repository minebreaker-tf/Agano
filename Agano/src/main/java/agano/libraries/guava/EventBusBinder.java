package agano.libraries.guava;

import agano.runner.controller.*;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public final class EventBusBinder {

    private final EventBus eventBus;
    private final Controllers controllers;
    private final ReceiveMessageController receiveMessageController;
    private final SendMessageController sendMessageController;
    private final ReceiveFileController receiveFileController;
    private final SendFileController sendFileController;

    @Inject
    public EventBusBinder(
            EventBus eventBus,
            Controllers controllers,
            ReceiveMessageController receiveMessageController,
            SendMessageController sendMessageController,
            ReceiveFileController receiveFileController,
            SendFileController sendFileController) {
        this.eventBus = eventBus;
        this.controllers = controllers;
        this.receiveMessageController = receiveMessageController;
        this.sendMessageController = sendMessageController;
        this.receiveFileController = receiveFileController;
        this.sendFileController = sendFileController;
    }

    public void bind() {
        eventBus.register(controllers);
        eventBus.register(receiveMessageController);
        eventBus.register(sendMessageController);
        eventBus.register(receiveFileController);
        eventBus.register(sendFileController);
    }

}
