package com.github.letsdrink.intellijplugin;


import com.google.common.collect.Iterables;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;

public class GoToViewAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(LangDataKeys.PSI_FILE);
        PsiElement psiElement = PsiUtils.getCurrentElement(editor, psiFile);

        List<PsiFile> viewPsiFiles = OuzoUtils.getViewPsiFiles(psiElement);
        if (!viewPsiFiles.isEmpty()) {
            FileEditorManager editorManager = FileEditorManager.getInstance(editor.getProject());
            editorManager.openFile(Iterables.getFirst(viewPsiFiles, null).getVirtualFile(), true);
        }
    }
}
