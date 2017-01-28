package agano.util.concurrent;

import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.inject.Inject;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public final class ConcurrencyHelper {

    private final ListeningScheduledExecutorService executor;

    @Inject
    public ConcurrencyHelper(ListeningScheduledExecutorService executor) {
        this.executor = executor;
    }

    public <T> ListenableScheduledFuture<T> delay(Callable<T> method, int delayMs) {
        return executor.schedule(method, delayMs, TimeUnit.MILLISECONDS);
    }

}
