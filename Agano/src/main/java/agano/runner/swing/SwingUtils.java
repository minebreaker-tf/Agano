package agano.runner.swing;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public final class SwingUtils {

    private SwingUtils() {}

    public static Optional<Path> showFileSaveDialog(JFrame frame, String fileName) {
        JFileChooser dialog = new JFileChooser();
        dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dialog.setSelectedFile(new File(fileName));

        int ret = dialog.showSaveDialog(frame);

        if (ret == JFileChooser.APPROVE_OPTION) {
            return Optional.of(dialog.getSelectedFile().toPath());
        } else {
            return Optional.empty();
        }
    }

}
