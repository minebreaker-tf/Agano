package agano.runner;

import agano.config.Config;
import agano.config.ConfigModule;
import agano.ipmsg.MessageBuilder;
import agano.ipmsg.OperationBuilder;
import agano.libraries.guava.EventBusModule;
import agano.messaging.NettyUdpServer;
import agano.messaging.ServerManager;
import agano.messaging.ServerModule;
import agano.runner.controller.Controller;
import agano.runner.controller.ReceiveMessageController;
import agano.runner.controller.SendMessageController;
import agano.runner.state.StateManager;
import agano.runner.swing.MainForm;
import agano.runner.swing.SwingModule;
import agano.util.Constants;
import agano.util.NetHelper;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.net.BindException;
import java.net.InetSocketAddress;

import static agano.ipmsg.Command.*;

@Singleton
public final class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final MainForm form;
    private final NettyUdpServer udpServer;
    private final NetHelper netHelper;
    private final Config config;

    public static void main(String[] args) {
        try {

            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();

            setLaf();

            Guice.createInjector(new EventBusModule(), new SwingModule(), new ServerModule(), new ConfigModule())
                 .getInstance(Main.class);

        } catch (Throwable t) {
            logger.error("Fatal error had occurred.", t);
            if (t.getCause() instanceof BindException) {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to bind the socket.\nMaybe the port is already used by an another application.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            System.exit(-1);
        }
    }

    @Inject
    public Main(
            MainForm.Factory formFactory,
            EventBus eventBus,
            StateManager stateManager,
            Controller controller,
            ReceiveMessageController receiveMessageController,
            SendMessageController sendMessageController,
            ServerManager serverManager,
            NetHelper netHelper,
            Config config) {

        this.form = formFactory.newInstance(this::shutdown);
        this.udpServer = serverManager.getUdpServer();
        this.netHelper = netHelper;
        this.config = config;

        stateManager.register(form);

        eventBus.register(controller);
        eventBus.register(receiveMessageController);
        eventBus.register(sendMessageController);

        /*"default-user\0\0\nUN:default-user\nHN:main\nNN:default-nickname\nGN:"*/
        udpServer.submit(
                new MessageBuilder().setUp(IPMSG_NOOPERATION, "default-user").build(),
                new InetSocketAddress(netHelper.broadcastAddress(), config.getPort())
        );
        udpServer.submit(
                new MessageBuilder().setUp(
                        OperationBuilder.ofDefault(IPMSG_BR_ENTRY)
                                        .build(),
                        ""
                ).build(),
                new InetSocketAddress(netHelper.broadcastAddress(), config.getPort())
        );

    }

    private void shutdown(WindowEvent event) {
        udpServer.submit(
                new MessageBuilder().setUp(IPMSG_BR_EXIT, "").build(),
                new InetSocketAddress(netHelper.broadcastAddress(), config.getPort())
        );
        try {
            udpServer.shutdown().sync();
            logger.debug("Application is about to shutdown successfully. Event: {}", event);
        } catch (InterruptedException e) {
            logger.warn("Failed to shutdown the server.", e);
        }
        System.exit(0);
    }

    private static void setLaf() {
        try {
            UIManager.setLookAndFeel(Constants.defaultLaf);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            logger.warn("Failed to set laf.", e);
        }
    }

}
