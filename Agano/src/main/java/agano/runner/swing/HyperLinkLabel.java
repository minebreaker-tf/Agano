package agano.runner.swing;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

public final class HyperLinkLabel implements Viewable {

    private static final Logger logger = LoggerFactory.getLogger(HyperLinkLabel.class);

    private final JLabel label;

    public HyperLinkLabel(@Nonnull String text, @Nonnull URI linkTo, @Nonnull Font original) {
        label = new JLabel(checkNotNull(text));
        checkNotNull(linkTo);

        label.setFont(linkFont(original));
        label.setForeground(SwingUtils.fromRGB(40, 123, 222));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    try {
                        Desktop.getDesktop().browse(linkTo);
                    } catch (IOException ex) {
                        logger.warn("Failed to launch the browser. Event: {}", e, ex);
                    }
                }
            }
        });
    }

    private Font linkFont(Font font) {
        return font.deriveFont(ImmutableMap.of(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON));
    }

    @Override
    public JComponent component() {
        return label;
    }

}
