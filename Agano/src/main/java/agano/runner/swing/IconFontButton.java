package agano.runner.swing;

import agano.libraries.materialicons.IconConstants;
import agano.util.AganoException;
import agano.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public final class IconFontButton implements Viewable {

    private static final Logger logger = LoggerFactory.getLogger(IconFontButton.class);

    private static final Font font = createFont();
    private static final int iconFontSize = 18;

    private final JButton button;

    private IconFontButton(IconConstants icon, ActionListener listener) {
        button = new JButton(icon.getCodePoint());

        button.setUI(new BasicButtonUI());
        button.setFont(font);
        button.setPreferredSize(new Dimension(24, 24));
        button.setBackground(SwingUtils.appBackgorund());
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(SwingUtils.editorText(), 1));
        button.setBorderPainted(false);

        if (listener != null) {
            button.addActionListener(listener);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBorderPainted(true);
                    button.setBackground(SwingUtils.buttonHighlight());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBorderPainted(false);
                    button.setBackground(SwingUtils.appBackgorund());
                }
            });
        }
    }

    @Nonnull
    public static IconFontButton newInstance(@Nonnull IconConstants icon) {
        return new IconFontButton(checkNotNull(icon), null);
    }

    @Nonnull
    public static IconFontButton newInstance(@Nonnull IconConstants icon, @Nullable ActionListener listener) {
        return new IconFontButton(checkNotNull(icon), listener);
    }

    private static Font createFont() {
        try {
            return Font.createFont(
                    Font.TRUETYPE_FONT,
                    IconFontButton.class.getResourceAsStream(Constants.iconFont)
            ).deriveFont(Font.PLAIN, iconFontSize);

        } catch (FontFormatException | IOException e) {
            logger.warn("Failed to load font.", e);
            throw new AganoException(e);
        }
    }

    @Override
    public JComponent component() {
        return button;
    }

}
