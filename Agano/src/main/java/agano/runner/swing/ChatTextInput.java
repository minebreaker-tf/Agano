package agano.runner.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChatTextInput extends JScrollPane {

    private final JTextArea textArea;

    public ChatTextInput() {

        textArea = new JTextArea();

        textArea.setLineWrap(true);
        textArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
        textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit-message");
        textArea.getActionMap().put("submit-message", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // send event
                textArea.setText("");
            }
        });

        this.getViewport().setView(textArea);

    }

    public JTextArea getTextArea() {
        return textArea;
    }

}
