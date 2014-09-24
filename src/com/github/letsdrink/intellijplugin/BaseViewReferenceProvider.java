package com.github.letsdrink.intellijplugin;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public abstract class BaseViewReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (!isApplicable(psiElement)) {
            return new PsiReference[0];
        }
        String viewName = PsiUtils.getContent(psiElement);
        PsiFile viewPsiFile = OuzoUtils.getViewPsiFile(psiElement.getProject(), viewName);

        if (viewPsiFile == null) {
            return new PsiReference[0];
        }

        PsiReferenceBase.Immediate reference = new PsiReferenceBase.Immediate(psiElement, viewPsiFile);
        return new PsiReference[]{reference};
    }

    protected abstract boolean isApplicable(PsiElement psiElement);
}
