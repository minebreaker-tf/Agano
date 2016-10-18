package agano.runner;

import agano.libraries.guice.EventBusModule;
import agano.messaging.NettyUdpServer;
import agano.runner.controller.Controller;
import agano.runner.swing.MainForm;
import agano.util.Constants;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

@Singleton
public final class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final MainForm form;
    private final NettyUdpServer udpServer;

    public static void main(String[] args) {

        setLaf();

        Injector injector = Guice.createInjector(new EventBusModule());
        Main application = injector.getInstance(Main.class);

        prepareWindow(application.form);

    }

    @Inject
    public Main(MainForm form, EventBus eventBus, Controller controller, NettyUdpServer udpServer) {
        this.form = form;
        this.udpServer = udpServer;

        eventBus.register(controller);
    }

    private static void setLaf() {
        try {
            UIManager.setLookAndFeel(Constants.defaultLaf);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            logger.warn("Failed to set laf.", e);
        }
    }

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
