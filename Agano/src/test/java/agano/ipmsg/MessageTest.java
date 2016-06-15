package agano.ipmsg;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageTest {

    Message message;

    @Before
    public void setUp() {
        message = new Message("1", 123, "user", "host", 1L, "load", 2425);
    }

    @Ignore
    @Test
    public void testHashCode() {
        System.out.println(message.hashCode());
    }

    @SuppressWarnings({ "EqualsBetweenInconvertibleTypes", "ObjectEqualsNull" })
    @Test
    public void testEquals() {
        Message equivalent = new Message("1", 123, "user", "host", 1L, "load", 2425);
        Message nonEquivalent1 = new Message("1", 456, "user", "host", 1L, "load", 2425); // packet
        Message nonEquivalent2 = new Message("1", 123, "user", "another", 1L, "load", 2425); // host
        Message nonEquivalent3 = new Message("1", 456, "user", "host", 1L, "load", 80);

        assertThat(message.equals(equivalent), is(true));
        assertThat(message.equals(nonEquivalent1), is(false));
        assertThat(message.equals(nonEquivalent2), is(false));
        assertThat(message.equals(nonEquivalent3), is(false));
        assertThat(message.equals("other class"), is(false));
        assertThat(message.equals(null), is(false));
    }

}