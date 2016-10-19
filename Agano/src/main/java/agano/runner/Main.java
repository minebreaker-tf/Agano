package agano.runner;

import agano.libraries.guice.EventBusModule;
import agano.messaging.NettyUdpServer;
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

        prepareWindow(application.form);

    }

    @Inject
    public Main(
            MainForm.Factory formFactory,
            EventBus eventBus,
            StateManager stateManager,
            Controller controller,
            NettyUdpServer.Factory udpServerFactory) {

        this.udpServer = udpServerFactory.newInstance(Constants.port);
        this.form = formFactory.newInstance(event -> {
            try {
                udpServer.shutdown().sync();
            } catch (InterruptedException e) {
                logger.warn("Failed to shutdown the server.", e);
            }
            logger.debug("Application is about to shutdown successfully. Event: {}", event);
            System.exit(0);
        });
        stateManager.register(form);

        eventBus.register(controller);

    }

    private static void setLaf() {
        try {
            UIManager.setLookAndFeel(Constants.defaultLaf);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            logger.warn("Failed to set laf.", e);
        }
    }

    // TODO
    private static MainForm prepareWindow(MainForm form) {
        try {
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
