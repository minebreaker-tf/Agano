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
    private EventBus eventBus;
    private SocketAddress address;
    private SocketAddress remote;
    private UdpServer server;
    private UdpSender sender;

    @Before
    public void setUp() {
        eventBus = new EventBus();
        eventBus.register(this);

        address = new InetSocketAddress(2425);
        remote = new InetSocketAddress("localhost", 2425);
        server = new UdpServer(eventBus, address);
        sender = new UdpSender();

        reveivedData = Collections.synchronizedList(new ArrayList<String>());
    }

    @After
    public void tearDown() {

        server.stop();
        server = null;
        sender.close();
        sender = null;

        reveivedData = null;
    }

    /*
     * 自ソケットに向けて送受信を試み、正しく動いていることを確認する
     */
    @Ignore
    @Test
    public void test() throws Exception {

        server.start();

        ByteBuffer load = ByteBuffer.allocate(1);

        load.put((byte) 0xFF).flip();
        sender.send(remote, load);
        Thread.sleep(1000);
        load.clear();
        load.put((byte) 0x00).flip();
        sender.send(remote, load);
        Thread.sleep(1000);
        load.clear();
        load.put((byte) 0x11).flip();
        sender.send(remote, load);

        Thread.sleep(1000);

        assertThat(reveivedData.get(0), is("FF"));
        assertThat(reveivedData.get(1), is("00"));
        assertThat(reveivedData.get(2), is("11"));
    }

    /*
     * 手動で実行し、オリジナルのIPmsgからデータを送り受信を試みるためのテスト
     */
    @Ignore
    @Test
    public void testReceive() throws Exception {
        server.start();
        Thread.sleep(Long.MAX_VALUE);
    }

    @Subscribe
    public void received(ByteBuffer buf) {
        byte[] temp = new byte[buf.remaining()];
        for (int i = 0; buf.hasRemaining(); i++) {
            temp[i] = buf.get();
        }
        System.out.println(new String(temp, Charset.forName("SJIS")).intern());
        System.out.println(DatatypeConverter.printHexBinary(temp));

        reveivedData.add(DatatypeConverter.printHexBinary(temp));
    }

}