package agano.messaging;

import agano.util.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class ServerManager {

    private final NettyUdpServer udpServer;

    @Inject
    public ServerManager(NettyUdpServer.Factory factory) {
        this.udpServer = factory.newInstance(Constants.defaultPort);
    }

    public NettyUdpServer getUdpServer() {
        return udpServer;
    }

}
