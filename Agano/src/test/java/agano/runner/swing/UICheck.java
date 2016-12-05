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
import java.util.Arrays;

public class UICheck {

    public static void main(String[] args) throws Exception {

        UIManager.setLookAndFeel(Constants.defaultLaf);

        Injector injector = Guice.createInjector(new SwingModule(), new EventBusModule(), new ConfigModule());
        MainForm form = injector.getInstance(MainForm.Factory.class)
                                .newInstance(e -> System.exit(0));

        StateManager manager = injector.getInstance(StateManager.class);
        manager.register(form);

        User user = new User("test-user", new InetSocketAddress("localhost", 2425), Arrays.asList(
                MessageFactory.fileAttachedMessageFromString("1:124:test-user:localhost:2097184:メッセージ" + "\0" + "123:name.txt:10:10:1:", 2425),
                MessageFactory.fileAttachedMessageFromString("1:124:test-user:localhost:2097184:メッセージ" + "\0" + "123:name.txt:10:10:1:", 2425),
                MessageFactory.fileAttachedMessageFromString("1:124:test-user:localhost:2097184:メッセージ" + "\0" + "123:name.txt:10:10:1:", 2425),
                MessageFactory.fileAttachedMessageFromString(
                        "1:124:test-user:localhost:2097184:メッセージ" + "\0" +
                        "123:name.txt:10:10:1:" + "\07" + "456:name.dat:A:B:2:" + "\07" + "789:name.sql:A:B:2:", 2425),
                MessageFactory.fileAttachedMessageFromString("1:124:test-user:localhost:2097184:メッセージ" + "\0" + "123:name.txt:10:10:1:", 2425),
                MessageFactory.fileAttachedMessageFromString("1:124:test-user:localhost:2097184:メッセージ" + "\0" + "123:name.txt:10:10:1:", 2425),
                MessageFactory.fromString("1:123:test-user:localhost:00000001:Testing...", 2425)
        ));
        manager.swap(state -> state.addUser(user));
        manager.swap(state -> state.selectUser(user));
    }

}
