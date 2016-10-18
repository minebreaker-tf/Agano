package agano.runner.swing;

import agano.runner.state.State;
import agano.util.Constants;
import com.google.inject.Inject;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainForm implements Observer<State> {

    private final JFrame frame;
    private final JSplitPane splitPane;
    private final UserList userList;
    private final ChatPane chatPane;

    @Inject
    public MainForm(UserList userList, ChatPane chatPane) {

        frame = new JFrame();
        frame.setTitle(Constants.title);
        frame.setSize(800, 450);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // TODO callback
                System.out.println("App exits.");
                System.exit(0);
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);

        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(5);

        frame.getContentPane().add(splitPane);

        this.userList = userList;
        splitPane.setLeftComponent(userList);

        this.chatPane = chatPane;
        splitPane.setRightComponent(chatPane);

        frame.setVisible(true);

    }

    @Override
    public void update(State state) {

    }

}
