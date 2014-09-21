package com.github.letsdrink.intellijplugin;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

import javax.annotation.Nullable;
import java.util.*;

public class TranslationParser {
    private final PsiFile psiFile;

    public TranslationParser(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    public static Function<PsiFile, TranslationParser> createParser() {
        return new Function<PsiFile, TranslationParser>() {
            @Nullable
            @Override
            public TranslationParser apply(@Nullable PsiFile psiFile) {
                return  new TranslationParser(psiFile);
            }
        };
    }

    public static Function<TranslationParser, String> getTextFunction(final String text) {
        return new Function<TranslationParser, String>() {
            @Nullable
            @Override
            public String apply(@Nullable TranslationParser translationParser) {
                return translationParser.getKey(text);
            }
        };
    }

    public String getKey(String translation) {
        Collection<ArrayHashElement> hashElements = PsiTreeUtil.collectElementsOfType(psiFile, ArrayHashElement.class);
        for (ArrayHashElement hashElement : hashElements) {
            PhpPsiElement value = hashElement.getValue();
            if (PlatformPatterns.psiElement(PhpElementTypes.STRING).accepts(value)) {
                if (translation.equals(getContent(value))) {
                    return getKey(hashElement);
                }
            }
        }
        return null;
    }

    private String getContent(PhpPsiElement value) {
        return ((StringLiteralExpression) value).getContents();
    }

    private String getKey(ArrayHashElement hashElement) {
        LinkedList<String> keys = new LinkedList<String>();
        keys.add(getContent(hashElement.getKey()));

        ArrayHashElement element = hashElement;

        while (is(element.getParent(), PhpElementTypes.ARRAY_CREATION_EXPRESSION) && is(element.getParent().getParent().getParent(), PhpElementTypes.HASH_ARRAY_ELEMENT)) {
            element = (ArrayHashElement) element.getParent().getParent().getParent();
            keys.addFirst(getContent(element.getKey()));
        }
        return Joiner.on(".").join(keys);
    }

    private boolean is(PsiElement element, IElementType type) {
        return PlatformPatterns.psiElement(type).accepts(element);
    }
}
