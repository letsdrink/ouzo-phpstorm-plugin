package com.github.letsdrink.intellijplugin.translation.usages;


import com.github.letsdrink.intellijplugin.PsiUtils;
import com.github.letsdrink.intellijplugin.translation.TranslationHashElement;
import com.github.letsdrink.intellijplugin.translation.TranslationUtils;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TranslationFindUsagesHandlerFactory extends FindUsagesHandlerFactory {
    @Override
    public boolean canFindUsages(@NotNull PsiElement psiElement) {
        String translationKey = getTranslationKey(psiElement);
        return translationKey != null;
    }

    @Nullable
    @Override
    public FindUsagesHandler createFindUsagesHandler(@NotNull final PsiElement psiElement, boolean forHighlightUsages) {
        return new FindUsagesHandler(psiElement) {
            @NotNull
            @Override
            public PsiElement[] getSecondaryElements() {
                TranslationHashElement translationHashElement = TranslationHashElement.newInstance(psiElement);
                List<ArrayHashElement> children = translationHashElement.getChildrenHashElements();
                return children.toArray(PsiElement.EMPTY_ARRAY);
            }

            @Override
            public boolean processElementUsages(@NotNull final PsiElement psiElement, @NotNull Processor<UsageInfo> usageInfoProcessor, @NotNull FindUsagesOptions findUsagesOptions) {
                UsageInfo[] usages = ApplicationManager.getApplication().runReadAction(new Computable<UsageInfo[]>() {
                    @Override
                    public UsageInfo[] compute() {
                        String key = TranslationHashElement.newInstance(psiElement).getFullKey();
                        TranslationUsagesFinder usagesFinder = new TranslationUsagesFinder(key);
                        return usagesFinder.findUsages(psiElement.getProject());
                    }
                });

                for (UsageInfo usageInfo : usages) {
                    usageInfoProcessor.process(usageInfo);
                }
                return true;
            }
        };
    }

    private String getTranslationKey(PsiElement psiElement) {
        PsiFile psiFile = psiElement.getContainingFile();
        if (!TranslationUtils.isTranslationFile(psiFile)) {
            return null;
        }
        ArrayHashElement hashElement = PsiTreeUtil.getParentOfType(psiElement, ArrayHashElement.class);
        if (hashElement == null) {
            return null;
        }
        return PsiUtils.getContent(hashElement.getKey());
    }
}
