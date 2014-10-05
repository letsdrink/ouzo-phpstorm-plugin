package com.github.letsdrink.intellijplugin.rename;

import com.github.letsdrink.intellijplugin.TranslationUtils;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenameHandler;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class TranslationKeyRenameHandler implements RenameHandler {
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

        if (!TranslationUtils.isTranslationFile(file)) {
            return false;
        }

        final ArrayHashElement element = getElement(file, editor);

        return element.getValue() instanceof StringLiteralExpression;
    }

    @Nullable
    private static ArrayHashElement getElement(PsiFile file, Editor editor) {
        PsiElement element = file.findElementAt(editor.getCaretModel().getCurrentCaret().getOffset());
        if (element instanceof ArrayHashElement) {
            return (ArrayHashElement) element;
        }
        return PsiTreeUtil.getParentOfType(element, ArrayHashElement.class);
    }


    @Override
    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        ArrayHashElement element = getElement(psiFile, editor);
        if (element == null) {
            return;
        }

        RenameDialog.showRenameDialog(dataContext, new RenameTranslationKeyDialog(project, element, null, editor));
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {
        throw new UnsupportedOperationException();
    }
}
