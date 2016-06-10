package agano.ipmsg;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class UdpServerCliTest {

    @Test
    public void test() throws Exception {
        EventBus eventBus = new EventBus();
        eventBus.register(this);
        UdpServer server = new UdpServer(eventBus, new InetSocketAddress(2425));
        server.start();
        Thread.sleep(100000);
    }

    @Subscribe
    public void out(ByteBuffer buf) {
        byte[] temp = new byte[1024];
        for (int i = 0; buf.hasRemaining(); i++) {
            temp[i] = buf.get();
        }
        System.out.println(new String(temp).intern());
        System.out.println(DatatypeConverter.printHexBinary(temp));
    }


}