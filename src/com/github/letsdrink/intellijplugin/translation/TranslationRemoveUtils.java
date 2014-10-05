package com.github.letsdrink.intellijplugin.translation;


import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;

import java.util.ArrayList;
import java.util.List;

public class TranslationRemoveUtils {
    public static List<PsiElement> getElementToRemove(Project project, String key) {
        List<PsiFile> translationFiles = TranslationUtils.getTranslationFiles(project);
        List<TranslationFileFacade> translationFileFacades = Lists.transform(translationFiles, TranslationFileFacade.createParser());
        return getElementToRemove(key, translationFileFacades);
    }

    public static List<PsiElement> getElementToRemove(String key, List<TranslationFileFacade> translationFileFacades) {
        final List<PsiElement> elementsToRemove = new ArrayList<PsiElement>();

        for (TranslationFileFacade translationFileFacade : translationFileFacades) {
            ArrayHashElement element = translationFileFacade.getTranslationElement(key);
            PsiElement elementToRemove = findElementToRemove(element);
            if (elementToRemove != null) {
                elementsToRemove.add(elementToRemove);
            }
        }
        return elementsToRemove;
    }

    public static PsiElement findElementToRemove(PsiElement element) {
        if (element != null && element.getParent().getChildren().length == 1 && element.getParent().getParent().getParent() instanceof ArrayHashElement) {
            return findElementToRemove(element.getParent().getParent().getParent());
        } else {
            return element;
        }
    }
}
