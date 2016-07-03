package agano.runner;

import agano.runner.ui.MainForm;
import agano.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        bootstrap();
    }

    private static void bootstrap() {

        try {
            // 別に例外が起きても死ぬわけじゃないので適当
            UIManager.setLookAndFeel(Constants.defaultLaf);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        MainForm form = new MainForm();
        try {
            Font defaultFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    Main.class.getResourceAsStream(
                            Constants.defaultFont)
            );
            defaultFont = defaultFont.deriveFont(Font.PLAIN, Constants.defaultFontSize);
//            form.setFont(defaultFont);
            form.getUserList().getList().setFont(defaultFont);
            form.getChatPane().getChatText().setFont(defaultFont);
            form.getChatPane().getTextInput().setFont(defaultFont);

        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
