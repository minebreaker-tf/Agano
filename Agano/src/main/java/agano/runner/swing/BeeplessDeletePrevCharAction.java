package agano.runner.swing;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;

public final class BeeplessDeletePrevCharAction extends TextAction {

    private final JTextComponent textComponent;

    private BeeplessDeletePrevCharAction(JTextComponent textComponent) {
        super(DefaultEditorKit.deletePrevCharAction);
        this.textComponent = textComponent;
    }

    public static TextAction beeplessDeletePrevCharAction(JTextComponent textComponent) {
        return new BeeplessDeletePrevCharAction(textComponent);
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
