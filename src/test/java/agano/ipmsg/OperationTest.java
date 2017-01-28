package agano.ipmsg;

import org.junit.Test;

import java.util.EnumSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OperationTest {

    @Test
    public void testConstructor() {

        Operation op = new Operation(
                Command.IPMSG_BR_ENTRY,
                EnumSet.of(
                        Option.IPMSG_SENDCHECKOPT,
                        Option.IPMSG_SECRETOPT
                )
        );

        long raw = 0x00000301;

        assertThat(op.getCode(), is(raw));
    }

    @Test
    public void testConstructorFromCode() {

        Operation op = new Operation(0x00000301);

        assertThat(op.getCommand(), is(Command.IPMSG_BR_ENTRY));
        assertThat(op.getOptions().contains(Option.IPMSG_SENDCHECKOPT), is(true));
        assertThat(op.getOptions().contains(Option.IPMSG_SECRETOPT), is(true));

    }

}
