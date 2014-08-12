package com.github.letsdrink.intellijplugin;

import com.intellij.patterns.ObjectPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class ModelFieldReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar psiReferenceRegistrar) {
        ObjectPattern.Capture<PsiElement> pattern = PlatformPatterns.instanceOf(PsiElement.class);
        psiReferenceRegistrar.registerReferenceProvider(pattern, new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
                return new PsiReference[0];
            }
        });
    }
}
