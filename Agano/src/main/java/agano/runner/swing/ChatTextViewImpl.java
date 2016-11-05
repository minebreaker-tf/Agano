package agano.runner.swing;

import agano.config.Config;
import agano.runner.state.State;
import agano.runner.state.User;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public final class ChatTextViewImpl implements ChatTextView {

    private final JPanel base;
    private final JTextPane chatText;

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
            String talks = user.getTalks().stream()
                               .map(msg -> "[" + now() + " - " + msg.getUser() + " ] " + msg.getLoad())
                               .collect(joining("\n"));
            if (!talks.equals(chatText.getText())) {
                chatText.setText(talks);
            }

        } else {
            chatText.setText("");
        }
    }

    private static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
