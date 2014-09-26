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
                if (!OuzoUtils.isExpectedFile(psiElement, "routes.php")) {
                    return false;
                }

                Method resourceMethod = OuzoUtils.getRouteResourceMethod(psiElement.getProject());
                Method getMethod = OuzoUtils.getRouteGetMethod(psiElement.getProject());
                Method postMethod = OuzoUtils.getRoutePostMethod(psiElement.getProject());
                Method deleteMethod = OuzoUtils.getRouteDeleteMethod(psiElement.getProject());
                Method putMethod = OuzoUtils.getRoutePutMethod(psiElement.getProject());

                return PsiUtils.isElementTheFirstParameterInMethodCall(psiElement, resourceMethod) ||
                        PsiUtils.isElementTheNthParameterInMethodCall(psiElement, getMethod, 1) ||
                        PsiUtils.isElementTheNthParameterInMethodCall(psiElement, postMethod, 1) ||
                        PsiUtils.isElementTheNthParameterInMethodCall(psiElement, deleteMethod, 1) ||
                        PsiUtils.isElementTheNthParameterInMethodCall(psiElement, putMethod, 1);
            }
        });
    }
}