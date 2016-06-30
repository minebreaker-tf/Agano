package agano;

import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Vector;

public class UiCheck {

    // TODO: クラスを実装したらそれで置き換える

    public static void main(String[] args) throws Exception {

        String theme = "com.bulenkov.darcula.DarculaLaf";
//        Font font = Font.createFont();

        UIManager.setLookAndFeel(theme);

        JFrame window = new JFrame();
        window.setSize(600, 400);

        JPanel panel = new JPanel();

        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        panel.setLayout(layout);

        JLabel label = new JLabel();
        label.setText(theme);
        panel.add(label);

        JButton button = new JButton();
        button.setText("SampleButton");
        panel.add(button);

        JCheckBox checkBox = new JCheckBox();
        panel.add(checkBox);

        JComboBox comboBox = new JComboBox();
        panel.add(comboBox);

        Vector<String> listContent = new Vector<String>(Arrays.asList("first", "second", "third"));
        JList list = new JList(listContent);
        panel.add(list);

//        JOptionPane optionPane = new JOptionPane();
//        panel.add(optionPane);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setValue(50);
        panel.add(progressBar);

        JScrollBar scrollBar = new JScrollBar();
//        scrollBar.setSize(100, 100);
        panel.add(scrollBar);

        JSlider slider = new JSlider();
        panel.add(slider);

        JSpinner spinner = new JSpinner();
        panel.add(spinner);

        JTable table = new JTable(
                new String[][] {{"1", "2", "3"}, {"4", "5", "6"}},
                new String[] {"A", "B", "C"});
        panel.add(table);

        JTextField textField = new JTextField();
        textField.setText("Text Field");
        panel.add(textField);

        JTextArea textArea = new JTextArea();
        textArea.setText("Text Area");
        panel.add(textArea);

        JEditorPane editor = new JEditorPane();
        editor.setText("Editor");
        panel.add(editor);

        JToolBar toolbar = new JToolBar();
        panel.add(toolbar);

        JToolTip toolTip = new JToolTip();
        panel.add(toolTip);

        JTree tree = new JTree();
        panel.add(tree);

        JViewport viewport = new JViewport();
        panel.add(viewport);


        JMenuBar menuBar = new JMenuBar();
        window.setJMenuBar(menuBar);
        JMenu menu = new JMenu("Menu1");
        menuBar.add(menu);
        JMenuItem menuItem = new JMenuItem("MenuItem1");
        menu.add(menuItem);
        menu.add(new JSeparator());
        JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Radio");
        menu.add(rbMenuItem);
        JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Check");
        menu.add(cbMenuItem);

        JTabbedPane tabbedPane = new JTabbedPane();
        panel.add(tabbedPane);

//        JFileChooser fileChooser = new JFileChooser();
//        panel.add(fileChooser);
//
//        JColorChooser colorChooser = new JColorChooser();
//        panel.add(colorChooser);

        window.add(panel);

        window.setVisible(true);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
