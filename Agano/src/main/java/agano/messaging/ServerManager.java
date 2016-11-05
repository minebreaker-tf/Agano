package agano.messaging;

import agano.config.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class ServerManager {

    private final NettyUdpServer udpServer;

    @Inject
    public ServerManager(NettyUdpServer.Factory factory, Config config) {
        this.udpServer = factory.newInstance(config.getPort());
    }

    public NettyUdpServer getUdpServer() {
        return udpServer;
    }

}
