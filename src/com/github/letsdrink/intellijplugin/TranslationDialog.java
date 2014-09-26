package com.github.letsdrink.intellijplugin;

import com.github.letsdrink.intellijplugin.ui.TranslationsEditor;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class TranslationDialog extends JDialog {
    private final OkCallback okCallback;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public interface OkCallback {
        void onClick(String key, Map<String, String> translations);
    }

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private TranslationsEditor translations;
    private JComboBox key;

    public TranslationDialog(List<String> keyTexts, Map<String, String> translationsMap, OkCallback okCallback) {
        this.okCallback = okCallback;
        translations.initialize(translationsMap);

        setContentPane(contentPane);
        setModal(true);

        key.setEditable(true);
        key.setModel(new DefaultComboBoxModel<String>(keyTexts.toArray(new String[0])));
        if (!keyTexts.isEmpty()) {
            key.setSelectedIndex(0);
        }

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
        String keyText = (String)key.getSelectedItem();
        if (StringUtils.isBlank(keyText)) {
            return;
        }
        okCallback.onClick(keyText, translations.getTranslations());
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        TranslationDialog dialog = new TranslationDialog(asList("test"), ImmutableMap.of("en", "asd"), new OkCallback() {
            @Override
            public void onClick(final String key, Map<String, String> translations) {
            }
        });
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
