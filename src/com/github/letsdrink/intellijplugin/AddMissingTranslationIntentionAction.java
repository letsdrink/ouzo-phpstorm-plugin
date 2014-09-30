package com.github.letsdrink.intellijplugin;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class AddMissingTranslationIntentionAction extends BaseIntentionAction {
    @NotNull
    @Override
    public String getFamilyName() {
        return "Ouzo";
    }

    @NotNull
    @Override
    public String getText() {
        return "Add missing translations";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        PsiElement psiElement = PsiUtils.getCurrentElement(editor, psiFile);
        String key = PsiUtils.getContent(psiElement.getParent());

        List<PsiFile> translationFiles = TranslationUtils.getTranslationFiles(project);
        final List<TranslationFileFacade> translationFileFacades = Lists.transform(translationFiles, TranslationFileFacade.createParser());

        TranslationDialog dialog = new TranslationDialog(asList(key), getTranslations(translationFileFacades), new TranslationDialog.OkCallback() {
            @Override
            public void onClick(final String key, Map<String, String> translations) {
                for (Map.Entry<String, String> entry : translations.entrySet()) {
                    TranslationFileFacade parser = Iterables.find(translationFileFacades, TranslationFileFacade.languageEqualsFunction(entry.getKey()));
                    parser.addTranslation(key, entry.getValue());
                }
            }
        });
        dialog.showDialog();
    }

    private HashMap<String, String> getTranslations(List<TranslationFileFacade> translationFileFacades) {
        HashMap<String, String> translationsMap = new HashMap<String, String>();

        for (TranslationFileFacade translationFileFacade : translationFileFacades) {
            translationsMap.put(translationFileFacade.getLanguage(), "");
        }
        return translationsMap;
    }
}
