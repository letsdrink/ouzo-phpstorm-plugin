package com.github.letsdrink.intellijplugin;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

class TranslationReferenceProvider extends PsiReferenceProvider {
    private static final Logger log = Logger.getInstance(TranslationReferenceProvider.class);

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (!(psiElement.getContext() instanceof ParameterList)) {
            return new PsiReference[0];
        }

        ParameterList parameterList = (ParameterList) psiElement.getContext();

        if (parameterList == null || !(parameterList.getContext() instanceof FunctionReference)) {
            return new PsiReference[0];
        }

        FunctionReference function = (FunctionReference) parameterList.getContext();

        if (!isTranslatorCall(function) || !(function.getParameters()[0] instanceof StringLiteralExpression)) {
            return new PsiReference[0];
        }
        String key = ((StringLiteralExpression) function.getParameters()[0]).getContents();

        return FluentIterable.from(TranslationUtils.getTranslationFiles(psiElement.getProject()))
                .transform(TranslationParser.createParser())
                .transform(TranslationParser.getTranslationElementFunction(key))
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

    public boolean isTranslatorCall(PsiElement e) {
        if (!(e instanceof FunctionReference)) {
            return false;
        }
        FunctionReference ref = (FunctionReference) e;
        return "t".equals(ref.getName());
    }
}