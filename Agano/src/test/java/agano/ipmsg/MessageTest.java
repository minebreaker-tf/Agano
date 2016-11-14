package agano.ipmsg;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageTest {

    private Message message;
    private Message message2;
    private Message message3;

    @Before
    public void setUp() {
        message = new Message("1", 123, "user", "host", 1L, "load", 2425, Collections.emptyList());
        message2 = new Message("2", 123, "user2", "host", 2L, "load2", 2425, Collections.emptyList());
        message3 = new Message("3", 123, "user3", "host", 3L, "load3", 2425, Collections.emptyList());
    }

    @SuppressWarnings({ "EqualsBetweenInconvertibleTypes", "ObjectEqualsNull" })
    @Test
    public void testEquals() {
        Message nonEquivalentPacket = new Message("1", 456, "user", "host", 1L, "load", 2425, Collections.emptyList());
        Message nonEquivalentHost = new Message("1", 123, "user", "another", 1L, "load", 2425, Collections.emptyList());
        Message nonEquivalentPort = new Message("1", 456, "user", "host", 1L, "load", 80, Collections.emptyList());

        assertThat(message.equals(message2), is(true));
        assertThat(message.equals(message3), is(true));
        assertThat(message.equals(nonEquivalentPacket), is(false));
        assertThat(message.equals(nonEquivalentHost), is(false));
        assertThat(message.equals(nonEquivalentPort), is(false));
        assertThat(message.equals("other class"), is(false));
        assertThat(message.equals(null), is(false));
    }

    @Test
    public void testEqualsSymmetry() {
        assertThat(message.equals(message2), is(message2.equals(message)));
        assertThat(message.equals(message3), is(message3.equals(message)));
    }

    @Test
    public void testEqualsTransitive() {
        assertThat(message.equals(message2), is(true));
        assertThat(message2.equals(message3), is(true));
        assertThat(message.equals(message3), is(true));
    }

    @Test
    public void testHashCode() {
        Message msg1 = new Message("1", 123, "user1", "host", 1L, "load1", 2425, Collections.emptyList());
        Message msg2 = new Message("2", 123, "user2", "host", 2L, "load2", 2425, Collections.emptyList());

        assertThat(msg1.hashCode(), is(msg2.hashCode()));
        assertThat(msg1.equals(msg2), is(true));
    }

}