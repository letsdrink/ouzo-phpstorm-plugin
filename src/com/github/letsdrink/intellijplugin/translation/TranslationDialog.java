package com.github.letsdrink.intellijplugin.translation;

import com.github.letsdrink.intellijplugin.ui.TranslationsEditor;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.Map;

import static java.util.Arrays.asList;

public class TranslationDialog extends JDialog {
    private final OkCallback okCallback;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void showDialog() {
        pack();
        setTitle("Input translation key");
        setSize(470, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void disableKey() {
        key.setEnabled(false);
    }

    public interface OkCallback {
        void onClick(String key, Map<String, String> translations);
    }

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private TranslationsEditor translations;
    private JComboBox key;

    public TranslationDialog(final TranslationModel translationModel, OkCallback okCallback) {
        this.okCallback = okCallback;
        translations.initialize(translationModel.getLangTextMap());

        setContentPane(contentPane);
        setModal(true);

        key.setEditable(true);
        key.setModel(new DefaultComboBoxModel<String>(translationModel.getKeys().toArray(new String[0])));
        if (!translationModel.getKeys().isEmpty()) {
            key.setSelectedIndex(0);
        }

        key.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                String item = (String)evt.getItem();

                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    Map<String, String> langTextMap = translationModel.getLangTextMap(item);
                    if (langTextMap != null) {
                        translations.setValues(langTextMap);
                    }
                }
            }
        });

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
        String keyText = (String) key.getSelectedItem();
        if (StringUtils.isBlank(keyText) || keyText.endsWith(".")) {
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
        Map<String, Map<String, String>> map = ImmutableMap.of(
                "key1", (Map<String, String>) ImmutableMap.of(
                        "en", "en val1",
                        "pl", "pl val1"
                ),
                "key2", ImmutableMap.of(
                        "en", "en val2",
                        "pl", "pl val2"
                )
        );


        TranslationDialog dialog = new TranslationDialog(new TranslationModel(asList("key1", "key2", "prev."), map), new OkCallback() {
            @Override
            public void onClick(final String key, Map<String, String> translations) {
            }
        });
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
