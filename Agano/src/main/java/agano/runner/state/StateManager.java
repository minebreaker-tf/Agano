package agano.runner.state;

import agano.runner.swing.Observer;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public final class StateManager {

    private final AtomicReference<State> state;
    private Observer<State> observer;

    public StateManager() {
        state = new AtomicReference<>(State.initialState());
    }

    @Nonnull
    public State get() {
        return state.get();
    }

    @Nullable
    public synchronized Observer<State> getObserver() {
        return observer;
    }

    /**
     * {@code register}を呼び出し、observerを登録した後に呼び出すこと.<br>
     * updaterはnullを返してはいけない(が、保証されていないので注意)
     *
     * @param updater 状態を変更する関数
     */
    public void swap(@Nonnull UnaryOperator<State> updater) {
        observer.update(state.updateAndGet(updater));
    }

    public synchronized void register(@Nonnull Observer<State> view) {
        this.observer = checkNotNull(view);
    }

}
