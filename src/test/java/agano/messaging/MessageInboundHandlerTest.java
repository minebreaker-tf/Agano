package agano.messaging;

import agano.ipmsg.Message;
import agano.runner.parameter.MessageReceivedParameter;
import com.google.common.eventbus.EventBus;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ResourceLeakDetector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("ConstantConditions")
public class MessageInboundHandlerTest {

    @Before
    public void setUp() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }

    @Test
    public void test() {
        EventBus mockEventBus = mock(EventBus.class);
        MessageInboundHandler handler = new MessageInboundHandler(mockEventBus);
        EmbeddedChannel channel = new EmbeddedChannel(handler);

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer("1:123:user:host:00000001:積み荷1:積み荷2", StandardCharsets.UTF_8),
                new InetSocketAddress("localhost", 2425),
                new InetSocketAddress("192.168.0.1", 2425)
        );
        channel.writeInbound(packet);

        verify(mockEventBus).post(argThat(new MessageMatcher()));
    }

    private static class MessageMatcher implements ArgumentMatcher<MessageReceivedParameter> {

        @Override
        public boolean matches(MessageReceivedParameter argument) {
            Message msg = argument.getMessage();
            return msg.getVersion().equals("1") &&
                   msg.getPacketNumber() == 123 &&
                   msg.getUser().equals("user") &&
                   msg.getHost().equals("host") &&
                   msg.getOperation().getCode() == 1 &&
                   msg.getLoad().equals("積み荷1:積み荷2");
        }

    }

}