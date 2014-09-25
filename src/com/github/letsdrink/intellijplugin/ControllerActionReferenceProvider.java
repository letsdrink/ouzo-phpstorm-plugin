package com.github.letsdrink.intellijplugin;

import com.google.common.collect.Iterables;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class ControllerActionReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (!isApplicable(psiElement)) {
            return new PsiReference[]{};
        }

        String controller = sanitizeController(PsiUtils.getContent(psiElement));

        PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());
        Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN(controller);

        PhpClass phpClass = Iterables.getLast(phpClasses, null);

        PsiReferenceBase.Immediate reference = new PsiReferenceBase.Immediate(psiElement, phpClass);
        return new PsiReference[]{reference};
    }

    private String sanitizeController(String controller) {
        return "Controller\\" + prepareNamespace(underscoreToCamelCase(controller)) + "Controller";
    }

    private String underscoreToCamelCase(String text) {
        return StringUtils.capitalize(WordUtils.capitalizeFully(text, new char[]{'_'}).replaceAll("_", ""));
    }

    private String prepareNamespace(String s) {
        return s.replace('/', '\\');
    }

    protected abstract boolean isApplicable(PsiElement psiElement);
}