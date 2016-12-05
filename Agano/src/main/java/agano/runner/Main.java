package agano.runner;

import agano.config.Config;
import agano.config.ConfigModule;
import agano.ipmsg.MessageBuilder;
import agano.libraries.guava.EventBusBinder;
import agano.libraries.guava.EventBusModule;
import agano.messaging.ServerManager;
import agano.messaging.ServerModule;
import agano.messaging.TcpServer;
import agano.messaging.UdpServer;
import agano.runner.parameter.Parameters;
import agano.runner.state.StateManager;
import agano.runner.swing.MainForm;
import agano.runner.swing.SwingModule;
import agano.util.Constants;
import agano.util.NetHelper;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.net.BindException;
import java.net.InetSocketAddress;

import static agano.ipmsg.Command.IPMSG_BR_EXIT;

@Singleton
public final class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final MainForm form;
    private final UdpServer udpServer;
    private final TcpServer tcpServer;
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
                        "Failed to bind the socket.\nMaybe the port is already used by another application.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            System.exit(-1);
        }
    }

    @Inject
    public Main(
            MainForm.Factory formFactory,
            EventBus eventBus,
            EventBusBinder binder,
            StateManager stateManager,
            ServerManager serverManager,
            NetHelper netHelper,
            Config config) {

        this.form = formFactory.newInstance(this::shutdown);
        this.udpServer = serverManager.getUdpServer();
        this.tcpServer = serverManager.getTcpServer();
        this.netHelper = netHelper;
        this.config = config;

        stateManager.register(form);

        binder.bind();

        eventBus.post(new Parameters.RefreshParameter());

    }

    private void shutdown(WindowEvent event) {
        udpServer.submit(
                new MessageBuilder().setUp(config, IPMSG_BR_EXIT, "").build(),
                new InetSocketAddress(netHelper.broadcastAddress(), config.getPort())
        );
        Future fUdp = udpServer.shutdown();
        Future fTcp = tcpServer.shutdown();
        fUdp.syncUninterruptibly();
        fTcp.syncUninterruptibly();
        logger.debug("Application is about to shutdown successfully. Event: {}", event);
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
