package com.github.letsdrink.intellijplugin.index;

import com.github.letsdrink.intellijplugin.PsiUtils;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileContent;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.BinaryExpression;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


class TranslationCallDataIndexer implements DataIndexer<String, Void, FileContent> {
    @NotNull
    @Override
    public Map<String, Void> map(@NotNull FileContent fileContent) {
        Map<String, Void> map = new HashMap<String, Void>();

        PsiFile psiFile = fileContent.getPsiFile();
        Collection<FunctionReference> calls = PsiTreeUtil.collectElementsOfType(psiFile, FunctionReference.class);
        for (FunctionReference call : calls) {
            if (call.getParameters().length > 0 && (call.getName().equals("t") || call.getText().startsWith("I18n::labels"))) {
                addKey(map, call.getParameters()[0]);
            }
        }
        return map;
    }

    private void addKey(Map<String, Void> map, PsiElement parameter) {
        String key = PsiUtils.getContent(parameter);
        if (key != null) {
            map.put(key, null);
        } else if (PlatformPatterns.psiElement(PhpElementTypes.CONCATENATION_EXPRESSION).accepts(parameter)) {
            BinaryExpression binaryExpression = (BinaryExpression) parameter;
            addKey(map, binaryExpression.getLeftOperand());
        }
    }
}
