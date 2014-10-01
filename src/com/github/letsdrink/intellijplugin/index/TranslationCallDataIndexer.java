package com.github.letsdrink.intellijplugin.index;

import com.github.letsdrink.intellijplugin.TranslationCallParser;
import com.github.letsdrink.intellijplugin.TranslationUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


class TranslationCallDataIndexer implements DataIndexer<String, Void, FileContent> {
    @NotNull
    @Override
    public Map<String, Void> map(@NotNull FileContent fileContent) {
        final Map<String, Void> map = new HashMap<String, Void>();

        PsiFile psiFile = fileContent.getPsiFile();
        TranslationCallParser translationCallParser = new TranslationCallParser();
        translationCallParser.parse(psiFile, new TranslationCallParser.TranslationCallHandler() {
            @Override
            public void handleKey(String key, PsiElement keyElement) {
                map.put(key, null);
            }
        });
        return map;
    }

}
