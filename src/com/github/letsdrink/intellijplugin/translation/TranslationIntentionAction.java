package com.github.letsdrink.intellijplugin.translation;

import com.github.letsdrink.intellijplugin.PsiUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;


public abstract class TranslationIntentionAction extends PsiElementBaseIntentionAction {
    @NotNull
    @Override
    public String getFamilyName() {
        return "Ouzo";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        String key = PsiUtils.getContent(psiElement.getParent());

        List<PsiFile> translationFiles = TranslationUtils.getTranslationFiles(project);
        final List<TranslationFileFacade> translationFileFacades = Lists.transform(translationFiles, TranslationFileFacade.createParser());

        TranslationDialog dialog = new TranslationDialog(new TranslationModel(translationFileFacades, asList(key), ""), new TranslationDialog.OkCallback() {
            @Override
            public void onClick(final String key, Map<String, String> translations) {
                for (Map.Entry<String, String> entry : translations.entrySet()) {
                    TranslationFileFacade parser = Iterables.find(translationFileFacades, TranslationFileFacade.languageEqualsFunction(entry.getKey()));
                    parser.addTranslationInWriteCommand(key, entry.getValue());
                }
            }
        });
        dialog.disableKey();
        dialog.showDialog();
    }
}
