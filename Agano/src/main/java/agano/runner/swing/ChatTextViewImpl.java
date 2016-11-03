package agano.runner.swing;

import agano.runner.state.State;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

public final class ChatTextViewImpl implements ChatTextView {

    private final JPanel base;
    private final JTextPane chatText;

    @Inject
    public ChatTextViewImpl() {
        base = new JPanel();

        chatText = new JTextPane();
        chatText.setText("Mock chat!");
        chatText.setBorder(BorderFactory.createEmptyBorder());
//        chatText.setMargin(new Insets(0, 0, 0, 0));
        chatText.setEditable(false);

        LayoutManager layout = new BorderLayout();
        base.setLayout(layout);
        base.add(chatText, BorderLayout.CENTER);
    }

    @Override
    public JComponent component() {
        return base;
    }

    @Override
    public void update(@Nonnull State element) {

    }
}
