package com.github.letsdrink.intellijplugin;


import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;

public class InvalidTranslationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        PsiFile psiFile = psiElement.getContainingFile();
        if (!TranslationUtils.isTranslationFile(psiFile)) {
            return;
        }
        if (PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).accepts(psiElement) && !(psiElement.getParent() instanceof ArrayHashElement)) {
            annotationHolder.createErrorAnnotation(psiElement, "Invalid Translation");
        }
    }
}
