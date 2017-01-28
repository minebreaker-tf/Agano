package agano.util;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringUtilsTest {

    @Test
    public void byteToString1() throws Exception {
        byte[] from = { 0x61, 0x62, 0x63 };
        String res = StringUtils.byteToString(from, Charsets.shiftJIS());

        assertThat(res, is("abc"));
    }

    @Test
    public void byteToString2() throws Exception {
        ByteBuffer from = ByteBuffer.wrap(new byte[] { 0x61, 0x62, 0x63 });
        String res = StringUtils.byteToString(from, Charsets.shiftJIS());

        assertThat(res, is("abc"));
    }

    @Test
    public void stringToByte() throws Exception {
        byte[] res = StringUtils.stringToByte("abc", Charsets.shiftJIS());

        assertThat(res, is(new byte[] { 0x61, 0x62, 0x63 }));
    }

    @Test
    public void stringToByteBuffer() throws Exception {
        ByteBuffer res = StringUtils.stringToByteBuffer("abc", Charsets.shiftJIS());
        res.flip();
        byte[] buf = new byte[res.remaining()];
        for (int i = 0; res.hasRemaining(); i++) buf[i] = res.get();

        assertThat(buf, is(new byte[] { 0x61, 0x62, 0x63 }));
    }

}