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

        assertThat(op.get(), is(raw));
    }

}