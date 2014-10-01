package com.github.letsdrink.intellijplugin;


import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class EditTranslationIntentionAction extends TranslationIntentionAction {

    @NotNull
    @Override
    public String getText() {
        return "Edit translations";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        return PsiUtils.isElementTheFirstParameterInFunctionCall(psiElement.getParent(), "t");
    }

}
