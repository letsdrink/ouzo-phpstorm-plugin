package com.github.letsdrink.intellijplugin;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class TranslationDialog extends JDialog {
    private final OkCallback okCallback;

    public interface OkCallback {
        void onClick(String key);
    }

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField key;
    private JTable translations;

    public TranslationDialog(String keyText, Map<String, String> translationsMap, OkCallback okCallback) {
        this.okCallback = okCallback;
        setContentPane(contentPane);
        setModal(true);

        this.translations.setModel(new TranslationsTableModel(translationsMap));

        JTextField textField = new JTextField();
        textField.setMargin(new Insets(0, 0 ,0, 0));
        DefaultCellEditor singleClick = new DefaultCellEditor(textField);
        singleClick.setClickCountToStart(1);

        this.translations.setDefaultEditor(this.translations.getColumnClass(1), singleClick);
        this.translations.setRowHeight(30);
        this.translations.setCellSelectionEnabled(false);
        this.translations.getColumnModel().getColumn(0).setMaxWidth(40);

        key.setText(keyText);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (translations.getCellEditor() != null) {
                    translations.getCellEditor().stopCellEditing();
                }
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
        String keyText = key.getText();
        if (StringUtils.isBlank(keyText)) {
            return;
        }
        okCallback.onClick(keyText);
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        TranslationDialog dialog = new TranslationDialog("test", ImmutableMap.of("en", "asd"), new OkCallback() {
            @Override
            public void onClick(final String key) {
            }
        });
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
