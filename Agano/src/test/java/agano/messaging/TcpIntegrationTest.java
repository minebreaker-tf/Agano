package agano.messaging;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Ignore;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class TcpIntegrationTest {

    @Ignore
    @Test
    public void test() throws Exception {
        EventBus eventBus = new EventBus();
        eventBus.register(this);

        TcpServer server = new TcpServer(eventBus, new InetSocketAddress("localhost", 2425));

        server.start();
        Thread.sleep(Long.MAX_VALUE);
    }

    @Subscribe
    public void received(ByteBuffer buf) {
        byte[] temp = new byte[buf.remaining()];
        buf.get(temp);

        System.out.println(new String(temp).intern());
    }

}
