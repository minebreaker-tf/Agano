package agano.ipmsg;

import agano.util.Constants;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageBuilderTest {

    private Message message;

    @Before
    public void setUp() {
        message = new Message("1", 123, "user", "host", 1L, "load", 2425);
    }

    @Test
    public void testBuild() {
        Message built = new MessageBuilder().version("1")
                                            .packetNumber(123)
                                            .user("user")
                                            .host("host")
                                            .operation(new Operation(1))
                                            .load("load")
                                            .port(2425)
                                            .build();

        assertThat(built.getVersion(), is(message.getVersion()));
        assertThat(built.getPacketNumber(), is(message.getPacketNumber()));
        assertThat(built.getUser(), is(message.getUser()));
        assertThat(built.getHost(), is(message.getHost()));
        assertThat(built.getOperation(), is(message.getOperation()));
        assertThat(built.getLoad(), is(message.getLoad()));
        assertThat(built.getPort(), is(message.getPort()));

    }


    @Test
    public void testGracefulSetUp() throws UnknownHostException {
        Message built = new MessageBuilder().setUp(new Operation(1), "load").build();

        assertThat(built.getVersion(), is(Constants.protocolVersion));
        assertThat(built.getUser(), is("default-user")); // TODO
        assertThat(built.getHost(), is(InetAddress.getLocalHost().getHostName()));
        assertThat(built.getOperation(), is(message.getOperation()));
        assertThat(built.getLoad(), is(message.getLoad()));
        assertThat(built.getPort(), is(Constants.defaultPort));

    }
}