package agano.runner.action;

import agano.config.Config;
import agano.ipmsg.MessageBuilder;
import agano.ipmsg.OperationBuilder;
import agano.messaging.NettyUdpServer;
import agano.messaging.ServerManager;
import agano.runner.state.State;
import agano.runner.state.StateManager;
import agano.util.NetHelper;
import com.google.inject.Inject;

import java.net.InetSocketAddress;

import static agano.ipmsg.Command.IPMSG_BR_ENTRY;
import static agano.ipmsg.Command.IPMSG_NOOPERATION;

public final class RefreshAction {


    private final StateManager manager;
    private final NettyUdpServer udpServer;
    private final Config config;
    private final NetHelper netHelper;

    @Inject
    public RefreshAction(StateManager stateManager, ServerManager serverManager, Config config, NetHelper netHelper) {
        this.manager = stateManager;
        this.udpServer = serverManager.getUdpServer();
        this.config = config;
        this.netHelper = netHelper;
    }

    public void execute() {

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
