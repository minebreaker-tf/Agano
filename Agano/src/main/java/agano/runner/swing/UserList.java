package agano.runner.swing;

import javax.swing.*;
import java.awt.*;

public final class UserList extends JPanel {

    private final JScrollPane base;
    private final JList<String> list;
    private final DefaultListModel<String> model;

    public UserList() {

        this.base = new JScrollPane();

        this.model = new DefaultListModel<>();
        model.add(0, "a");
        model.add(1, "b");
        model.add(2, "c");

        this.list = new JList<>(model);
        this.list.setBorder(BorderFactory.createEmptyBorder());

        this.base.getViewport().setView(list);
        this.base.setBorder(BorderFactory.createEmptyBorder());

        LayoutManager layout = new BorderLayout();
        this.setLayout(layout);

        this.add(base, BorderLayout.CENTER);
//        this.setMinimumSize(new Dimension(100, 100));
    }

}
