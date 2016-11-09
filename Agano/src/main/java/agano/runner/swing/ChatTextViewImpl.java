package agano.runner.swing;

import agano.config.Config;
import agano.ipmsg.Message;
import agano.runner.state.State;
import agano.runner.state.User;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public final class ChatTextViewImpl implements ChatTextView {

    private static final Logger logger = LoggerFactory.getLogger(ChatTextViewImpl.class);

    private final JPanel base;
    private final JTextPane chatText;

    private final AttributeSet timeAttr;
    private final AttributeSet userAttr;
    private final AttributeSet defaultAttr;

    @Inject
    public ChatTextViewImpl(Config config) {
        base = new JPanel();

        chatText = new JTextPane();
        chatText.setFont(config.getFont());
        chatText.setBorder(BorderFactory.createEmptyBorder());
//        chatText.setMargin(new Insets(0, 0, 0, 0));
        chatText.setEditable(false);

        LayoutManager layout = new BorderLayout();
        base.setLayout(layout);
        base.add(chatText, BorderLayout.CENTER);

        MutableAttributeSet timeAttr = new SimpleAttributeSet();
        StyleConstants.ColorConstants.setForeground(timeAttr, SwingUtils.fromRGB(106, 135, 89));
        this.timeAttr = timeAttr;

        MutableAttributeSet userAttr = new SimpleAttributeSet();
        StyleConstants.ColorConstants.setForeground(userAttr, SwingUtils.fromRGB(255, 198, 109));
        StyleConstants.setItalic(userAttr, true);
        this.userAttr = userAttr;

        this.defaultAttr = chatText.getCharacterAttributes();
    }

    @Override
    public JComponent component() {
        return base;
    }

    @Override
    public void update(@Nonnull State state) {
        Optional<User> selected = state.getSelectedUser();
        if (selected.isPresent()) {
            User user = selected.get();

            DefaultStyledDocument doc = new DefaultStyledDocument();

            int offset = 0;

            try {
                for (Message each : user.getTalks()) {
                    String time = "[" + now() + "] ";
                    doc.insertString(offset, time, timeAttr);
                    offset += time.length();

                    String username = each.getUser();
                    doc.insertString(offset, username, userAttr);
                    offset += username.length();

                    String msg = " " + each.getLoad() + "\n";
                    doc.insertString(offset, msg, defaultAttr);
                    offset += msg.length();
                }
            } catch (BadLocationException e) {
                logger.warn("Failed to draw chat texts.", e);
            }
            chatText.setStyledDocument(doc);

        } else {
            chatText.setText("");
        }
    }

    private static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
