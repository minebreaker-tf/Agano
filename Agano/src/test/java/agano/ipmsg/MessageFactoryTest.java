package agano.ipmsg;

import agano.util.Charsets;
import agano.util.StringUtils;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageFactoryTest {

    @Test
    public void test() {
        String original = "1:123:user:host:00000001:積み荷1:積み荷2";
        ByteBuffer buf = StringUtils.stringToByteBuffer(original, Charsets.shiftJIS());
        buf.flip();

        Message message = MessageFactory.fromByte(buf, 2425);

        assertThat(message.getVersion(), is("1"));
        assertThat(message.getPacketNumber(), is(123L));
        assertThat(message.getUser(), is("user"));
        assertThat(message.getHost(), is("host"));
        assertThat(message.getOperation().get(), is(1L));
        assertThat(message.getLoad(), is("積み荷1:積み荷2"));
        assertThat(message.getPort(), is(2425));
    }

    @Test(expected = MalformedMessageException.class)
    public void testInvalid() {
        MessageFactory.fromString("Some invalid string", 2425);
    }

}