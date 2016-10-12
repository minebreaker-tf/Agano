package agano.runner.swing;

import javax.swing.*;

public final class MainForm extends JFrame {

    private JSplitPane splitPane;
    private UserList userList;
    private ChatPane chatPane;

    public MainForm() {

        this.setTitle("Agano");
        this.setSize(600, 400);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);

        userList = new UserList();
        splitPane.setLeftComponent(userList);

        chatPane = new ChatPane();
        splitPane.setRightComponent(chatPane);

        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(5);

        this.getContentPane().add(splitPane);

        this.setVisible(true);

    }

    public UserList getUserList() {
        return userList;
    }

    public ChatPane getChatPane() {
        return chatPane;
    }

}
