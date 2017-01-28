package agano.util.concurrent;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.concurrent.Executors;

public final class ConcurrencyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConcurrencyHelper.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public ListeningScheduledExecutorService provideExecutorService() {
        return MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(2));
    }

}
