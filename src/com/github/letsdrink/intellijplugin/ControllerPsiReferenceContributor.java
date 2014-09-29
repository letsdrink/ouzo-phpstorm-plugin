package com.github.letsdrink.intellijplugin;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

public class ControllerPsiReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(StringLiteralExpression.class), new ControllerActionReferenceProvider());
    }

}