package com.github.letsdrink.intellijplugin;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;

import javax.annotation.Nullable;

public class PsiFunctions {
    public static Predicate<MethodReference> isCallTo(final String methodFQN) {
        return new Predicate<MethodReference>() {
            @Override
            public boolean apply(@Nullable MethodReference methodReference) {
                return PsiUtils.isMethodCallTo(methodReference, methodFQN);
            }
        };
    }

    public static Function<MethodReference, String> extractFirstArgumentStringContent() {
        return new Function<MethodReference, String>() {
            @Nullable
            @Override
            public String apply(@Nullable MethodReference methodReference) {
                PsiElement[] parameters = methodReference.getParameters();
                return parameters.length > 0 ? PsiUtils.getContent(parameters[0]) : null;
            }
        };
    }
}
