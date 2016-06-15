package agano.util;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;

public final class Charsets {

    private Charsets() {}

    @Nonnull
    public static Charset shiftJIS() {
        return Charset.forName("SJIS");
    }

}
