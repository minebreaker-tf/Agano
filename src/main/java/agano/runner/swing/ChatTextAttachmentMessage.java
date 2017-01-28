package agano.runner.swing;

import agano.ipmsg.Attachment;
import agano.libraries.materialicons.IconConstants;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public final class ChatTextAttachmentMessage implements Viewable {

    private final IconFontButton icon;
    private final JPanel base;
    private final JLabel label;

    public ChatTextAttachmentMessage(
            @Nonnull IconConstants iconConstants,
            @Nonnull Font font,
            @Nonnull Attachment attachment,
            @Nonnull ActionListener onClick) {
        icon = IconFontButton.newBiggerInstance(iconConstants, Colors.listBackground, onClick);

        label = new JLabel(attachment.getFileName() + " (" + attachment.getFilesize() + ")");
        label.setFont(font);

        base = new JPanel();
        base.setLayout(new BorderLayout());
        base.add(icon.component(), BorderLayout.WEST);
        base.add(label, BorderLayout.CENTER);

        base.setBorder(BorderFactory.createMatteBorder(2, 20, 2, 20, Colors.appBackgorund));
        base.setBackground(Colors.listBackground);
        base.setOpaque(true);

    }

    @Nonnull
    @Override
    public JComponent component() {
        return base;
    }

}
