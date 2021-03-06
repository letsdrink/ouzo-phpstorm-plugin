package com.github.letsdrink.intellijplugin;


import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class PartialReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(StringLiteralExpression.class), new BaseViewReferenceProvider() {
            @Override
            protected boolean isApplicable(PsiElement psiElement) {
                return PsiUtils.isElementTheFirstParameterInFunctionCall(psiElement, "renderPartial");
            }
        });
    }
}
