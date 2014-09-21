package com.github.letsdrink.intellijplugin;

import javax.swing.*;
import java.awt.event.*;

public class TranslationDialog extends JDialog {
    private final OkCallback okCallback;

    public interface OkCallback {
        void onClick(String key, String plText, String enText);
    }

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField key;
    private JTextField plText;
    private JTextField enText;

    public TranslationDialog(String keyText, String plText, String enText, OkCallback okCallback) {
        this.okCallback = okCallback;
        setContentPane(contentPane);
        setModal(true);
        this.plText.setText(plText);
        this.enText.setText(enText);
        key.setText(keyText);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        okCallback.onClick(key.getText(), plText.getText(), enText.getText());
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        TranslationDialog dialog = new TranslationDialog("test", "test", "test", new OkCallback() {
            @Override
            public void onClick(final String key, final String plText, final String enText) {
            }
        });
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
