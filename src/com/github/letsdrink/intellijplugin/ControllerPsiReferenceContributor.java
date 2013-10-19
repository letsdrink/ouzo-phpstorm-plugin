package com.github.letsdrink.intellijplugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;

public class ControllerPsiReferenceContributor extends PsiReferenceContributor {
    private static final Logger log = Logger.getInstance(ControllerPsiReferenceContributor.class);

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar psiReferenceRegistrar) {
        PsiElementPattern.Capture<PsiElement> pattern = PlatformPatterns.psiElement().withText(StandardPatterns.string().contains("#"));
        psiReferenceRegistrar.registerReferenceProvider(pattern, new ControllerActionReferenceProvider());
    }

}
