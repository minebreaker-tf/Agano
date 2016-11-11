package agano.runner.swing;

import agano.config.Config;
import agano.runner.parameter.SendMessageParameter;
import agano.util.AganoException;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;

public final class ChatTextInputImpl implements ChatTextInput {

    private static final Logger logger = LoggerFactory.getLogger(ChatTextViewImpl.class);

    private final EventBus eventBus;
    private final JPanel panel;
    private final JScrollPane scrollPane;
    private final JTextArea textArea;

    @Inject
    public ChatTextInputImpl(ChatToolbar.Factory chatToolbar, EventBus eventBus, Config config) {

        this.eventBus = eventBus;
        textArea = new JTextArea();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());

        scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        textArea.setFont(config.getFont());
        textArea.setLineWrap(true);
        textArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
        textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit-message");
        textArea.getActionMap().put("submit-message", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submit();
            }
        });
        textArea.setTransferHandler(new DropHandler());

        ChatToolbar toolbar = chatToolbar.newInstance(e -> submit());

        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(toolbar.component(), BorderLayout.NORTH);

    }

    private void submit() {
        eventBus.post(new SendMessageParameter(textArea.getText()));
        SwingUtilities.invokeLater(() -> textArea.setText(""));
    }

    @Override
    public JComponent component() {
        return panel;
    }

    private final class DropHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferSupport transferSupport) {
            return transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor) ||
                   transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport transferSupport) {
            try {
                if (transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) transferSupport.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    logger.debug("Dropped: {}", files);
                    return true;
                } else if (transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String str = (String) transferSupport.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    textArea.insert(str, textArea.getCaretPosition());
                    return true;
                } else {
                    return false;
                }
            } catch (UnsupportedFlavorException | IOException e) {
                throw new AganoException(e);
            }
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection(nullToEmpty(textArea.getSelectedText()));
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == MOVE) {
                textArea.replaceSelection("");
            }
        }

    }

}
