package agano.runner.swing;

import agano.config.Config;
import agano.util.AganoException;
import agano.util.Constants;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

@Singleton
public final class HelpDialog {

    private static final Logger logger = LoggerFactory.getLogger(HelpDialog.class);

    private static final URI repoUri = uri("https://bitbucket.org/minebreaker_tf/agano");
    private static final URI legalUri = uri("https://bitbucket.org/minebreaker_tf/agano/src/master/Agano/license");

    private final JDialog dialog;

    @Inject
    public HelpDialog(Config config) {
        dialog = new JDialog();
        dialog.setTitle(Constants.title);
        dialog.setSize(400, 250);
        dialog.setResizable(false);

        JLabel title = new JLabel("Agano");
        title.setFont(config.getFont().deriveFont((float) 36));
        title.setAlignmentY(Component.CENTER_ALIGNMENT);
        JLabel text = new JLabel("- The LAN Messenger -");
        text.setFont(config.getFont());
        HyperLinkLabel repo = new HyperLinkLabel("Sourcecode Repository", repoUri, config.getFont());
        HyperLinkLabel legal = new HyperLinkLabel("Powered by open-source software", legalUri, config.getFont());

        JButton close = new JButton("Close");
        close.setPreferredSize(close.getSize());
        close.addActionListener(e -> closeDialog());

        Box box = Box.createVerticalBox();
        box.add(title);
        box.add(text);
        box.add(Box.createVerticalStrut(20));
        box.add(repo.component());
        box.add(legal.component());
        box.add(Box.createVerticalStrut(10));
        box.add(close);

        box.setBorder(BorderFactory.createLineBorder(dialog.getBackground(), 10));

        dialog.add(box);
    }

    public void showDialog() {
        dialog.setVisible(true);
        dialog.requestFocus();
    }

    public void closeDialog() {
        dialog.setVisible(false);
    }

    private static URI uri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            logger.error("The internally defined URI is wrong.", e);
            throw new AganoException(e);
        }
    }

}
