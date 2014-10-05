package com.github.letsdrink.intellijplugin.translation;


import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class AddMissingTranslationIntentionAction extends TranslationIntentionAction {

    @NotNull
    @Override
    public String getText() {
        return "Add missing translations";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return true;
    }

}
