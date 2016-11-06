package agano.runner.parameter;

import java.awt.event.WindowEvent;

public class WindowFocusedParameter {

    private final boolean windowFocused;

    public WindowFocusedParameter(WindowEvent e) {
        this.windowFocused = e.getWindow().isFocused();
    }

    public boolean isWindowFocused() {
        return windowFocused;
    }

}
