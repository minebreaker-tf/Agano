package agano.runner.swing;

import agano.config.Config;
import agano.libraries.materialicons.IconConstants;
import agano.runner.action.RefreshAction;
import agano.runner.parameter.SelectionParameter;
import agano.runner.state.User;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class UserListImpl implements UserList {

    private final JPanel base;
    private final JScrollPane scrollPane;
    private final JList<User> list;
    private final DefaultListModel<User> model;

    @Inject
    public UserListImpl(EventBus eventBus, Config config, RefreshAction refreshAction) {

        scrollPane = new JScrollPane();

        model = new DefaultListModel<>();

        list = new JList<>(model);
        list.setFont(config.getFont());
        list.setBorder(BorderFactory.createEmptyBorder());
        list.addListSelectionListener(e -> {
            User selected = model.get(e.getFirstIndex());
            eventBus.post(new SelectionParameter(e, selected));
        });
        list.setCellRenderer(new UserListCellRenderer());

        scrollPane.getViewport().setView(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        base = new JPanel();

        LayoutManager layout = new BorderLayout();
        base.setLayout(layout);

        FlowLayout panelLayout = new FlowLayout(FlowLayout.RIGHT, 0, 0);
        JPanel panel = new JPanel();
        panel.setLayout(panelLayout);
        panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        IconFontButton refresh = IconFontButton.newInstance(IconConstants.REFRESH);
        refresh.addActionListener(e -> refreshAction.execute());
        panel.add(refresh.component());
        panel.setBorder(BorderFactory.createEmptyBorder());

        base.add(panel, BorderLayout.NORTH);
        base.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public synchronized void update(@Nonnull List<User> state) {
        if (model.size() == state.size()) {
            for (int i = 0; i < state.size(); i++) {
                if (model.get(i) != state.get(i)) {
                    model.set(i, state.get(i));
                }
            }
        } else {
            model.removeAllElements();
            state.forEach(model::addElement);
        }
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
