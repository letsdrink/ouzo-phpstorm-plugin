package com.github.letsdrink.intellijplugin;

import com.google.common.base.*;
import com.google.common.collect.Iterables;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import com.jetbrains.php.lang.psi.elements.PhpReturn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.github.letsdrink.intellijplugin.PsiUtils.getContent;
import static java.util.Arrays.asList;

public class TranslationParser {
    private final PsiFile psiFile;
    private final TranslationCodeBuilder translationCodeBuilder = new TranslationCodeBuilder();

    public TranslationParser(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    public static Function<PsiFile, TranslationParser> createParser() {
        return new Function<PsiFile, TranslationParser>() {
            @Nullable
            @Override
            public TranslationParser apply(@Nullable PsiFile psiFile) {
                return new TranslationParser(psiFile);
            }
        };
    }

    public static Predicate<? super TranslationParser> languageEqualsFunction(final String language) {
        return new Predicate<TranslationParser>() {
            @Nullable
            @Override
            public boolean apply(@Nullable TranslationParser parser) {
                return language.equals(parser.getLanguage());
            }
        };
    }

    public static Function<TranslationParser, String> getKeyFunction(final String text) {
        return new Function<TranslationParser, String>() {
            @Nullable
            @Override
            public String apply(@Nullable TranslationParser translationParser) {
                return translationParser.getKey(text);
            }
        };
    }

    public static Function<TranslationParser, ArrayHashElement> getTranslationElementFunction(final String key) {
        return new Function<TranslationParser, ArrayHashElement>() {
            @Nullable
            @Override
            public ArrayHashElement apply(@Nullable TranslationParser translationParser) {
                return translationParser.getTranslationElement(key);
            }
        };
    }

    public String getLanguage() {
        return psiFile.getName().replaceFirst(".php", "");
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

    public ArrayHashElement getTranslationElement(String key) {
        List<String> keys = Splitter.on(".").splitToList(key);
        List<String> missingKeys = new ArrayList<String>();
        PsiElement translationArray = findDestinationArrayParent(keys, missingKeys);

        if (missingKeys.isEmpty()) {
            Optional<PsiElement> elementOptional = Iterables.tryFind(asList(translationArray.getChildren()), hasArrayElementKey(Iterables.getLast(keys)));
            return (ArrayHashElement) elementOptional.orNull();
        }
        return null;
    }


    public String getTranslation(String key) {
        ArrayHashElement translationElement = getTranslationElement(key);
        return translationElement != null ? getContent(translationElement.getValue()) : null;
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

    /*
    Translation key can
    - be missing in existing nested key
    - already exist or
    - be missing with missing nested keys
    - be an array (incomplete key)
     */
    public void addTranslation(String key, String text) {
        List<String> keys = Splitter.on(".").splitToList(key);
        List<String> missingKeys = new ArrayList<String>();

        PsiElement translationArray = findDestinationArrayParent(keys, missingKeys);

        Optional<PsiElement> elementOptional = Iterables.tryFind(asList(translationArray.getChildren()), hasArrayElementKey(Iterables.getLast(keys)));
        if (elementOptional.isPresent() && missingKeys.isEmpty()) {
            ArrayHashElement hashElement = (ArrayHashElement) elementOptional.get();
            overwrite(hashElement.getValue(), text);
        } else {
            String insertionText = translationCodeBuilder.getInsertionText(text, keys, missingKeys, translationArray.getChildren().length > 0);
            insert(getInsertionPosition(translationArray), insertionText);
        }
    }

    private PsiElement findDestinationArrayParent(List<String> keys, List<String> missingKeys) {
        Collection<PhpReturn> phpReturns = PsiTreeUtil.collectElementsOfType(psiFile, PhpReturn.class);
        PhpReturn phpReturn = Iterables.getOnlyElement(phpReturns);
        PsiElement root = phpReturn.getFirstPsiChild();


        Iterable<String> prefixKeys = Iterables.limit(keys, keys.size() - 1);
        Iterables.addAll(missingKeys, prefixKeys);
        for (final String keyPart : prefixKeys) {
            Optional<PsiElement> elementOptional = Iterables.tryFind(asList(root.getChildren()), hasArrayElementKey(keyPart));
            if (elementOptional.isPresent()) {
                ArrayHashElement hashElement = (ArrayHashElement) elementOptional.get();
                root = hashElement.getValue();
                missingKeys.remove(0);
            } else {
                return root;
            }
        }
        return root;
    }

    private int getInsertionPosition(PsiElement translations) {
        if (translations.getChildren().length > 0) {
            return translations.getFirstChild().getTextRange().getEndOffset() + 1;
        }
        return translations.getTextRange().getEndOffset() - 1;
    }

    private Predicate<PsiElement> hasArrayElementKey(final String keyPart) {
        return new Predicate<PsiElement>() {
            @Override
            public boolean apply(@Nullable PsiElement element) {
                ArrayHashElement hashElement = (ArrayHashElement) element;
                PsiElement stringElement = hashElement.getKey();
                return keyPart.equals(getContent(stringElement));
            }
        };
    }

    public void insert(final int position, final String text) {
        Project project = psiFile.getProject();
        final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        final Document document = manager.getDocument(psiFile);

        new WriteCommandAction(project) {
            @Override
            protected void run(Result result) throws Throwable {
                document.insertString(position, text);
                manager.doPostponedOperationsAndUnblockDocument(document);
                manager.commitDocument(document);
            }

            @Override
            public String getGroupID() {
                return "Translation Extraction";
            }
        }.execute();
    }

    private void overwrite(final PhpPsiElement value, final String text) {
        Project project = psiFile.getProject();
        final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        final Document document = manager.getDocument(psiFile);

        new WriteCommandAction(project) {
            @Override
            protected void run(Result result) throws Throwable {
                document.deleteString(value.getTextRange().getStartOffset(), value.getTextRange().getEndOffset());
                document.insertString(value.getTextRange().getStartOffset(), "'" + text + "'");
                manager.doPostponedOperationsAndUnblockDocument(document);
                manager.commitDocument(document);
            }

            @Override
            public String getGroupID() {
                return "Translation Extraction";
            }
        }.execute();
    }
}
