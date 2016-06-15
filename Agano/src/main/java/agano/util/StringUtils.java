package agano.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public final class StringUtils {

    private StringUtils() {}

    public static String byteToString(byte[] bytes, Charset charset) {
        return new String(bytes, charset).intern();
    }

    public static String byteToString(ByteBuffer from, Charset charset) {
        byte[] bytes = new byte[from.remaining()];
        for (int i = 0; from.hasRemaining(); i++) {
            bytes[i] = from.get();
        }
        return byteToString(bytes, charset);
    }

    public static byte[] stringToByte(String string, Charset charset) {
        return string.getBytes(charset);
    }

    /**
     * 戻り値のByteBufferをすぐに読み込む場合、flip()を呼び出してください。
     *
     * @param string
     * @param charset
     * @return
     */
    public static ByteBuffer stringToByteBuffer(String string, Charset charset) {
        byte[] bytes = stringToByte(string, charset);
        ByteBuffer buf = ByteBuffer.allocate(bytes.length);
        buf.put(bytes); // flip()したほうがいいかなあ
        return buf;
    }

}
