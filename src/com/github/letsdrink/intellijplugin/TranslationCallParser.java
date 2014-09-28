package com.github.letsdrink.intellijplugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.FunctionReference;

import java.util.Collection;

public class TranslationCallParser {
    public interface TranslationCallHandler {
        void handleKey(String key, PsiElement keyElement);
    }

    public void parse(PsiFile psiFile, TranslationCallHandler handler) {
        Collection<FunctionReference> calls = PsiTreeUtil.collectElementsOfType(psiFile, FunctionReference.class);
        for (FunctionReference call : calls) {
            if (TranslationUtils.isTranslationCall(call)) {
                PsiElement psiElement = TranslationUtils.extractKey(call);
                String key = PsiUtils.getContent(psiElement);
                if (key != null) {
                    handler.handleKey(key, psiElement);
                }
            }
        }
    }
}
