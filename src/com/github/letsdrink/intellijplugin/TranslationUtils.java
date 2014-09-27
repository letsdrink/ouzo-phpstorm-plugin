package com.github.letsdrink.intellijplugin;


import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
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

    public static boolean isParent(String parent, String key) {
        if (!key.startsWith(parent)) {
            return false;
        }
        return key.charAt(parent.length()) == '.';
    }

    static String getParentKey(String key) {
        List<String> parts = Splitter.on(".").splitToList(key);
        if (parts.size() == 1) {
            return null;
        }
        return Joiner.on(".").join(Iterables.limit(parts, parts.size() - 1));
    }

    static boolean isTranslationFile(PsiFile file) {
        return file.getParent().getName().equals("locales");
    }
}
