package com.github.letsdrink.intellijplugin.translation;


import com.github.letsdrink.intellijplugin.PsiUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpReturn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TranslationParser {
    public interface Handler {
        void handle(String key, String text, ArrayHashElement element);
    }

    public void parse(PsiFile file, Handler handler) {
        Collection<PhpReturn> phpReturns = PsiTreeUtil.collectElementsOfType(file, PhpReturn.class);
        PhpReturn phpReturn = Iterables.getOnlyElement(phpReturns);
        PsiElement root = phpReturn.getFirstPsiChild();

        parse(root, handler);
    }

    public void parse(PsiElement root, Handler handler) {
        List<String> keyParts = new ArrayList<>();
        parse(root, keyParts, handler);
    }

    private void parse(PsiElement root, List<String> keyParts, Handler handler) {
        for (PsiElement psiElement : root.getChildren()) {
            if (psiElement instanceof ArrayHashElement) {
                ArrayHashElement hashElement = (ArrayHashElement) psiElement;
                String key = PsiUtils.getContent(hashElement.getKey());
                if (key == null) {
                    throw new IllegalStateException("Unsupported key element: " + hashElement.getKey().getClass());
                }
                keyParts.add(key);

                if (PlatformPatterns.psiElement(PhpElementTypes.ARRAY_CREATION_EXPRESSION).accepts(hashElement.getValue())) {
                    parse(hashElement.getValue(), keyParts, handler);
                } else {
                    String text = PsiUtils.getContent(hashElement.getValue());
                    handler.handle(Joiner.on('.').join(keyParts), text, hashElement);
                }
                keyParts.remove(keyParts.size() - 1);
            }
        }
    }
}
