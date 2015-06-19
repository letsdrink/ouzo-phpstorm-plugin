package com.github.letsdrink.intellijplugin.model;


import com.github.letsdrink.intellijplugin.OuzoUtils;
import com.github.letsdrink.intellijplugin.PsiUtils;
import com.google.common.collect.ImmutableSet;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocPropertyTag;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ModelAttributesCompletionContributor extends CompletionContributor {
    private final static ImmutableSet<String> SUPPORTED_METHODS = ImmutableSet.of("create", "createNoValidation", "newInstance", "assignAttributes", "updateAttributes");
    private final static ImmutableSet<String> SUPPORTED_TYPES = ImmutableSet.of("int", "integer", "string", "bool", "mixed", "boolean", "float");

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
            if (SUPPORTED_METHODS.contains(method.getName())) {
                PhpExpression classReference = method.getClassReference();
                if (classReference instanceof ClassReference) {
                    String fqn = ((ClassReference) classReference).getFQN();
                    addCompletion(result, element, fqn);
                } else if (classReference instanceof Variable) {
                    Variable variable = (Variable) classReference;
                    PhpType inferredType = variable.getInferredType(1);
                    Set<String> types = inferredType.getTypes();
                    for (String type : types) {
                        addCompletion(result, element, type);
                    }
                }
            }
        } else if (context instanceof NewExpression) {
            NewExpression newExpression = (NewExpression) context;
            ClassReference classReference = newExpression.getClassReference();
            String fqn = classReference.getFQN();
            addCompletion(result, element, fqn);
        }
    }

    private void addCompletion(CompletionResultSet result, PsiElement element, String fqn) {
        ArrayCreationExpression arrayCreation = PsiTreeUtil.getParentOfType(element, ArrayCreationExpression.class);
        if (arrayCreation != null && canBecomeKey(element)) {
            Set<String> existingKeys = PsiUtils.getKeys(arrayCreation);
            PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());
            Collection<PhpClass> classesByFQN = phpIndex.getClassesByFQN(fqn);
            for (PhpClass phpClass : classesByFQN) {
                if (PsiUtils.isInstanceOf(phpClass, OuzoUtils.OUZO_MODEL_FQN)) {
                    addCompletionForModelClass(phpClass, existingKeys, result);
                }
            }
        }
    }

    private void addCompletionForModelClass(PhpClass phpClass, Set<String> existingKeys, CompletionResultSet result) {
        PhpDocComment docComment = phpClass.getDocComment();
        if (docComment != null) {
            List<PhpDocPropertyTag> propertyTags = docComment.getPropertyTags();
            for (PhpDocPropertyTag propertyTag : propertyTags) {
                PhpDocProperty property = propertyTag.getProperty();
                if (property != null) {
                    String name = property.getName();
                    if (!existingKeys.contains(name) && SUPPORTED_TYPES.contains(property.getType().toString())) {
                        result.addElement(LookupElementBuilder.create(name));
                    }
                }
            }
        }
    }

    private boolean canBecomeKey(PsiElement element) {
        return (PsiTreeUtil.getParentOfType(element, ArrayHashElement.class) == null || PlatformPatterns.psiElement(PhpElementTypes.ARRAY_KEY).accepts(element.getParent()));
    }
}
