package com.github.letsdrink.intellijplugin.model;


import com.github.letsdrink.intellijplugin.OuzoUtils;
import com.github.letsdrink.intellijplugin.PsiUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocPropertyTag;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class ModelAttributesCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition().getParent();

        ParameterList parameterList = PsiTreeUtil.getParentOfType(element, ParameterList.class);
        if (parameterList == null) {
            return;
        }
        PsiElement context = parameterList.getContext();
        if (context instanceof MethodReference) {
            MethodReference method = (MethodReference) context;
            //check if method is one of create ..., filter out properties other than string, int, mixed and bool.
            String fqn = ((ClassReference) method.getClassReference()).getFQN();
            PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());
            Collection<PhpClass> classesByFQN = phpIndex.getClassesByFQN(fqn);
            for (PhpClass phpClass : classesByFQN) {
                if (PsiUtils.isInstanceOf(phpClass, OuzoUtils.OUZO_MODEL_FQN)) {
                    List<PhpDocPropertyTag> propertyTags = phpClass.getDocComment().getPropertyTags();
                    for (PhpDocPropertyTag propertyTag : propertyTags) {
                        result.addElement(LookupElementBuilder.create(propertyTag.getProperty().getName()));
                    }
                }
            }

            //   addCompletionForFunctionOptions(method, element, givenParameters, result);
        } else if (context instanceof NewExpression) {
            NewExpression newExpression = (NewExpression) context;
         //   addCompletionForConstructorOptions(newExpression, element, givenParameters, result);
        }
    }

    private void addCompletionForConstructorOptions(NewExpression newExpression, PsiElement element, PsiElement[] givenParameters, CompletionResultSet result) {
        ClassReference classReference = newExpression.getClassReference();
        PsiElement resolvedClass = classReference.resolve();
        Method method = (Method) resolvedClass;

        PhpDocComment docComment = method.getDocComment();

        addCompletionForOptions(result, element);
    }

    private void addCompletionForFunctionOptions(FunctionReference function, PsiElement element, PsiElement[] givenParameters, CompletionResultSet result) {
        PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());
        Collection<? extends PhpNamedElement> bySignature = phpIndex.getBySignature(function.getSignature());
        if (!bySignature.isEmpty()) {
            PhpNamedElement first = bySignature.iterator().next();
            PhpDocComment docComment = first.getDocComment();
            if (docComment != null) {
                addCompletionForOptions(result, element);
            }
        }
    }

    private void addCompletionForOptions(CompletionResultSet result, PsiElement element) {
        ArrayCreationExpression arrayCreation = PsiTreeUtil.getParentOfType(element, ArrayCreationExpression.class);
        if (arrayCreation != null && canBecomeKey(element)) {

        }
    }

    private Set<String> getAllKeys(ArrayCreationExpression arrayCreation) {
        Set<String> keys = newHashSet();
        ArrayHashElement[] hashElements = PsiTreeUtil.getChildrenOfType(arrayCreation, ArrayHashElement.class);
        if (hashElements == null) {
            return keys;
        }
        for (ArrayHashElement hashElement : hashElements) {
            keys.add(PsiUtils.getContent(hashElement.getKey()));
        }
        return keys;
    }

    private boolean canBecomeKey(PsiElement element) {
        return (PsiTreeUtil.getParentOfType(element, ArrayHashElement.class) == null || PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).accepts(element.getParent()));
    }
}
