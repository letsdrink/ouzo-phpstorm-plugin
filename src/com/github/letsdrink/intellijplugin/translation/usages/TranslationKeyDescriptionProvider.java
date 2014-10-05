package com.github.letsdrink.intellijplugin.translation.usages;

import com.github.letsdrink.intellijplugin.translation.TranslationHashElement;
import com.github.letsdrink.intellijplugin.translation.TranslationUtils;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TranslationKeyDescriptionProvider implements ElementDescriptionProvider {
    @Nullable
    @Override
    public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
        if (!TranslationUtils.isTranslationFile(element.getContainingFile())) {
            return null;
        }

        if (location instanceof UsageViewTypeLocation) {
            return "Translation key";
        }

        if (location instanceof UsageViewNodeTextLocation || location instanceof UsageViewShortNameLocation || location instanceof UsageViewLongNameLocation) {
            return TranslationHashElement.newInstance(element).getFullKey();
        }

        return null;
    }
}
