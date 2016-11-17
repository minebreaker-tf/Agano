package agano.runner.controller;

import agano.config.Config;
import agano.ipmsg.MessageBuilder;
import agano.ipmsg.OperationBuilder;
import agano.messaging.ServerManager;
import agano.messaging.UdpServer;
import agano.runner.parameter.Parameters;
import agano.runner.state.State;
import agano.runner.state.StateManager;
import agano.util.NetHelper;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.net.InetSocketAddress;

import static agano.ipmsg.Command.IPMSG_BR_ENTRY;
import static agano.ipmsg.Command.IPMSG_NOOPERATION;

@Singleton
public final class Controllers {

    private final StateManager manager;
    private final UdpServer udpServer;
    private final Config config;
    private final NetHelper netHelper;

    @Inject
    public Controllers(StateManager stateManager, ServerManager serverManager, Config config, NetHelper netHelper) {
        this.manager = stateManager;
        this.udpServer = serverManager.getUdpServer();
        this.config = config;
        this.netHelper = netHelper;
    }

    @Subscribe
    public void select(Parameters.SelectionParameter parameter) {
        manager.swap(state -> state.selectUser(parameter.getSelected()));
    }

    @Subscribe
    public void windowFocused(Parameters.WindowFocusedParameter parameter) {
        manager.swap(state -> state.changeFocus(parameter.isWindowFocused()));
    }

    @Subscribe
    public void refresh(Parameters.RefreshParameter parameter) {

        manager.swap(State::clearUser);

        /*"default-user\0\0\nUN:default-user\nHN:main\nNN:default-nickname\nGN:"*/
        udpServer.submit(
                new MessageBuilder().setUp(config, IPMSG_NOOPERATION, "").build(),
                new InetSocketAddress(netHelper.broadcastAddress(), config.getPort())
        );
        udpServer.submit(
                new MessageBuilder().setUp(
                        config,
                        OperationBuilder.ofDefault(IPMSG_BR_ENTRY)
                                        .build(),
                        ""
                ).build(),
                new InetSocketAddress(netHelper.broadcastAddress(), config.getPort())
        );
    }

}
