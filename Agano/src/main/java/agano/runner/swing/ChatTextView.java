package agano.runner.swing;

import javax.swing.*;
import java.awt.*;

public final class ChatTextView extends JPanel {

    public ChatTextView() {
        JTextPane chatText = new JTextPane();
        chatText.setText("Mock chat!");
        chatText.setBorder(BorderFactory.createEmptyBorder());
//        chatText.setMargin(new Insets(0, 0, 0, 0));
        chatText.setEditable(false);

        LayoutManager layout = new BorderLayout();
        this.setLayout(layout);
        this.add(chatText, BorderLayout.CENTER);
    }

}
