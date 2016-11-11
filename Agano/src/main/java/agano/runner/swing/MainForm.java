package agano.runner.swing;

import agano.runner.parameter.Parameters;
import agano.runner.state.State;
import agano.util.Constants;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MainForm implements Observer<State> {

    private final JFrame frame;
    private final JSplitPane splitPane;
    private final UserList userList;
    private final ChatPane chatPane;

    public interface Factory {
        public MainForm newInstance(Consumer<WindowEvent> callback);
    }

    @Inject
    public MainForm(EventBus eventBus, UserList userList, ChatPane chatPane, @Assisted Consumer<WindowEvent> callback) {

        frame = new JFrame();
        frame.setTitle(Constants.title);
        frame.setSize(800, 450);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                callback.accept(e);
            }
        });
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                eventBus.post(new Parameters.WindowFocusedParameter(e));
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                eventBus.post(new Parameters.WindowFocusedParameter(e));
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);

        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(5);

        frame.getContentPane().add(splitPane);

        this.userList = userList;
        splitPane.setLeftComponent(userList.component());

        this.chatPane = chatPane;
        splitPane.setRightComponent(chatPane.component());

        frame.setVisible(true);

    }

    @Override
    public void update(@Nonnull State state) {
        checkNotNull(state);

        userList.update(state.getUsers());
        chatPane.update(state);
    }

}
