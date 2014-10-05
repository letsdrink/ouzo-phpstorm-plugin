package com.github.letsdrink.intellijplugin.translation;


import com.github.letsdrink.intellijplugin.Settings;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.BinaryExpression;
import com.jetbrains.php.lang.psi.elements.FunctionReference;

import java.util.ArrayList;
import java.util.List;

public class TranslationUtils {
    public static List<PsiFile> getTranslationFiles(Project project) {
        List<PsiFile> translationFiles = new ArrayList<PsiFile>();
        Settings settings = Settings.getInstance(project);

        VirtualFile localesPath = settings.getOuzoProjectRoot().findChild("locales");
        if (localesPath == null) {
            return translationFiles;
        }
        VirtualFile[] locales = VfsUtil.getChildren(localesPath);

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

    public static String getParentKey(String key) {
        List<String> parts = Splitter.on(".").splitToList(key);
        if (parts.size() == 1) {
            return null;
        }
        return Joiner.on(".").join(Iterables.limit(parts, parts.size() - 1));
    }

    public static boolean isTranslationFile(PsiFile file) {
        return isTranslationFile(file.getVirtualFile());
    }

    public static boolean isTranslationFile(VirtualFile virtualFile) {
        return virtualFile.getParent().getName().equals("locales");
    }

    public static boolean isTranslationCall(FunctionReference call) {
        return call.getParameters().length > 0 && (call.getName().equals("t") || call.getText().startsWith("I18n::labels"));
    }

    public static PsiElement extractKey(FunctionReference translationCall) {
        PsiElement parameter = translationCall.getParameters()[0];

        if (PlatformPatterns.psiElement(PhpElementTypes.CONCATENATION_EXPRESSION).accepts(parameter)) {
            BinaryExpression binaryExpression = (BinaryExpression) parameter;
            parameter = binaryExpression.getLeftOperand();
        }
        return parameter;
    }
}
