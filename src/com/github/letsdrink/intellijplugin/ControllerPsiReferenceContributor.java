package com.github.letsdrink.intellijplugin;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ControllerPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(StringLiteralExpression.class), new ControllerActionReferenceProvider() {
            @Override
            protected boolean isApplicable(PsiElement psiElement) {
                Method renderMethod = OuzoUtils.getRouteResourceMethod(psiElement.getProject());
                return PsiUtils.isElementTheFirstParameterInMethodCall(psiElement, renderMethod);
            }
        });
    }
}