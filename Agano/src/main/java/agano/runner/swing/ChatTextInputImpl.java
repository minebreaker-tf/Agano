package agano.runner.swing;

import agano.runner.parameter.SendMessageParameter;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class ChatTextInputImpl implements ChatTextInput {

    private final EventBus eventBus;
    private final JScrollPane scrollPane;
    private final JTextArea textArea;

    @Inject
    public ChatTextInputImpl(EventBus eventBus) {

        this.eventBus = eventBus;

        scrollPane = new JScrollPane();

        textArea = new JTextArea();

        textArea.setLineWrap(true);
        textArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
        textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit-message");
        textArea.getActionMap().put("submit-message", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eventBus.post(new SendMessageParameter(textArea.getText()));
                SwingUtilities.invokeLater(() -> textArea.setText(""));
            }
        });

        textArea.setText("mock input");

        scrollPane.getViewport().setView(textArea);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());

    }

    @Override
    public JComponent component() {
        return scrollPane;
    }

}
