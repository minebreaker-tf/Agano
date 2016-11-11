package agano.runner.parameter;

import agano.runner.state.User;

import javax.swing.event.ListSelectionEvent;
import java.awt.event.WindowEvent;

public final class Parameters {

    private Parameters() {}

    public static class WindowFocusedParameter {

        private final boolean windowFocused;

        public WindowFocusedParameter(WindowEvent e) {
            this.windowFocused = e.getWindow().isFocused();
        }

        public boolean isWindowFocused() {
            return windowFocused;
        }

    }

    public static final class SelectionParameter {

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

    public static final class RefreshParameter {}

}
