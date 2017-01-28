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

    private static final int size = 24;
    private static final int sizeB = 48;
    private static final Font font = createFont(18);
    private static final Font fontB = createFont(42);

    private final JButton button;
    private final Color bgColor;

    private IconFontButton(IconConstants icon, ActionListener listener, int size, Font font, Color backgroundColor) {
        button = new JButton(icon.getCodePoint());
        bgColor = backgroundColor == null ? Colors.appBackgorund : backgroundColor;

        button.setUI(new BasicButtonUI());
        button.setFont(font);
        button.setPreferredSize(new Dimension(size, size));
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Colors.editorText, 1));
        button.setBorderPainted(false);

        if (listener != null) {
            button.addActionListener(listener);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
//                    button.setBorderPainted(true);
                    button.setBackground(Colors.buttonHighlight);
                }

                @Override
                public void mouseExited(MouseEvent e) {
//                    button.setBorderPainted(false);
                    button.setBackground(bgColor);
                }
            });
        }
    }

    @Nonnull
    public static IconFontButton newInstance(@Nonnull IconConstants icon) {
        return new IconFontButton(checkNotNull(icon), null, size, font, null);
    }

    @Nonnull
    public static IconFontButton newInstance(@Nonnull IconConstants icon, @Nullable ActionListener onClick) {
        return new IconFontButton(checkNotNull(icon), onClick, size, font, null);
    }

    @Nonnull
    public static IconFontButton newBiggerInstance(@Nonnull IconConstants icon, @Nullable Color backgroundColor, @Nullable ActionListener onClick) {
        return new IconFontButton(checkNotNull(icon), onClick, sizeB, fontB, backgroundColor);
    }

    private static Font createFont(int size) {
        try {
            return Font.createFont(
                    Font.TRUETYPE_FONT,
                    IconFontButton.class.getResourceAsStream(Constants.iconFont)
            ).deriveFont(Font.PLAIN, size);

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
