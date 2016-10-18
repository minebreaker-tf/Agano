package agano.runner.state;

public final class State {

    private String chatText;

    State(String chatText) {
        this.chatText = chatText;
    }

    private State newInstance() {
        return new State(chatText);
    }

    public String getChatText() {
        return chatText;
    }

    public State swapChatText(String chatText) {
        State newState = newInstance();
        newState.chatText = chatText;
        return newState;
    }

}
