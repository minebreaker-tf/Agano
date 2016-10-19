package agano.runner.state;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class State {

    private String chatText;

    private State(String chatText) {
        this.chatText = chatText;
    }

    @Nonnull
    static State initialState() {
        return new State("");
    }

    @Nonnull
    private State copy() {
        return new State(chatText);
    }

    public String getChatText() {
        return chatText;
    }

    public State swapChatText(String chatText) {
        checkNotNull(chatText);

        State newState = copy();
        newState.chatText = chatText;
        return newState;
    }

}
