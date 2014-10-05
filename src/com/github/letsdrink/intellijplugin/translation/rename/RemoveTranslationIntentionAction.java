package com.github.letsdrink.intellijplugin.translation.rename;


import com.github.letsdrink.intellijplugin.PsiUtils;
import com.github.letsdrink.intellijplugin.translation.TranslationFileFacade;
import com.github.letsdrink.intellijplugin.translation.TranslationHashElement;
import com.github.letsdrink.intellijplugin.translation.TranslationRemoveUtils;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RemoveTranslationIntentionAction extends PsiElementBaseIntentionAction {

    @NotNull
    @Override
    public String getFamilyName() {
        return "Ouzo";
    }

    @Override
    public void invoke(@NotNull Project project, final Editor editor, @NotNull final PsiElement psiElement) throws IncorrectOperationException {
        final String key = TranslationHashElement.newInstance(psiElement).getFullKey();

        final List<PsiElement> elementsToRemove = TranslationRemoveUtils.getElementToRemove(project, key);

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        new WriteCommandAction(project) {
            @Override
            protected void run(Result result) throws Throwable {
                for (PsiElement element : elementsToRemove) {
                    PsiUtils.deleteArrayElement(element);
                }
            }

            @Override
            public String getGroupID() {
                return "Remove Translation";
            }
        }.execute();
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove translations";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return true;
    }

}
