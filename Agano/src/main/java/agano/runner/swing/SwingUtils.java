package agano.runner.swing;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public final class SwingUtils {

    private SwingUtils() {}

    private static Color fromRGB(int r, int g, int b) {
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public static TextAction beeplessDeletePrevCharAction(JTextComponent textComponent) {
        return new BeeplessDeletePrevCharAction(textComponent);
    }

    private static class BeeplessDeletePrevCharAction extends TextAction {

        private final JTextComponent textComponent;

        private BeeplessDeletePrevCharAction(JTextComponent textComponent) {
            super(DefaultEditorKit.deletePrevCharAction);
            this.textComponent = textComponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (textComponent.getCaretPosition() > 0) {
                try {
                    textComponent.getDocument().remove(textComponent.getCaretPosition() - 1, 1);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

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
