package com.github.letsdrink.intellijplugin.translation;


import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;

import java.util.ArrayList;
import java.util.List;

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

    public String getFullKey() {
        StringBuilder sb = new StringBuilder();
        getFullKey(hashElement, sb);
        return sb.toString();
    }

    private void getFullKey(ArrayHashElement element, StringBuilder sb) {
        if (is(element.getParent(), PhpElementTypes.ARRAY_CREATION_EXPRESSION) && is(element.getParent().getParent().getParent(), PhpElementTypes.HASH_ARRAY_ELEMENT)) {
            getFullKey((ArrayHashElement) element.getParent().getParent().getParent(), sb);
        }
        if (sb.length() > 0) {
            sb.append('.');
        }
        sb.append(getKey(element));
    }

    private String getKey(ArrayHashElement element) {
        return getContent(element.getKey());
    }

    private static boolean is(PsiElement element, IElementType type) {
        return PlatformPatterns.psiElement(type).accepts(element);
    }

    public ArrayHashElement getHashElement() {
        return hashElement;
    }

    public List<ArrayHashElement> getChildrenHashElements() {
        final List<ArrayHashElement> children = new ArrayList<>();
        if (PlatformPatterns.psiElement(PhpElementTypes.ARRAY_CREATION_EXPRESSION).accepts(hashElement.getValue())) {
            TranslationParser translationParser = new TranslationParser();
            translationParser.parse(hashElement.getValue(), new TranslationParser.Handler() {
                @Override
                public void handle(String key, String text, ArrayHashElement element) {
                    children.add(element);
                }
            });
        }
        return children;
    }
}
