package agano.config;

import java.util.Properties;

/**
 * グローバルにConfigのもろもろを管理する
 */
public final class ConfigManager {

    private static final ConfigManager singleton = new ConfigManager();

    private final Properties properties;

    private ConfigManager() {
        this.properties = new Properties();
    }

    public static ConfigManager get() {
        return singleton;
    }

    public Properties getConfig() {
        return properties;
    }

    public void loadFromFile() {

    }

}
