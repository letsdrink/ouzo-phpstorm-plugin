package com.github.letsdrink.intellijplugin.translation;


import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.twig.TwigLanguage;

public class TranslationContentCreator {
    private final ElementTypeResolver typeResolver;

    public TranslationContentCreator(ElementTypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public String buildTranslation(String key, PsiElement element) {
        Language language = typeResolver.getFileLanguage(element);
        if (PhpLanguage.INSTANCE.equals(language)) {
            return buildPhpTranslation(key, element);
        }
        if (TwigLanguage.INSTANCE.equals(language)) {
            return buildTwigTranslation(key, element);
        }
        return buildUniversalTranslation(key);
    }

    private String buildUniversalTranslation(String key) {
        return "t('" + key + "')";
    }

    private String buildTwigTranslation(String key, PsiElement element) {
        String insertString = buildUniversalTranslation(key);
        if (!typeResolver.isTwigLiteral(element)) {
            insertString = "{{ " + insertString + " }}";
        }
        return insertString;
    }

    private String buildPhpTranslation(String key, PsiElement element) {
        String insertString = buildUniversalTranslation(key);
        if (!typeResolver.isInView(element)) {
            insertString = "I18n::" + insertString;
        }
        if (!typeResolver.isPhpString(element)) {
            insertString = "<?= " + insertString + "?>";
        }
        if (typeResolver.isJsLiteral(element)) {
            insertString = "'" + insertString + "'";
        }
        return insertString;
    }
}
