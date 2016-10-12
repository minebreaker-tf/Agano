package agano.runner.parameter;

import agano.runner.swing.MainForm;

public class Binder {

    private final MainForm form;

    public Binder(MainForm form) {
        this.form = form;
    }

    public void bind(State state) {
        form.getChatPane().getChatText().setText(state.getChatText());
    }

}
