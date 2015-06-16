package com.github.letsdrink.intellijplugin.translation;


import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpLanguage;

public class TranslationContentCreator {
    private final ElementTypeResolver typeResolver;

    public TranslationContentCreator(ElementTypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public String buildTranslation(String key, PsiElement element) {
        Language language = element.getContainingFile().getLanguage();
        if (PhpLanguage.INSTANCE.equals(language)) {
            return buildPhpTranslation(key, element);
        }
        return "t('" + key + "')";
    }

    private String buildPhpTranslation(String key, PsiElement element) {
        String insertString = "t('" + key + "')";
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
