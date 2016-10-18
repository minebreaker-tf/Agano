package agano.runner.swing;

import com.google.inject.Inject;

import javax.swing.*;

public final class ChatPane extends JSplitPane {

    private final ChatTextView chatView;
    private final ChatTextInput input;

    @Inject
    public ChatPane(ChatTextView chatView, ChatTextInput input) {
        super(VERTICAL_SPLIT);

        this.chatView = chatView;
        this.input = input;

//        this.setMinimumSize(new Dimension(100, 100));
        this.setBorder(BorderFactory.createEmptyBorder());

        input.setBorder(BorderFactory.createEmptyBorder());

        this.setLeftComponent(chatView);
        this.setRightComponent(input);

        this.setDividerLocation(300) ;
        this.setDividerSize(5);
    }

}
