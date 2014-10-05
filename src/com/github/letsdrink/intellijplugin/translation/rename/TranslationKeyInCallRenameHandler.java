package com.github.letsdrink.intellijplugin.translation.rename;

import com.github.letsdrink.intellijplugin.PsiUtils;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenameHandler;
import org.jetbrains.annotations.NotNull;


public class TranslationKeyInCallRenameHandler implements RenameHandler {
    @Override
    public boolean isAvailableOnDataContext(DataContext dataContext) {
        final Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        if (editor == null) {
            return false;
        }

        final PsiFile file = CommonDataKeys.PSI_FILE.getData(dataContext);
        if (file == null) {
            return false;
        }
        PsiElement element = getPsiElement(editor, file);

        return PsiUtils.isElementTheFirstParameterInFunctionCall(element, "t");
    }

    private PsiElement getPsiElement(Editor editor, PsiFile file) {
        PsiElement element = file.findElementAt(editor.getCaretModel().getCurrentCaret().getOffset());
        return element.getParent();
    }

    @Override
    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        final PsiElement element = getPsiElement(editor, psiFile);
        RenameDialog.showRenameDialog(dataContext, new RenameTranslationKeyDialog(project, element, null, editor) {
            @Override
            protected String getKey() {
                return PsiUtils.getContent(element);
            }
        });
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {
        throw new UnsupportedOperationException();
    }
}
