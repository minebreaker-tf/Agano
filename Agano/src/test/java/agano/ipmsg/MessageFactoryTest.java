package agano.ipmsg;

import agano.util.StringUtils;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MessageFactoryTest {

    @Test
    public void testFromString() {
        String original = "1:123:user:host:00000001:積み荷1:積み荷2";
        Message message = MessageFactory.fromString(original, 2425);

        assertThat(message.getVersion(), is("1"));
        assertThat(message.getPacketNumber(), is(123L));
        assertThat(message.getUser(), is("user"));
        assertThat(message.getHost(), is("host"));
        assertThat(message.getOperation().getCode(), is(1L));
        assertThat(message.getLoad(), is("積み荷1:積み荷2"));
        assertThat(message.getPort(), is(2425));
    }

    @Test
    public void testFromByte() {
        String original = "1:123:user:host:00000001:積み荷1:積み荷2";
        ByteBuffer buf = StringUtils.stringToByteBuffer(original, StandardCharsets.UTF_8);
        buf.flip();

        Message message = MessageFactory.fromByte(buf, 2425);

        assertThat(message.getVersion(), is("1"));
        assertThat(message.getPacketNumber(), is(123L));
        assertThat(message.getUser(), is("user"));
        assertThat(message.getHost(), is("host"));
        assertThat(message.getOperation().getCode(), is(1L));
        assertThat(message.getLoad(), is("積み荷1:積み荷2"));
        assertThat(message.getPort(), is(2425));
    }

    @Test(expected = MalformedMessageException.class)
    public void testInvalid() {
        MessageFactory.fromString("Some invalid string", 2425);
    }

    //    fileID:filename:size:mtime:fileattr[:extend-attr=val1
//     [,val2...][:extend-attr2=...]]:\a[:]fileID...
    @Test
    public void testFileAttachedMessage() {
        String original = "1:123:user:host:2097184:メッセージ" + "\0" + "123:name.txt:10:10:1:" + "\07" + "456:name.dat:A:B:2:";
        FileAttachedMessage message = MessageFactory.fileAttachedMessageFromString(original, 2425);

        assertThat(message.getVersion(), is("1"));
        assertThat(message.getPacketNumber(), is(123L));
        assertThat(message.getUser(), is("user"));
        assertThat(message.getHost(), is("host"));
        assertThat(message.getOperation().getCommand(), is(Command.IPMSG_SENDMSG));
        assertThat(message.getOperation().isEnabledOption(Option.IPMSG_FILEATTACHOPT), is(true));
        assertThat(message.getLoad(), is("メッセージ"));
        assertThat(message.getPort(), is(2425));

        List<? extends Attachment> attachments = message.getAttachments();
        assertThat(attachments.size(), is(2));
        Attachment att1 = attachments.get(0);
        Attachment att2 = attachments.get(1);

        assertThat(att1.getFileID(), is(123L));
        assertThat(att1.getFilename(), is("name.txt"));
        assertThat(att1.getFilesize(), is(16L));
        assertThat(att1.getMtime(), is(16L));
        assertThat(att1.getFileInfo().getFileType(), is(FileType.IPMSG_FILE_REGULAR));
        assertThat(att1.getFileInfo().getAttributes().isEmpty(), is(true));

        assertThat(att2.getFileID(), is(456L));
        assertThat(att2.getFilename(), is("name.dat"));
        assertThat(att2.getFilesize(), is(10L));
        assertThat(att2.getMtime(), is(11L));
        assertThat(att2.getFileInfo().getFileType(), is(FileType.IPMSG_FILE_DIR));
        assertThat(att2.getFileInfo().getAttributes().isEmpty(), is(true));
    }

    @Test
    public void testFileSendRequest() {
        String original = "1:123:user:host:96:A:B:C";
        FileSendRequestMessage message = MessageFactory.fileSendRequestMessageFromString(original, 2425);

        assertThat(message.getVersion(), is("1"));
        assertThat(message.getPacketNumber(), is(123L));
        assertThat(message.getUser(), is("user"));
        assertThat(message.getHost(), is("host"));
        assertThat(message.getOperation().getCommand(), is(Command.IPMSG_GETFILEDATA));
        assertThat(message.getOperation().getOptions().isEmpty(), is(true));
        assertThat(message.getLoad(), is("a:b:c"));
        assertThat(message.getPort(), is(2425));

        assertThat(message.getRequestPacketNumber(), is(10L));
        assertThat(message.getRequestFileID(), is(11L));
        assertThat(message.getOffset(), is(12L));
    }

}