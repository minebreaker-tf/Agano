package agano.util.concurrent;

import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ConcurrencyHelperTest {

    private ConcurrencyHelper helper;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new ConcurrencyModule());
        this.helper = injector.getInstance(ConcurrencyHelper.class);
    }

    @Ignore
    @Test
    public void testDelay() throws ExecutionException, InterruptedException {
        Instant start = Instant.now();

        ListenableScheduledFuture<String> f = helper.delay(() -> "hoge", 1000);
        System.out.println(f.get());

        Instant end = Instant.now();
        long time = start.until(end, ChronoUnit.MILLIS);
        System.out.println(time);
        assertThat(time >= 1000, is(true));
    }

    @Test
    public void test() throws Exception {
        ListeningScheduledExecutorService executor = mock(ListeningScheduledExecutorService.class);
        ListenableScheduledFuture<String> f = mock(ListenableScheduledFuture.class);
        Callable<String> c = () -> "ret";

        when(executor.schedule(eq(c), anyLong(), any())).thenReturn(f);
        when(f.get()).thenReturn(c.call());

        ConcurrencyHelper helper = new ConcurrencyHelper(executor);
        ListenableScheduledFuture<String> ret = helper.delay(c, 1);

        verify(executor).schedule(eq(c), eq(1L), any());
        assertThat(ret.get(), is("ret"));
    }

}
