package agano.runner.swing;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public interface ChatToolbar extends Viewable {

    public interface Factory {
        public ChatToolbar newInstance(Consumer<ActionEvent> callback);
    }

}
