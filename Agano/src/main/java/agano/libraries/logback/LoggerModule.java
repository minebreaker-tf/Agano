package agano.libraries.logback;

import com.google.inject.AbstractModule;

public class LoggerModule extends AbstractModule {

    protected void configure() {
        bind(LoggerConfigurer.class).to(LoggerConfigurerImpl.class);
    }

}
