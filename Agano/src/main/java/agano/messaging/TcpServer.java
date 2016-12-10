package agano.messaging;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public final class TcpServer {

    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    private final EventLoopGroup group;
    private final EventLoopGroup workerGroup;

    public interface Factory {
        public TcpServer newInstance(int port);
    }

    @Inject
    public TcpServer(FileSendingHandler fileSendingHandler, @Assisted int port) {
        this.group = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group, workerGroup)
                 .channel(NioServerSocketChannel.class)
                 .childHandler(fileSendingHandler)
                 .bind(port).syncUninterruptibly();
    }

    @Nonnull
    public Future shutdown() {
        return group.shutdownGracefully();
    }

    /**
     * see Effective Java Item.7
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            shutdown();
        } finally {
            super.finalize();
        }
    }

}
