package com.github.letsdrink.intellijplugin;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class PhpGoToRelatedProvider extends GotoRelatedProvider {
    @NotNull
    @Override
    public List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        List<PsiFile> viewPsiFiles = OuzoUtils.getViewPsiFiles(psiElement);

        return Lists.transform(viewPsiFiles, new Function<PsiFile, GotoRelatedItem>() {
            @Nullable
            @Override
            public GotoRelatedItem apply(@Nullable PsiFile psiFile) {
                return new GotoRelatedItem(psiFile);
            }
        });
    }
}
