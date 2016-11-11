package agano.runner.swing;

import agano.libraries.materialicons.IconConstants;
import agano.runner.parameter.Parameters;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Consumer;

public class UserListToolbarImpl implements UserListToolbar {

    private final JPanel base;
    private final JTextField field;

    @Inject
    public UserListToolbarImpl(EventBus eventBus, @Assisted Consumer<String> onTextChange) {
        base = new JPanel();
        base.setBorder(BorderFactory.createEmptyBorder());

        field = new JTextField();
        field.getDocument().addDocumentListener(new DocumentListener() {
            private void onChange() {
                onTextChange.accept(field.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }
        });

        BorderLayout layout = new BorderLayout();
        base.setLayout(layout);

        base.add(searcher(), BorderLayout.CENTER);

        IconFontButton refresh = IconFontButton.newInstance(IconConstants.REFRESH);
        refresh.addActionListener(e -> eventBus.post(new Parameters.RefreshParameter()));
        base.add(refresh.component(), BorderLayout.EAST);
    }

    private JComponent searcher() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(SwingUtils.fromRGB(62, 78, 93), 3));
        panel.setLayout(new BorderLayout());

        field.setBorder(BorderFactory.createEmptyBorder());
        field.setBackground(SwingUtils.fromRGB(60, 63, 65));
        field.setMargin(new Insets(0, 0, 0, 0));
        panel.add(field, BorderLayout.CENTER);

        IconFontButton searchIcon = IconFontButton.newInstance(IconConstants.SEARCH, false);
        panel.add(searchIcon.component(), BorderLayout.WEST);

        return panel;
    }

    @Override
    public JComponent component() {
        return base;
    }

}
