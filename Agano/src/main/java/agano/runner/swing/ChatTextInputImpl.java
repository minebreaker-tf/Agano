package agano.runner.swing;

import agano.config.Config;
import agano.runner.parameter.SendMessageParameter;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public final class ChatTextInputImpl implements ChatTextInput {

    private final EventBus eventBus;
    private final JPanel panel;
    private final JScrollPane scrollPane;
    private final JTextArea textArea;

    @Inject
    public ChatTextInputImpl(ChatToolbar.Factory chatToolbar, EventBus eventBus, Config config) {

        this.eventBus = eventBus;
        textArea = new JTextArea();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());

        scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        textArea.setFont(config.getFont());
        textArea.setLineWrap(true);
        textArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
        textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit-message");
        textArea.getActionMap().put("submit-message", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submit();
            }
        });

        ChatToolbar toolbar = chatToolbar.newInstance(e -> submit());

        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(toolbar.component(), BorderLayout.NORTH);

    }

    private void submit() {
        eventBus.post(new SendMessageParameter(textArea.getText()));
        SwingUtilities.invokeLater(() -> textArea.setText(""));
    }

    @Override
    public JComponent component() {
        return panel;
    }

}
