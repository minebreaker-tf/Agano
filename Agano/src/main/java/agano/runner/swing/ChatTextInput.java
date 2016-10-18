package agano.runner.swing;

import agano.runner.parameter.SendMessageParameter;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class ChatTextInput extends JScrollPane {

    private final EventBus eventBus;
    private final JTextArea textArea;

    @Inject
    public ChatTextInput(EventBus eventBus) {

        this.eventBus = eventBus;

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

        this.getViewport().setView(textArea);

    }

}
