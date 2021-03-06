package com.github.letsdrink.intellijplugin.translation;

import com.github.letsdrink.intellijplugin.PsiUtils;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;

class TranslationReferenceProvider extends PsiReferenceProvider {
    private static final Logger log = Logger.getInstance(TranslationReferenceProvider.class);

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (!PsiUtils.isElementTheFirstParameterInFunctionCall(psiElement, "t")) {
            return new PsiReference[0];
        }
        String key = PsiUtils.getContent(psiElement);
        if (key == null) {
            return new PsiReference[0];
        }
        return FluentIterable.from(TranslationUtils.getTranslationFiles(psiElement.getProject()))
                .transform(TranslationFileFacade.createParser())
                .transform(TranslationFileFacade.getTranslationElementFunction(key))
                .filter(Predicates.notNull())
                .transform(createReference(psiElement))
                .toArray(TranslationReference.class);
    }

    private Function<ArrayHashElement, TranslationReference> createReference(final PsiElement psiElement) {
        return new Function<ArrayHashElement, TranslationReference>() {
            @javax.annotation.Nullable
            @Override
            public TranslationReference apply(@javax.annotation.Nullable ArrayHashElement element) {
                return new TranslationReference(psiElement, element.getValue(), PsiUtils.getContent(element.getValue()));
            }
        };
    }
}