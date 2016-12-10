package agano.libraries.logback;

import agano.config.Config;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.google.inject.Inject;
import org.slf4j.LoggerFactory;

public class LoggerConfigurerImpl implements LoggerConfigurer {

    private final Config config;

    @Inject
    public LoggerConfigurerImpl(Config config) {
        this.config = config;
    }

    @Override
    public void configure() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAndStopAllAppenders();
        rootLogger.setLevel(config.getLogLevel());

//        PatternLayoutEncoder ple = new PatternLayoutEncoder();
//        ple.setContext(loggerContext);
//        ple.setPattern("%level %date %msg%n");
//        ple.start();
//        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
//        appender.setContext(loggerContext);
//        appender.setEncoder(ple);
//        appender.start();
//        rootLogger.addAppender(appender);
    }

}
