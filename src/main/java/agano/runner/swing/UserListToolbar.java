package agano.runner.swing;

import java.util.function.Consumer;

public interface UserListToolbar extends Viewable {

    public interface Factory {
        public UserListToolbar newInstance(Consumer<String> onTextChange);
    }

}
