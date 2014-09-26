package com.github.letsdrink.intellijplugin;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ControllerPsiReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(StringLiteralExpression.class), new ControllerActionReferenceProvider() {
            @Override
            protected boolean isApplicable(PsiElement psiElement) {
                if (!OuzoUtils.isExpectedFile(psiElement, "routes.php")) {
                    return false;
                }

                return PsiUtils.isElementTheFirstParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_RESOURCE_FQN) ||
                        PsiUtils.isElementTheNthParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_GET_FQN, 1) ||
                        PsiUtils.isElementTheNthParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_POST_FQN, 1) ||
                        PsiUtils.isElementTheNthParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_DELETE_FQN, 1) ||
                        PsiUtils.isElementTheNthParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_PUT_FQN, 1);
            }
        });
    }
}