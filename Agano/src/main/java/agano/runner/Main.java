package agano.runner;

import agano.runner.controller.Controller;
import agano.runner.parameter.Binder;
import agano.runner.swing.MainForm;
import agano.util.Constants;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public final class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        setLaf();

        EventBus eventBus = new EventBus();
        MainForm form = prepareWindow();
        Controller controller = new Controller(new Binder(form));
        eventBus.register(controller);

    }

    private static void setLaf() {
        try {
            // 別に例外が起きても死ぬわけじゃないので適当
            UIManager.setLookAndFeel(Constants.defaultLaf);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            logger.warn("Failed to set laf.", e);
        }
    }

    private static MainForm prepareWindow() {
        MainForm form = new MainForm();
        try {
            Font defaultFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    Main.class.getResourceAsStream(Constants.defaultFont)
            );
            // TODO: Fontの設定の責任をForm側へ移す
            defaultFont = defaultFont.deriveFont(Font.PLAIN, Constants.defaultFontSize);
//            form.setFont(defaultFont);
            form.getUserList().getList().setFont(defaultFont);
            form.getChatPane().getChatText().setFont(defaultFont);
            form.getChatPane().getTextInput().getTextArea().setFont(defaultFont);

        } catch (FontFormatException | IOException e) {
            logger.warn("Failed to load font.", e);
        }

        return form;
    }

}
