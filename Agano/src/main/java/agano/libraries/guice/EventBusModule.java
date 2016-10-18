package agano.libraries.guice;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class EventBusModule extends AbstractModule {

    protected void configure() {
        bind(EventBus.class).in(Singleton.class);
    }

}
