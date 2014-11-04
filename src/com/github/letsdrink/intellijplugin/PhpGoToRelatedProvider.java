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

import static com.google.common.collect.Lists.newArrayList;

public class PhpGoToRelatedProvider extends GotoRelatedProvider {
    @NotNull
    @Override
    public List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        List<PsiElement> relatedItems = newArrayList();

        relatedItems.addAll(OuzoUtils.getViewPsiFiles(psiElement));

        PsiFile psiFile = psiElement.getContainingFile();
        if (OuzoUtils.isInViewDir(psiFile)) {
            PsiElement element = getControllerOrActionElement(psiFile);
            if (element != null) {
                relatedItems.add(element);
            }
        }

        return Lists.transform(relatedItems, new Function<PsiElement, GotoRelatedItem>() {
            @Nullable
            @Override
            public GotoRelatedItem apply(@Nullable PsiElement element) {
                return new GotoRelatedItem(element);
            }
        });
    }

    private PsiElement getControllerOrActionElement(PsiFile psiFile) {
        String resourceName = psiFile.getParent().getName();

        String controller = "Controller\\" + resourceName + "Controller";
        String action = psiFile.getName().replaceAll("\\.phtml", "");
        return OuzoUtils.getControllerAction(psiFile.getProject(), controller, action);
    }
}
