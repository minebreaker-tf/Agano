package agano.runner.controller;

import agano.runner.parameter.SelectionParameter;
import agano.runner.parameter.WindowFocusedParameter;
import agano.runner.state.StateManager;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public final class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private final StateManager manager;

    @Inject
    public Controller(StateManager manager) {
        this.manager = manager;
    }

    @Subscribe
    public void select(SelectionParameter parameter) {
        manager.swap(state -> state.selectUser(parameter.getSelected()));
    }

    @Subscribe
    public void windowFocused(WindowFocusedParameter parameter) {
        manager.swap(state -> state.changeFocus(parameter.isWindowFocused()));
    }

}
