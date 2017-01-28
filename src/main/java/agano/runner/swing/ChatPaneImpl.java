package agano.runner.swing;

import agano.runner.state.State;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.swing.*;

import static javax.swing.JSplitPane.VERTICAL_SPLIT;

public final class ChatPaneImpl implements ChatPane {

    private final JSplitPane base;
    private final ChatTextView chatView;
    private final ChatTextInput input;

    @Inject
    public ChatPaneImpl(ChatTextView chatView, ChatTextInput input) {

        base = new JSplitPane(VERTICAL_SPLIT);

        this.chatView = chatView;
        this.input = input;

//        this.setMinimumSize(new Dimension(100, 100));
        base.setBorder(BorderFactory.createEmptyBorder());

        base.setLeftComponent(chatView.component());
        base.setRightComponent(input.component());

        base.setDividerLocation(300);
        base.setDividerSize(5);
    }

    @Override
    public void update(@Nonnull State state) {
        chatView.update(state);
    }

    @Override
    public JComponent component() {
        return base;
    }

}
