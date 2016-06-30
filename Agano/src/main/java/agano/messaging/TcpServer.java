package agano.messaging;

import agano.util.AganoException;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TcpServer implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);
    private static final int bufferCapacity = 1024;

    private final ServerSocketChannel channel;
    private final SocketAddress address;
    private final ExecutorService executorService;
    private final EventBus eventBus;

    public TcpServer(@Nonnull EventBus eventBus, @Nonnull SocketAddress address) {

        checkNotNull(eventBus);
        checkNotNull(address);

        this.eventBus = eventBus;
        this.address = address;

        try {
            this.channel = ServerSocketChannel.open();
            this.channel.socket().setReuseAddress(true);
            this.channel.configureBlocking(false);
            this.channel.socket().bind(address);
        } catch (IOException e) {
            logger.warn("Failed to initialize socket", e);
            throw new AganoException(e);
        }

        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void start() {
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                while (true) {
                    SocketChannel accepted = channel.accept();
                    if (accepted == null) continue;

                    BufferedInputStream bis = new BufferedInputStream(
                            accepted.socket().getInputStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[bufferCapacity];
                    for (int len = 0; (len = bis.read(buf)) > 0; ) {
                        baos.write(buf);
                    }
                    baos.close();
                    ByteBuffer received = ByteBuffer.wrap(baos.toByteArray());
                    eventBus.post(received);
                    accepted.close();
                }
            }
        });
    }

    public void stop() {
        executorService.shutdown();
        try {
            channel.close();
        } catch (IOException e) {
            logger.warn("Failed to close socket", e);
            throw new AganoException(e);
        }
    }

    @Override
    public void close() {
        stop();
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }

}
