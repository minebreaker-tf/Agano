package agano.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 定数クラス
 */
public final class Constants {

    public static final String protocolVersion = "1";

    public static final Path configPath = Paths.get("../conf/agano.conf");

    public static final String defaultLaf = "com.bulenkov.darcula.DarculaLaf";
    //    public static final String defaultFont = "/agano/font/mplus-1c-light.ttf";
    public static final String defaultFont = "/agano/font/SourceHanSansJP-Light.otf";
    public static final int defaultFontSize = 16;

    public static final String title = "Agano";

    public static final int defaultPort = 2425;

}
