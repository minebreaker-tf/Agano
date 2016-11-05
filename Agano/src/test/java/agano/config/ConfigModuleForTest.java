package agano.config;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.typesafe.config.ConfigFactory;

import java.awt.*;

public class ConfigModuleForTest extends AbstractModule {

    @Override protected void configure() {}

    @Provides
    public Config provideConfig() {
        return new Config(ConfigFactory.parseMap(ImmutableMap.of(
                "port", 62425,
                "font.size", 10,
                "font.name", Font.SANS_SERIF
        )));
    }

    public static class Empty extends AbstractModule {

        @Override
        protected void configure() {}

        @Provides
        public Config provideConfig() {
            return new Config(ConfigFactory.empty());
        }

    }

}
