package com.github.letsdrink.intellijplugin;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.ArrayList;
import java.util.List;

public class TranslationUtils {
    public static List<PsiFile> getTranslationFiles(Project project) {
        List<PsiFile> translationFiles = new ArrayList<PsiFile>();
        Settings settings = Settings.getInstance(project);

        VirtualFile[] locales = VfsUtil.getChildren(settings.getOuzoProjectRoot().findChild("locales"));

        for (VirtualFile locale : locales) {
            if ("php".equals(locale.getExtension())) {
                translationFiles.add(PsiManager.getInstance(project).findFile(locale));
            }
        }
        return translationFiles;
    }
}
