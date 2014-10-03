package com.github.letsdrink.intellijplugin;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(
        name = "OuzoPluginSettings",
        storages = {
                @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
                @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/ouzo.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class Settings implements PersistentStateComponent<Settings> {

    public String ouzoProjectRoot;
    public boolean annotateUnusedTranslation = true;
    public boolean annotateMissingTranslation = true;
    private Project project;

    @Override
    public Settings getState() {
        return this;
    }
    @Override
    public void loadState(Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static Settings getInstance(Project project) {
        Settings settings = ServiceManager.getService(project, Settings.class);
        settings.project = project;
        return settings;
    }

    public VirtualFile getOuzoProjectRoot() {
        if (ouzoProjectRoot != null) {
            return project.getBaseDir().findFileByRelativePath(ouzoProjectRoot);
        }
        return project.getBaseDir();
    }
}