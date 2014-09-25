package com.github.letsdrink.intellijplugin;


import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ViewReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(StringLiteralExpression.class), new BaseViewReferenceProvider() {
            @Override
            protected boolean isApplicable(PsiElement psiElement) {
                Method renderMethod = OuzoUtils.getOuzoViewRenderMethod(psiElement.getProject());
                return PsiUtils.isElementTheFirstParameterInMethodCall(psiElement, renderMethod);
            }
        });
    }
}
