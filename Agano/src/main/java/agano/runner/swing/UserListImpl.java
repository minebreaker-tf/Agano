package agano.runner.swing;

import agano.config.Config;
import agano.runner.parameter.Parameters;
import agano.runner.state.User;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public final class UserListImpl implements UserList {

    private final JPanel base;
    private final JScrollPane scrollPane;
    private final JList<User> jList;
    private final DefaultListModel<User> model;

    private List<User> users;

    @Inject
    public UserListImpl(UserListToolbar.Factory toolbarFactory, EventBus eventBus, Config config) {

        scrollPane = new JScrollPane();
        model = new DefaultListModel<>();
        base = new JPanel();
        users = Collections.emptyList();

        jList = new JList<>(model);
        jList.setFont(config.getFont());
        jList.setBorder(BorderFactory.createEmptyBorder());
        jList.addListSelectionListener(e -> {
            synchronized (this) {
                int i = jList.getSelectedIndex();
                if (i >= 0) {
                    eventBus.post(new Parameters.SelectionParameter(e, model.get(i)));
                }
            }
        });
        jList.setCellRenderer(new UserListCellRenderer());

        scrollPane.getViewport().setView(jList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        UserListToolbar toolbar = toolbarFactory.newInstance(condition -> updateWithFilter(users, condition));

        LayoutManager layout = new BorderLayout();
        base.setLayout(layout);

        base.add(toolbar.component(), BorderLayout.NORTH);
        base.add(scrollPane, BorderLayout.CENTER);
    }

    private synchronized void updateWithFilter(List<User> users, String condition) {
        // TODO もっといい実装へリファクタ―
        this.users = users;

        if (model.size() == users.size()) {
            for (int i = 0; i < users.size(); i++) {
                if (model.get(i) != users.get(i)) {
                    model.set(i, users.get(i));
                }
            }
            for (int i = 0; i < model.size(); i++) {
                if (!model.get(i).getName().contains(condition)) {
                    model.remove(i);
                    i = 0;
                }
            }
        } else {
            model.clear();
            users.stream()
                 .filter(user -> user.getName().contains(condition))
                 .forEachOrdered(model::addElement);
        }
    }

    @Override
    public void update(@Nonnull List<User> users) {
        updateWithFilter(users, "");
    }

    @Override
    public JComponent component() {
        return base;
    }

    private static final class UserListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setText(((User) value).getName());

            return label;
        }

    }

}
