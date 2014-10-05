package com.github.letsdrink.intellijplugin.rename;


import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageInfo;
import org.jetbrains.annotations.NotNull;

public abstract class RenameTranslationUsageInfo extends UsageInfo {
    public RenameTranslationUsageInfo(@NotNull PsiElement element) {
        super(element);
    }
    abstract void performRefactoring(String newKey);

    public abstract void performPsiSpoilingRefactoring(String newKey);
}
