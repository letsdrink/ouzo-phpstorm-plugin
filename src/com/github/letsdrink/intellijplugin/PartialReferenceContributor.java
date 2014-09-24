package com.github.letsdrink.intellijplugin;


import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class PartialReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(StringLiteralExpression.class), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
                if (!PsiUtils.isElementTheFirstParameterInFunctionCall(psiElement, "renderPartial")) {
                    return new PsiReference[0];
                }
                String partialName = PsiUtils.getContent(psiElement);
                PsiFile partialPsiFile = OuzoUtils.getPartialPsiFile(psiElement.getContainingFile(), partialName);

                if (partialPsiFile == null) {
                    return new PsiReference[0];
                }

                PsiReferenceBase.Immediate reference = new PsiReferenceBase.Immediate(psiElement, partialPsiFile);
                return new PsiReference[]{reference};
            }
        });
    }
}
