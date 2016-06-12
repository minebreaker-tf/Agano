package agano.ipmsg;

import agano.messaging.UdpSender;
import agano.messaging.UdpServer;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UdpIntegrationTest {

    private List<String> reveivedData;

    @Before
    public void setUp() {
        reveivedData  = Collections.synchronizedList(new ArrayList<String>());
    }

    @After
    public void tearDown() {
        reveivedData = null;
    }

    @Ignore
    @Test
    public void test() throws Exception {
        EventBus eventBus = new EventBus();
        eventBus.register(this);

        SocketAddress address = new InetSocketAddress("localhost", 2425);
        UdpServer server = new UdpServer(eventBus, address);
        server.start();

        UdpSender sender = new UdpSender(address);
        ByteBuffer load = ByteBuffer.allocate(1);

        load.put((byte) 0xFF).flip();
        sender.send(load);
        Thread.sleep(1000);
        load.clear();
        load.put((byte) 0x00).flip();
        sender.send(load);
        Thread.sleep(1000);
        load.clear();
        load.put((byte) 0x11).flip();
        sender.send(load);

        Thread.sleep(10000);
        assertThat(reveivedData.get(0), is("FF"));
        assertThat(reveivedData.get(1), is("00"));
        assertThat(reveivedData.get(2), is("11"));
    }

    @Subscribe
    public void out(ByteBuffer buf) {
        byte[] temp = new byte[buf.remaining()];
        for (int i = 0; buf.hasRemaining(); i++) {
            temp[i] = buf.get();
        }
        System.out.println(new String(temp, Charset.forName("SJIS")).intern());
        System.out.println(DatatypeConverter.printHexBinary(temp));

        reveivedData.add(DatatypeConverter.printHexBinary(temp));
    }


}