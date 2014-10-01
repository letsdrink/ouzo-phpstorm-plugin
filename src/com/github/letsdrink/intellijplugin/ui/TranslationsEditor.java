package com.github.letsdrink.intellijplugin.ui;

import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.Map;

public class TranslationsEditor extends JPanel {
    private final Map<String, JTextField> inputs = new HashMap<>();

    public void initialize(Map<String, String> translationsMap) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        for (Map.Entry<String, String> entry : translationsMap.entrySet()) {
            final JTextField textField = new JTextField();
            textField.setPreferredSize(new Dimension(400, 25));
            textField.setText(entry.getValue());
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    textField.selectAll();
                }
            });
            inputs.put(entry.getKey(), textField);

            FlowLayout layout = new FlowLayout();
            JPanel panel = new JPanel();
            panel.setLayout(layout);
            Label label = new Label(entry.getKey());
            label.setPreferredSize(new Dimension(20, 25));
            panel.add(label);
            panel.add(textField);
            add(panel);
        }
    }

    public void setValues(Map<String, String> translationsMap) {
        for (Map.Entry<String, String> entry : translationsMap.entrySet()) {
            JTextField textField = inputs.get(entry.getKey());
            if (textField != null && StringUtils.isNotBlank(entry.getValue())) {
                textField.setText(entry.getValue());
            }
        }
    }

    public Map<String, String> getTranslations() {
        Map<String, String> translations = new HashMap<>();
        for (Map.Entry<String, JTextField> entry : inputs.entrySet()) {
            translations.put(entry.getKey(), entry.getValue().getText());
        }
        return translations;
    }
}