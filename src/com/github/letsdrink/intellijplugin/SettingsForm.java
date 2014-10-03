package com.github.letsdrink.intellijplugin;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;


public class SettingsForm implements Configurable {
    private final Project project;

    private TextFieldWithBrowseButton ouzoProjectRoot;
    private JPanel panel;
    private JButton resetPath;
    private JCheckBox annotateMissing;
    private JCheckBox annotateUnused;

    public SettingsForm(@NotNull final Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Ouzo";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        ouzoProjectRoot.getButton().addMouseListener(createPathButtonMouseListener(ouzoProjectRoot.getTextField(), FileChooserDescriptorFactory.createSingleFolderDescriptor()));
        resetPath.addMouseListener(createResetPathButtonMouseListener(ouzoProjectRoot.getTextField(), null));
        return panel;
    }

    @Override
    public boolean isModified() {
        Settings settings = getSettings();
        return !Objects.equals(settings.ouzoProjectRoot, ouzoProjectRoot.getText()) ||
                settings.annotateMissingTranslation != annotateMissing.isSelected() ||
                settings.annotateUnusedTranslation != annotateUnused.isSelected()
                ;
    }

    @Override
    public void apply() throws ConfigurationException {
        Settings settings = getSettings();
        settings.ouzoProjectRoot = ouzoProjectRoot.getText();
        settings.annotateMissingTranslation = annotateMissing.isSelected();
        settings.annotateUnusedTranslation = annotateUnused.isSelected();
    }

    @Override
    public void reset() {
        Settings settings = getSettings();
        ouzoProjectRoot.setText(settings.ouzoProjectRoot);
        annotateMissing.setSelected(settings.annotateMissingTranslation);
        annotateUnused.setSelected(settings.annotateUnusedTranslation);
    }

    @Override
    public void disposeUIResources() {

    }

    private Settings getSettings() {
        return Settings.getInstance(project);
    }

    private MouseListener createPathButtonMouseListener(final JTextField textField, final FileChooserDescriptor fileChooserDescriptor) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                VirtualFile projectDirectory = project.getBaseDir();
                VirtualFile selectedFile = FileChooser.chooseFile(
                        fileChooserDescriptor,
                        project,
                        VfsUtil.findRelativeFile(textField.getText(), projectDirectory)
                );

                if (null == selectedFile) {
                    return; // Ignore but keep the previous path
                }

                String path = VfsUtil.getRelativePath(selectedFile, projectDirectory, '/');
                if (null == path) {
                    path = selectedFile.getPath();
                }

                textField.setText(path);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        };
    }

    private MouseListener createResetPathButtonMouseListener(final JTextField textField, final String defaultValue) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                textField.setText(defaultValue);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        };
    }
}
