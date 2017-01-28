package agano.runner.swing;

import agano.libraries.materialicons.IconConstants;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public final class ChatToolbarImpl implements ChatToolbar {

    private final JPanel base;

    @Inject
    public ChatToolbarImpl(HelpDialog dialog, @Assisted Consumer<ActionEvent> sendHandler) {

        base = new JPanel();
        base.setBorder(BorderFactory.createEmptyBorder());

        FlowLayout layout = new FlowLayout(FlowLayout.RIGHT, 0, 0);
        base.setLayout(layout);
        base.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        base.add(IconFontButton.newInstance(IconConstants.SEND, sendHandler::accept).component());
        base.add(IconFontButton.newInstance(IconConstants.HELP, e -> dialog.showDialog()).component());

    }

    @Override
    public JComponent component() {
        return base;
    }

}
