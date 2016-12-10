package agano.runner.swing;

import agano.config.Config;
import agano.libraries.materialicons.IconConstants;
import agano.runner.parameter.Parameters;
import agano.util.Constants;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.util.function.Consumer;

public class UserListToolbarImpl implements UserListToolbar {

    private final JPanel base;
    private final JTextField field;
    private final Consumer<String> onTextChange;

    @Inject
    public UserListToolbarImpl(EventBus eventBus, Config config, @Assisted Consumer<String> onTextChange) {
        base = new JPanel();
        base.setBorder(BorderFactory.createEmptyBorder());

        this.onTextChange = onTextChange;

        field = new JTextField();
        field.getDocument().addDocumentListener(new DocumentListenerAdaptor());
        field.getActionMap().put(DefaultEditorKit.deletePrevCharAction, SwingUtils.beeplessDeletePrevCharAction(field));
        field.setFont(config.getFont().deriveFont((float) Constants.defaultSearchFontSize));

        base.setLayout(new BorderLayout());

        base.add(searcher(), BorderLayout.CENTER);
        base.add(
                IconFontButton.newInstance(
                        IconConstants.REFRESH,
                        e -> eventBus.post(new Parameters.RefreshParameter())
                ).component(),
                BorderLayout.EAST
        );
    }

    private JComponent searcher() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Colors.textFieldBorder, 3));
        panel.setLayout(new BorderLayout());

        field.setBorder(BorderFactory.createEmptyBorder());
        field.setBackground(Colors.appBackgorund);
        field.setMargin(new Insets(0, 0, 0, 0));
        panel.add(field, BorderLayout.CENTER);

        IconFontButton searchIcon = IconFontButton.newInstance(IconConstants.SEARCH);
        panel.add(searchIcon.component(), BorderLayout.WEST);

        return panel;
    }

    @Override
    public JComponent component() {
        return base;
    }

    private class DocumentListenerAdaptor implements DocumentListener {

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
    }

}
