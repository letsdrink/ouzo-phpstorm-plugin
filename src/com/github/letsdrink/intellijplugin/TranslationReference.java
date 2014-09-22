package com.github.letsdrink.intellijplugin;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TranslationReference extends PsiReferenceBase<PsiElement> {
    private final PsiElement target;
    private final String translation;

    public TranslationReference(PsiElement psiElement, PsiElement target, String translation) {
        super(psiElement);
        this.target = target;
        this.translation = translation;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return target;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new LookupElement[]{LookupElementBuilder.create(translation).withTypeText("Translation")};
    }
}