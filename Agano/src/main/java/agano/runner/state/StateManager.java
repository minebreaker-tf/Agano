package agano.runner.state;

import agano.runner.swing.Observer;
import com.google.inject.Singleton;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

@Singleton
public final class StateManager {

    private final AtomicReference<State> state;
    private Observer<State> observer;

    public StateManager() {
        state = new AtomicReference<>(new State(""));
    }

    public State get() {
        return state.get();
    }

    public void swap(UnaryOperator<State> updater) {
        observer.update(state.updateAndGet(updater));
    }

    public synchronized void register(Observer<State> view) {
        this.observer = view;
    }

}
