package com.github.letsdrink.intellijplugin;


import com.intellij.psi.PsiElement;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProviderEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TranslationUsageTypeProvider implements UsageTypeProviderEx {
    @Nullable
    @Override
    public UsageType getUsageType(PsiElement psiElement, @NotNull UsageTarget[] usageTargets) {
        return null;
    }

    @Nullable
    @Override
    public UsageType getUsageType(PsiElement psiElement) {
        return null;
    }
}
