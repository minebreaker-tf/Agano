package agano.runner.ui;

import javax.swing.*;
import java.awt.*;

public final class UserList extends JPanel {

    private JScrollPane base;
    private JList list;
    private DefaultListModel model;

    public UserList() {

        this.base = new JScrollPane();

        this.model = new DefaultListModel();
        this.list = new JList(model);
        this.list.setBorder(BorderFactory.createEmptyBorder());

        this.base.getViewport().setView(list);
        this.base.setBorder(BorderFactory.createEmptyBorder());

        LayoutManager layout = new BorderLayout();
        this.setLayout(layout);

        this.add(base, BorderLayout.CENTER);
        this.setMinimumSize(new Dimension(100, 100));
    }

    public JList getList() {
        return list;
    }

    public void add(String label) {
        model.addElement(label);
    }

    public void insert(String label, int index) {
        model.insertElementAt(label, index);
    }

    public void remove(int index) {
        model.remove(index);
    }

}
