package agano.config;

import agano.util.AganoException;
import agano.util.Constants;
import com.typesafe.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public final class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private final com.typesafe.config.Config raw;

    private final int port;
    private final Font font;

    Config(com.typesafe.config.Config config) {
        this.raw = config;
        this.port = getInt("port").orElse(Constants.defaultPort);
        this.font = createFont();
    }

    private Optional<String> getString(String path) {
        try {
            return Optional.of(raw.getString(path));
        } catch (ConfigException.Missing | ConfigException.WrongType e) {
            logger.info("Failed to read config '{}'. Fall back to default.", path);
            return Optional.empty();
        }
    }

    private Optional<Integer> getInt(String path) {
        try {
            return Optional.of(raw.getInt(path));
        } catch (ConfigException.Missing | ConfigException.WrongType e) {
            logger.info("Failed to read config '{}'. Fall back to default.", path);
            return Optional.empty();
        }
    }

    private Font createFont() {
        Optional<String> optionalFontName = getString("font.name");
        int fontSize = getInt("font.size").orElse(Constants.defaultFontSize);

        if (optionalFontName.isPresent()) {
            return Font.decode(optionalFontName.get())
                       .deriveFont((float) fontSize);
        } else {
            return defaultFont();
        }
    }

    private Font defaultFont() {
        try {
            logger.info("Fell back to the default font.");
            return Font.createFont(Font.TRUETYPE_FONT, Config.class.getResourceAsStream(Constants.defaultFont))
                       .deriveFont(Font.PLAIN, Constants.defaultFontSize);
        } catch (FontFormatException | IOException e) {
            logger.warn("Failed to load the default font. Please contact to the developer.");
            throw new AganoException(e);
        }
    }

    public int getPort() {
        return port;
    }

    public Font getFont() {
        return font;
    }

}
