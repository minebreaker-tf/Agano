package agano.messaging;

import agano.config.Config;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

@Singleton
public final class ServerManager {

    private final UdpServer udpServer;
    private final TcpServer tcpServer;

    @Inject
    public ServerManager(UdpServer.Factory udpServer, TcpServer.Factory tcpServer, Config config) {
        this.udpServer = udpServer.newInstance(config.getPort());
        this.tcpServer = tcpServer.newInstance(config.getPort());
    }

    @Nonnull
    public UdpServer getUdpServer() {
        return udpServer;
    }

    @Nonnull
    public TcpServer getTcpServer() {
        return tcpServer;
    }

}
