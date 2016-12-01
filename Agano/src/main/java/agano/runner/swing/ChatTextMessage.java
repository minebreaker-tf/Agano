package agano.runner.swing;

import agano.libraries.materialicons.IconConstants;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public final class ChatTextMessage implements Viewable {

    private final IconFontButton icon;
    private final Box base;
    private final JLabel label;

    public ChatTextMessage(@Nonnull IconConstants iconConstants, @Nonnull Font font, @Nonnull String text, @Nonnull ActionListener onClick) {
        this.icon = IconFontButton.newBiggerInstance(iconConstants, onClick);
        base = Box.createHorizontalBox();
        label = new JLabel(text);
        label.setFont(font);

//        BorderLayout layout = new BorderLayout();
//        base.setLayout(layout);

        base.add(icon.component());
        base.add(label);

        base.setBorder(BorderFactory.createMatteBorder(2, 10, 2, 10, SwingUtils.appBackgorund()));

    }

    @Nonnull
    @Override
    public JComponent component() {
        return base;
    }

}
