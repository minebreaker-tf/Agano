package agano.config;

import agano.util.Constants;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.ConfigFactory;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {}

    @Provides
    @Singleton
    public Config provideConfig() {
        com.typesafe.config.Config tsConfFile = ConfigFactory.parseFile(Constants.configPath.toFile());
        com.typesafe.config.Config tsConfProp = ConfigFactory.systemProperties();
        com.typesafe.config.Config raw = tsConfProp.withFallback(tsConfFile);

        return new Config(raw);
    }

}
