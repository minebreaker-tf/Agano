package agano.runner.cui;

import agano.ipmsg.Message;
import agano.ipmsg.MessageFactory;
import agano.messaging.UdpSender;
import agano.messaging.UdpServer;
import agano.util.AganoException;
import com.google.common.base.Splitter;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;

public final class CliMain {

    private static final Logger logger = LoggerFactory.getLogger(CliMain.class);

    public static void main(String[] args) {
        new CliMain().run();
    }

    private EventBus eventBus = new EventBus();
    private SocketAddress address = new InetSocketAddress(2425);
    private UdpServer server = new UdpServer(eventBus, address);
    private UdpSender sender = new UdpSender();

    private void run() {
        server.start();

        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print(">");
            String in;
            try {
                in = reader.readLine();
            } catch (IOException e) {
                throw new AganoException(e);
            }
            System.out.println();

            if (in.equals("")) {
                logger.info("No op.");
            } else if (in.startsWith("%")) {
                try {
                    sendPack(in);
                } catch (Exception e) {
                    logger.warn("Failed to send.");
                }
            } else if (in.equals("exit") || in.equals("close") || in.equals("q") ||
                       in.equals("bye")) {
                break;
            } else {
                logger.info("Op not found.");
            }
        }

        logger.info("Bye.");

    }

    @Subscribe
    public void received(ByteBuffer buf) {
        Message message = MessageFactory.fromByte(buf, 2425);
        logger.info("Message received: {}", message);
    }

    private void sendPack(String in) {
        String received = in.substring(1, in.length());

        List<String> split = Splitter.on("%").splitToList(received);
        String host = split.get(0);
        String raw = split.get(1);

        Message message = MessageFactory.fromString(raw, 2425);
        logger.info("Send: " + message.toString());

        sender.send(new InetSocketAddress(host, 2425), message.asByte());
    }

}
