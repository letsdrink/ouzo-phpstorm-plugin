package com.github.letsdrink.intellijplugin.translation;


import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;

import java.util.Collection;
import java.util.LinkedList;

import static com.github.letsdrink.intellijplugin.PsiUtils.getContent;

public class TranslationHashElement {
    private final ArrayHashElement hashElement;

    public TranslationHashElement(ArrayHashElement hashElement) {
        this.hashElement = hashElement;
    }

    public static TranslationHashElement newInstance(PsiElement element) {
        ArrayHashElement hashElement;
        if (element instanceof ArrayHashElement) {
            hashElement = (ArrayHashElement) element;
        } else {
            hashElement = PsiTreeUtil.getParentOfType(element, ArrayHashElement.class);
        }
        return new TranslationHashElement(hashElement);
    }

    public Collection<String> getChildrenFullKeys() {
        LinkedList<String> children = new LinkedList<String>();
        getChildrenFullKeys("", hashElement, children);
        return children;
    }

    private void getChildrenFullKeys(String parentKey, ArrayHashElement element, Collection<String> children) {
        if (!parentKey.isEmpty()) {
            parentKey = parentKey + ".";
        }
        String fullKey = parentKey + getKey(element);

        if (is(element.getValue(), PhpElementTypes.ARRAY_CREATION_EXPRESSION)) {
            for (PsiElement psiElement : element.getValue().getChildren()) {
                getChildrenFullKeys(fullKey, (ArrayHashElement) psiElement, children);
            }
        } else if (!parentKey.isEmpty()) {
            children.add(fullKey);
        }
    }

    public String getFullKey() {
        StringBuilder sb = new StringBuilder();
        getFullKey(hashElement, sb);
        return sb.toString();
    }

    private void getFullKey(ArrayHashElement element, StringBuilder sb) {
        if (is(element.getParent(), PhpElementTypes.ARRAY_CREATION_EXPRESSION) && is(element.getParent().getParent().getParent(), PhpElementTypes.HASH_ARRAY_ELEMENT)) {
            getFullKey((ArrayHashElement) element.getParent().getParent().getParent(), sb);
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(getKey(element));
        }
    }

    private String getKey(ArrayHashElement element) {
        return getContent(element.getKey());
    }

    private static boolean is(PsiElement element, IElementType type) {
        return PlatformPatterns.psiElement(type).accepts(element);
    }
}
