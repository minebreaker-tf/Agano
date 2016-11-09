package agano.runner.swing;

import agano.config.ConfigModule;
import agano.ipmsg.MessageFactory;
import agano.libraries.guava.EventBusModule;
import agano.runner.state.StateManager;
import agano.runner.state.User;
import agano.util.Constants;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.util.Collections;

public class UICheck {

    public static void main(String[] args) throws Exception {

        UIManager.setLookAndFeel(Constants.defaultLaf);

        Injector injector = Guice.createInjector(new SwingModule(), new EventBusModule(), new ConfigModule());
        MainForm form = injector.getInstance(MainForm.Factory.class)
                                .newInstance(e -> System.exit(0));

        StateManager manager = injector.getInstance(StateManager.class);
        manager.register(form);

        User user = new User("test-user", new InetSocketAddress("localhost", 2425), Collections.singletonList(
                MessageFactory.fromString("1:123:test-user:localhost:00000001:Testing...", 2425)));
        manager.swap(state -> state.addUser(user));
        manager.swap(state -> state.selectUser(user));
    }

}
