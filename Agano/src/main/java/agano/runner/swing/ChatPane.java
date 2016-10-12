package agano.runner.swing;

import javax.swing.*;
import java.awt.*;

public class ChatPane extends JSplitPane {

    private JTextPane chatText;
    private ChatTextInput textInput;

    public ChatPane() {
        super(VERTICAL_SPLIT);
        this.setMinimumSize(new Dimension(100, 100));

        this.setBorder(BorderFactory.createEmptyBorder());

        chatText = new JTextPane();
        chatText.setText("Mock chat!");
        chatText.setBorder(BorderFactory.createEmptyBorder());
        chatText.setMargin(new Insets(0, 0, 0, 0));
        chatText.setEditable(false);

        textInput = new ChatTextInput();
        textInput.setBorder(BorderFactory.createEmptyBorder());

        this.setLeftComponent(chatText);
        this.setRightComponent(textInput);

        this.setDividerLocation(300) ;
        this.setDividerSize(5);
    }

    public JTextPane getChatText() {
        return chatText;
    }

    public ChatTextInput getTextInput() {
        return textInput;
    }

}
