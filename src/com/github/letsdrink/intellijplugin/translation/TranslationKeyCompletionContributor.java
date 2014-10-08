package com.github.letsdrink.intellijplugin.translation;


import com.github.letsdrink.intellijplugin.PsiUtils;
import com.github.letsdrink.intellijplugin.translation.index.TranslationKeyIndex;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class TranslationKeyCompletionContributor extends CompletionContributor {
    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition().getParent();

        if (PsiUtils.isElementTheFirstParameterInFunctionCall(element, "t")) {
            String prefix = result.getPrefixMatcher().getPrefix();
            final FileBasedIndex index = FileBasedIndex.getInstance();
            Collection<String> allKeys = index.getAllKeys(TranslationKeyIndex.KEY, element.getProject());
            addAllMatching(allKeys, prefix, result);
        }
    }

    private void addAllMatching(Collection<String> allKeys, String prefix, CompletionResultSet result) {
        for (String key : allKeys) {
            if (key.startsWith(prefix)) {
                result.addElement(LookupElementBuilder.create(key));
            }
        }
    }
}
