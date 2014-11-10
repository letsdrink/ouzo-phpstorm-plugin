package com.github.letsdrink.intellijplugin;

import com.google.common.collect.Iterables;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.jetbrains.php.lang.PhpFileType;

import java.util.List;


public class ExtractPartialAction extends AnAction {
    public void actionPerformed(AnActionEvent event) {

        final Editor editor = event.getData(PlatformDataKeys.EDITOR);
        final PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);

        int startOffset = editor.getSelectionModel().getSelectionStart();
        int endOffset = editor.getSelectionModel().getSelectionEnd();

        final List<PsiElement> selectedElements = PsiUtils.elementsInRange(psiFile, startOffset, endOffset);

        final PartialNameDialog dialog = new PartialNameDialog(psiFile.getProject());
        dialog.show();

        if (!dialog.isOK()) {
            return;
        }
        new WriteCommandAction(psiFile.getProject()) {
            @Override
            protected void run(Result result) throws Throwable {
                extract(dialog.getName(), selectedElements, editor, psiFile);
            }

            @Override
            public String getGroupID() {
                return "Partial Extraction";
            }
        }.execute();

    }

    private void extract(String name, List<PsiElement> selectedElements, Editor editor, PsiFile psiFile) {
        TextRange extractedRange = new TextRange(Iterables.getFirst(selectedElements, null).getTextRange().getStartOffset(), Iterables.getLast(selectedElements, null).getTextRange().getEndOffset());
        Document document = editor.getDocument();
        String code = document.getText(extractedRange);

        PsiDirectory directory = psiFile.getContainingDirectory();

        PsiFileFactory factory = PsiFileFactory.getInstance(psiFile.getProject());
        PsiFile newFile = factory.createFileFromText(name + ".phtml", PhpFileType.INSTANCE, code);
        PsiElement addedFile = directory.add(newFile);
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiFile.getManager().getProject());
        codeStyleManager.reformat(addedFile);

        String replacement = "<?= renderPartial('" + directory.getName() + "/" + name + "'); ?>";
        document.replaceString(extractedRange.getStartOffset(), extractedRange.getEndOffset(), replacement);
    }

}
