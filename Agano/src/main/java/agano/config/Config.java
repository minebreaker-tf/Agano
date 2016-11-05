package agano.config;

public final class Config {

    private final com.typesafe.config.Config raw;

    public Config(com.typesafe.config.Config config) {
        this.raw = config;
    }

    public int getPort() {
        return raw.getInt("port");
    }

}
