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
    public ChatToolbarImpl(@Assisted Consumer<ActionEvent> callback) {

        base = new JPanel();
        base.setBorder(BorderFactory.createEmptyBorder());

        FlowLayout layout = new FlowLayout(FlowLayout.RIGHT, 0, 0);
        base.setLayout(layout);
        base.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        IconFontButton refresh = IconFontButton.newInstance(IconConstants.SEND);
        refresh.addActionListener(callback::accept);
        base.add(refresh.component());

    }

    @Override
    public JComponent component() {
        return base;
    }

}
