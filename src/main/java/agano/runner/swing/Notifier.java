package agano.runner.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class Notifier {

    private static final Logger logger = LoggerFactory.getLogger(Notifier.class);

    private final TrayIcon trayIcon;

    public Notifier() {
        trayIcon = new TrayIcon(new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY)); // TODO
    }

    public void sendNotification(@Nullable String text) {
        try {
            SystemTray.getSystemTray().add(trayIcon);
            trayIcon.displayMessage("Message received", text, TrayIcon.MessageType.INFO);
            SystemTray.getSystemTray().remove(trayIcon);
        } catch (Exception e) {
            logger.info("Failed to send notification.", e);
        }

    }

}
