package com.github.letsdrink.intellijplugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

class ControllerActionReferenceProvider extends PsiReferenceProvider {
    private static final Logger log = Logger.getInstance(ControllerActionReferenceProvider.class);

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        return new PsiReference[]{new ControllerActionReference(psiElement)};
    }
}