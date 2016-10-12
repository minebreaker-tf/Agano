package agano.runner.controller;

import agano.runner.parameter.Binder;
import agano.runner.parameter.Parameter;
import agano.runner.parameter.State;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final Binder binder;

    public Controller(Binder binder) {
        this.binder = binder;
    }

    @Subscribe
    public void subscribe(Parameter parameter) {
        logger.info("Received: {}", parameter.getMessage());

        binder.bind(new State(parameter.getMessage()));
    }

}
