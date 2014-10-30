package com.github.letsdrink.intellijplugin;

import com.google.common.collect.Iterables;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


class RelationClassReferenceProvider extends PsiReferenceProvider {
    private boolean isApplicable(PsiElement psiElement) {
        PhpClass containingClass = PsiTreeUtil.getParentOfType(psiElement, PhpClass.class);
        if (containingClass == null) {
            return false;
        }
        if (!PsiUtils.isInstanceOf(containingClass, OuzoUtils.OUZO_MODEL_FQN)) {
            return false;
        }

        return isArrayValue(psiElement.getParent()) && psiElement.getParent().getParent() instanceof ArrayHashElement && "class".equals(getArrayKey(psiElement));
    }

    private boolean isArrayValue(PsiElement psiElement) {
        return PlatformPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).accepts(psiElement);
    }

    private String getArrayKey(PsiElement psiElement) {
        return PsiUtils.getContent(((ArrayHashElement) psiElement.getParent().getParent()).getKey());
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (!isApplicable(psiElement)) {
            return new PsiReference[]{};
        }

        PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());
        Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN(OuzoUtils.OUZO_MODEL_NAMESPACE + PsiUtils.getContent(psiElement));
        PhpClass phpClass = Iterables.getLast(phpClasses, null);

        if (phpClass == null) {
            return new PsiReference[]{};
        }

        PsiReferenceBase.Immediate reference = new PsiReferenceBase.Immediate(psiElement, phpClass);
        return new PsiReference[]{reference};
    }

}
