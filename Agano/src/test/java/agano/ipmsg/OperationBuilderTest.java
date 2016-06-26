package agano.ipmsg;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OperationBuilderTest {

    @Test
    public void test() {
        Operation op = OperationBuilder.of(Command.IPMSG_NOOPERATION)
                                       .add(Option.IPMSG_SENDCHECKOPT)
                                       .build();

        assertThat(op, is(new Operation(0x00000100)));
    }

}