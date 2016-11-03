package agano.runner.parameter;

import agano.runner.state.User;

import javax.swing.event.ListSelectionEvent;

public final class SelectionParameter {

    private final ListSelectionEvent event;
    private final User selected;

    public SelectionParameter(ListSelectionEvent event, User selected) {
        this.event = event;
        this.selected = selected;
    }

    public ListSelectionEvent getEvent() {
        return event;
    }

    public User getSelected() {
        return selected;
    }

}
