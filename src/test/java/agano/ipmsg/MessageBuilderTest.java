package agano.ipmsg;

import agano.config.Config;
import agano.config.ConfigModuleForTest;
import agano.util.Constants;
import com.google.inject.Guice;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

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
        Config config = Guice.createInjector(new ConfigModuleForTest.Empty()).getInstance(Config.class);

        Message built = new MessageBuilder().setUp(config, new Operation(1), "load").build();

        assertThat(built.getVersion(), is(Constants.protocolVersion));
        assertThat(built.getUser(), is(System.getProperty("user.name"))); // TODO
        assertThat(built.getHost(), is(InetAddress.getLocalHost().getHostName()));
        assertThat(built.getOperation(), is(message.getOperation()));
        assertThat(built.getLoad(), is(message.getLoad()));
        assertThat(built.getPort(), is(Constants.defaultPort));
    }

    @Test
    public void testFileAttachedMessage() {
        FileAttachedMessage built = new MessageBuilder()
                .version("1")
                .packetNumber(123)
                .user("user")
                .host("host")
                .operation(new Operation(2097184))
                .load("load")
                .port(2425)
                .attachments(Collections.singletonList(
                        new Attachment(123, "text.txt", 10, 11, new FileInfo(FileType.IPMSG_FILE_REGULAR))
                ))
                .buildFileAttachedMessage();

        assertThat(built.getAttachments().size(), is(1));
        assertThat(built.getAttachments().get(0).getFileID(), is(123L));
        assertThat(built.getAttachments().get(0).getFileName(), is("text.txt"));
        assertThat(built.getAttachments().get(0).getFilesize(), is(10L));
        assertThat(built.getAttachments().get(0).getMtime(), is(11L));
        assertThat(built.getAttachments().get(0).getFileInfo().getFileType(), is(FileType.IPMSG_FILE_REGULAR));
    }

    @Test
    public void testFileSendRequest() {
        FileSendRequestMessage built = new MessageBuilder()
                .version("1")
                .packetNumber(123)
                .user("user")
                .host("host")
                .operation(new Operation(96))
                .load("load")
                .port(2425)
                .fileSendRequest(10, 11, 12)
                .buildFileSendRequest();

        assertThat(built.getLoad(), is("a:b:c"));
        assertThat(built.getRequestPacketNumber(), is(10L));
        assertThat(built.getRequestFileID(), is(11L));
        assertThat(built.getOffset(), is(12L));
    }

}