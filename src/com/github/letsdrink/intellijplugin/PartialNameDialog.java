package com.github.letsdrink.intellijplugin;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class PartialNameDialog extends DialogWrapper {
    private JLabel nameLabel;
    private JTextField nameField;

    public PartialNameDialog(@Nullable Project project) {
        super(project);
        this.nameLabel = new JLabel("Name: ");
        this.nameField = new JTextField();
        this.nameField.setMinimumSize(new Dimension(200, 20));
        this.nameField.setPreferredSize(new Dimension(200, 20));

        setTitle("Extract partial");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        panel.add(nameLabel);
        panel.add(nameField);

        return panel;
    }

    public String getName() {
        return nameField.getText();
    }
}
