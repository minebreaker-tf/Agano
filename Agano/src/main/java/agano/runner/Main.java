package agano.runner;

import agano.ipmsg.MessageBuilder;
import agano.ipmsg.OperationBuilder;
import agano.ipmsg.Option;
import agano.libraries.guice.EventBusModule;
import agano.messaging.NettyUdpServer;
import agano.messaging.ServerManager;
import agano.messaging.ServerModule;
import agano.runner.controller.Controller;
import agano.runner.state.StateManager;
import agano.runner.swing.MainForm;
import agano.runner.swing.SwingModule;
import agano.util.Constants;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;

import static agano.ipmsg.Command.IPMSG_BR_ENTRY;
import static agano.ipmsg.Command.IPMSG_NOOPERATION;

@Singleton
public final class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final MainForm form;
    private final NettyUdpServer udpServer;

    public static void main(String[] args) {

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        setLaf();

        Injector injector = Guice.createInjector(new EventBusModule(), new SwingModule(), new ServerModule());
        Main application = injector.getInstance(Main.class);
    }

    @Inject
    public Main(
            MainForm.Factory formFactory,
            EventBus eventBus,
            StateManager stateManager,
            Controller controller,
            ServerManager serverManager) {

        this.udpServer = serverManager.getUdpServer();
        this.form = formFactory.newInstance(event -> {
            try {
                udpServer.shutdown().sync();
                logger.debug("Application is about to shutdown successfully. Event: {}", event);
            } catch (InterruptedException e) {
                logger.warn("Failed to shutdown the server.", e);
            }
            System.exit(0);
        });
        prepareWindow(form);
        stateManager.register(form);

        eventBus.register(controller);

        String greeting = "";
        serverManager.getUdpServer().submit(
                new MessageBuilder().setUp(IPMSG_NOOPERATION, "").build(),
                new InetSocketAddress("192.168.0.12", Constants.defaultPort)
        );
        serverManager.getUdpServer().submit(
                new MessageBuilder().setUp(OperationBuilder.of(IPMSG_BR_ENTRY)
                                                           .add(Option.IPMSG_ABSENCEOPT)
                                                           .add(Option.IPMSG_SENDCHECKOPT)
                                                           .add(Option.IPMSG_READCHECKOPT)
                                                           .add(Option.IPMSG_SECRETOPT)
                                                           .add(Option.IPMSG_SECRETEXOPT)
                                                           .build(), "display-name\0\nUN:default-user\nHN:main\nNN:default-nickname\nGN:").build(),
                new InetSocketAddress("192.168.0.12", Constants.defaultPort)
        );

    }

    private static void setLaf() {
        try {
            UIManager.setLookAndFeel(Constants.defaultLaf);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            logger.warn("Failed to set laf.", e);
        }
    }

    // TODO Config
    private static MainForm prepareWindow(MainForm form) {
        try {
            // TODO Noto Sans
            Font defaultFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    Main.class.getResourceAsStream(Constants.defaultFont)
            );
            defaultFont = defaultFont.deriveFont(Font.PLAIN, Constants.defaultFontSize);

        } catch (FontFormatException | IOException e) {
            logger.warn("Failed to load font.", e);
        }

        return form;
    }

}
